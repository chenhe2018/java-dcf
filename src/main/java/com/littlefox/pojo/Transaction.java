package com.littlefox.pojo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Represents a transaction for the purposes of computing the irregular rate
 * of return.
 * <p>
 * Note that negative amounts represent deposits into the investment (and so
 * withdrawals from your cash).  Positive amounts represent withdrawals from the
 * investment (deposits into cash).  Zero amounts are allowed in case your
 * investment is now worthless.
 */
public class Transaction {

    public final double amount;
    public final LocalDate when;
    public double ty;//计息基准

    /**
     * Construct a Transaction instance with the given amount at the given day.
     * @param amount the amount transferred
     * @param when the day the transaction took place
     */
    public Transaction(double amount, LocalDate when) {
        this.amount = amount;
        this.when = when;
        this.ty = 365.0;//计息基准默认365
    }

    /**
     * Construct a Transaction instance with the given amount at the given day.
     * @param amount the amount transferred
     * @param when the day the transaction took place
     */
    public Transaction(double amount, Date when) {
        this.amount = amount;
        this.when = LocalDate.from(when.toInstant().atZone(ZoneId.systemDefault()));
        this.ty = 365.0;//计息基准默认365
    }

    /**
     * Construct a Transaction instance with the given amount at the given day.
     * @param amount the amount transferred
     * @param when the day the transaction took place, see 
     *             {@link LocalDate#parse(CharSequence) }
     *             for the format
     */
    public Transaction(double amount, String when) {
        this.amount = amount;
        this.when = LocalDate.parse(when);
        this.ty = 365.0;//计息基准默认365
    }

    public Transaction(double amount) {
        this.amount = amount;
        this.when = null;
        this.ty = 365.0;//计息基准默认365
    }

    public Transaction(double amount, String when, double ty) {
        this.amount = amount;
        this.when = LocalDate.parse(when);
        this.ty = ty;
    }

    /**
     * The amount transferred in this transaction.
     * @return amount transferred in this transaction
     */
    public double getAmount() {
        return amount;
    }

    /**
     * The day the transaction took place.
     * @return day the transaction took place
     */
    public LocalDate getWhen() {
        return when;
    }

    public double getTy() {
        return ty;
    }

    public void setTy(double ty){
        this.ty = ty;
    }
}
