package com.littlefox.dcf;

import com.littlefox.arith.NewtonRaphson;
import com.littlefox.arith.NonconvergenceException;
import com.littlefox.arith.ZeroValuedDerivativeException;
import com.littlefox.pojo.Transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Calculates the irregular rate of return on a series of transactions.  The
 * irregular rate of return is the constant rate for which, if the transactions
 * had been applied to an investment with that rate, the same resulting returns
 * would be realized.
 * <p>
 * When creating the list of {@link Transaction} instances to feed Dcf, be
 * sure to include one transaction representing the present value of the account
 * now, as if you had cashed out the investment.
 * <p>
 * Example usage:
 * <code>
 *     double rate = new Dcf(
 *             new Transaction(-1000, "2016-01-15"),
 *             new Transaction(-2500, "2016-02-08"),
 *             new Transaction(-1000, "2016-04-17"),
 *             new Transaction( 5050, "2016-08-24")
 *         ).dcf();
 * </code>
 * <p>
 * Example using the builder to gain more control:
 * <code>
 *     double rate = Dcf.builder()
 *         .withNewtonRaphsonBuilder(
 *             NewtonRaphson.builder()
 *                 .withIterations(1000)
 *                 .withTolerance(0.0001))
 *         .withGuess(.20)
 *         .withTransactions(
 *             new Transaction(-1000, "2016-01-15"),
 *             new Transaction(-2500, "2016-02-08"),
 *             new Transaction(-1000, "2016-04-17"),
 *             new Transaction( 5050, "2016-08-24")
 *         ).dcf();
 * </code>
 * <p>
 * This class is not thread-safe and is designed for each instance to be used
 * once.
 */
public class Dcf {

    /**
     * Convenience method for getting an instance of a {@link Builder}.
     * @return new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private final List<Investment> investments;
    private final DcfDetails details;

    private NewtonRaphson.Builder builder = null;
    private Double guess = null;

    /**
     * Construct an Dcf instance for the given transactions.
     * @param tx the transactions
     * @throws IllegalArgumentException if there are fewer than 2 transactions
     * @throws IllegalArgumentException if all the transactions are on the same date
     * @throws IllegalArgumentException if all the transactions negative (deposits)
     * @throws IllegalArgumentException if all the transactions non-negative (withdrawals)
     */
    public Dcf(double guess, Transaction... tx) {
        this(Arrays.asList(tx), guess);
    }

    /**
     * Construct an Dcf instance for the given transactions.
     * @param txs the transactions
     * @throws IllegalArgumentException if there are fewer than 2 transactions
     * @throws IllegalArgumentException if all the transactions are on the same date
     * @throws IllegalArgumentException if all the transactions negative (deposits)
     * @throws IllegalArgumentException if all the transactions non-negative (withdrawals)
     */
    public Dcf(Collection<Transaction> txs, double guess) {
        this(txs, null, guess);
    }

    private Dcf(Collection<Transaction> txs, NewtonRaphson.Builder builder, Double guess) {
        if (txs.size() < 2) {
            throw new IllegalArgumentException(
                    "Must have at least two transactions");
        }
        details = txs.stream().collect(DcfDetails.collector());
        details.validate();
        investments = txs.stream()
                .map(this::createInvestment)
                .collect(Collectors.toList());

        this.builder = builder != null ? builder : NewtonRaphson.builder();
        this.guess = guess;
    }

    private Investment createInvestment(Transaction tx) {
        // Transform the transaction into an Investment instance
        // It is much easier to calculate the present value of an Investment
        final Investment result = new Investment();
        result.amount = tx.amount;
        // Don't use YEARS.between() as it returns whole numbers
        //注意：dcf年数为开始日期与当前日期的查值，不是当前日期与结束日志的差值
        result.years = DAYS.between(details.start,tx.when) / tx.ty;
        return result;
    }

    /**
     * Calculates the present value of the investment if it had been subject to
     * the given rate of return.
     * @param rate the rate of return
     * @return the present value of the investment if it had been subject to the
     *         given rate of return
     */
    public double presentValue(final double rate) {
        return investments.stream()
                .mapToDouble(inv -> inv.presentValue(rate))
                .sum();
    }

    /**
     * The derivative of the present value under the given rate.
     * @param rate the rate of return
     * @return derivative of the present value under the given rate
     */
    public double derivative(final double rate) {
        return investments.stream()
                .mapToDouble(inv -> inv.derivative(rate))
                .sum();
    }

    /**
     * Calculates the irregular rate of return of the transactions for this
     * instance of Dcf.
     * @return the irregular rate of return of the transactions
     * @throws ZeroValuedDerivativeException if the derivative is 0 while executing the Newton-Raphson method
     * @throws NonconvergenceException if the Newton-Raphson method fails to converge in the
     */
    public double dcf() {
        final double years = DAYS.between(details.start, details.end) / details.days;
        if (details.maxAmount == 0) {
            return -1; // Total loss
        }
        guess = guess != null ? guess : (details.total / details.deposits) / years;
        return builder.withFunction(this::presentValue)
                .withDerivative(this::derivative)
                .findRoot(guess);
    }

    /**
     * 给定到期收益的情况下，计算收益率
     * @param target
     * @return
     */
    public double dcf(double target) {
        final double years = DAYS.between(details.start, details.end) / details.days;
        if (details.maxAmount == 0) {
            return -1; // Total loss
        }
        guess = guess != null ? guess : (details.total / details.deposits) / years;
        return builder.withFunction(this::presentValue)
                .withDerivative(this::derivative)
                .findRoot(guess,target);
    }

    /**
     * Convenience class which represents {@link Transaction} instances more
     * conveniently for our purposes.
     */
    public static class Investment {
        /** The amount of the investment. */
        double amount;
        /** The number of years for which the investment applies, including
         * fractional years. */
        double years;

        /**
         * Present value of the investment at the given rate.
         * @param rate the rate of return
         * @return present value of the investment at the given rate
         */
        private double presentValue(final double rate) {
            if (-1 < rate) {
                return  amount / Math.pow(1 + rate, years);
            } else if (rate < -1) {
                // Extend the function into the range where the rate is less
                // than -100%.  Even though this does not make practical sense,
                // it allows the algorithm to converge in the cases where the
                // candidate values enter this range

                // We cannot use the same formula as before, since the base of
                // the exponent (1+rate) is negative, this yields imaginary
                // values for fractional years.
                // E.g. if rate=-1.5 and years=.5, it would be (-.5)^.5,
                // i.e. the square root of negative one half.

                // Ensure the values are always negative so there can never
                // be a zero (as long as some amount is non-zero).
                // This formula also ensures that the derivative is positive
                // (when rate < -1) so that Newton's method is encouraged to
                // move the candidate values towards the proper range

                return -Math.abs(amount) / Math.pow(-1 - rate, years);
            } else if (years == 0) {
                return amount; // Resolve 0^0 as 0
            } else {
                return 0;
            }
        }

        /**
         * Derivative of the present value of the investment at the given rate.
         * @param rate the rate of return
         * @return derivative of the present value at the given rate
         */
        private double derivative(final double rate) {
            if (years == 0) {
                return 0;
            } else if (-1 < rate) {
                double v = amount * (-years) * Math.pow(1 + rate, - years - 1);
//                System.out.println(rate+"\t"+v);
                return v;
            } else if (rate < -1) {
                double v = Math.abs(amount) * (-years) * Math.pow(-1 - rate, - years - 1);
//                System.out.println(rate+"\t"+v);
                return v;
            } else {
                return 0;
            }
        }
    }

    /**
     * Builder for {@link Dcf} instances.
     */
    public static class Builder {
        private Collection<Transaction> transactions = null;
        private NewtonRaphson.Builder builder = null;
        private Double guess = null;
        private double days = 0;

        public Builder() {
        }

        public Builder withTransactions(Transaction... txs) {
            return withTransactions(Arrays.asList(txs));
        }

        public Builder withTransactions(Collection<Transaction> txs) {
            this.transactions = txs;
            return this;
        }

        public Builder withNewtonRaphsonBuilder(NewtonRaphson.Builder builder) {
            this.builder = builder;
            return this;
        }

        public Builder withGuess(double guess) {
            this.guess = guess;
            return this;
        }

        public Builder withDasy(double days) {
            this.days = days;
            return this;
        }

        public Dcf build() {
            return new Dcf(transactions, builder, guess);
        }

        /**
         * Convenience method for building the Dcf instance and invoking
         * {@link Dcf#dcf()}.  See the documentation for that method for
         * details.
         * @return the irregular rate of return of the transactions
         */
        public double dcf() {
            return build().dcf();
        }
    }

}
