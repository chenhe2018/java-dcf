package com.littlefox.dcf;

import com.littlefox.arith.ArithmeticUtils;
import com.littlefox.arith.NewtonRaphson;
import com.littlefox.pojo.Transaction;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * discounted cash flow
 * 现金流贴现算法
 */
public class DCFUtil {

    /**
     * 默认猜测值
     */
    private static final double X0 = 0.6D;

//    private final List<Investment> investments;

    public static double dcf_yld2prc(List<Transaction> list, LocalDate startDay, double yld) {

        double allAmount = 0.0;
        for (int i = 0; i < list.size(); i++) {
            Transaction transaction = list.get(i);

            double amount = transaction.getAmount();
            double rate = 1 + yld;
            double years = ArithmeticUtils.div(Double.valueOf(DAYS.between(startDay, list.get(i).getWhen())), list.get(i).getTy());
//            System.out.println("power:"+power);
            double cashdiscounting = ArithmeticUtils.div(amount, Math.pow(rate, years));
//            System.out.println("现金流贴现计算["+yld+"]："+amount+"=>"+cashdiscounting);
            allAmount = ArithmeticUtils.sub(allAmount, cashdiscounting);
//            System.out.println(transaction.getWhen()+"\t"+yld+"=>"+amount+":"+cashdiscounting);
        }
        return allAmount;
    }

    /**
     * 根据预设收益率计算全价，yld2prc
     * @param list
     * @param yld 取值需大于-1（即完全亏完）
     * @return
     */
    public static double dcf_yld2prc(List<Transaction> list, double yld) {

        double allAmount = 0.0;
        for (int i = 0; i < list.size(); i++) {
            Transaction transaction = list.get(i);
//            double amount = transaction.getAmount();
//            double rate = 1 + yld;
//            double years = ArithmeticUtils.div(Double.valueOf(DAYS.between(list.get(0).getWhen(), list.get(i).getWhen())), list.get(i).getTy());
//            double cashdiscounting = ArithmeticUtils.div(amount, Math.pow(rate, years));
//            allAmount = ArithmeticUtils.add(allAmount, cashdiscounting);
////            System.out.println("现金流贴现计算["+yld+"]："+amount+"=>"+cashdiscounting);

            double years = Double.valueOf(DAYS.between(list.get(0).getWhen(), list.get(i).getWhen()))/list.get(i).getTy();
            allAmount += transaction.getAmount()/Math.pow(1+yld,years);
        }
        return allAmount;
    }

    /**
     * 牛顿拉夫森算法，prc2yld
     * @param list
     * @param prc
     * @return
     */
    public static double dcf_prc2yld(List<Transaction> list, double prc) {
        return new Dcf(list, X0).dcf(prc);
    }


}