package com.littlefox.xirr;

import com.littlefox.pojo.Transaction;

import java.util.List;

public class XIRRUtil {

    /**
     * 默认猜测值
     */
    private static final double X0 = 0.5D;

    /**
     *  计算 XIRR
     *  【参考列表】
     *  IRR Excel:https://support.office.com/zh-cn/article/IRR-%E5%87%BD%E6%95%B0-64925eaa-9988-495b-b290-3ad0c163c1bc
     *  XIRR Excel:https://support.office.com/zh-cn/article/XIRR-%E5%87%BD%E6%95%B0-de1242ec-6477-445b-b11b-a303ad9adc9d
     * @param list 现金流
     * @return
     */
    public static Double xirr(List<Transaction> list) {
        return new Xirr(list,X0).xirr();
    }

//    /**
//     * XIRR
//     * @param list 现金流
//     * @param guess 猜测值
//     * @return
//     */
//    public static Double xirr(List<Transaction> list, double guess) {
//        return new Xirr(list,guess).xirr();
//    }


    /**
     * XIRR 给定到期收益（即去除本金的到期全价），推出收益率
     * @param list
     * @param expireInterest 到期收益利息值
     * @return
     */
    public static Double xirr(List<Transaction> list, double expireInterest) {
        return new Xirr(list,X0).xirr(expireInterest);
    }

}
