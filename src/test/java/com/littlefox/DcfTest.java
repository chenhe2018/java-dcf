package com.littlefox;

import com.littlefox.arith.NonconvergenceException;
import com.littlefox.arith.ZeroValuedDerivativeException;
import com.littlefox.dcf.DCFUtil;
import com.littlefox.pojo.Transaction;
import com.littlefox.xirr.XIRRUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.littlefox.FileUtils.bufferedWriterMethod;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Unit test for simple IRR.
 */
public class DcfTest {

    public long startTime = 0;

    @Before
    public void before() {
        startTime = System.currentTimeMillis();
    }

    @After
    public void after() {
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) + "ms");
    }


    @Test
    public void coupon57() throws IOException {

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2025-11-25");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100, "2022-11-29"),
                new Transaction(5.7, "2023-11-25"),
                new Transaction(5.7, "2024-11-25"),
                new Transaction(105.7, "2025-11-25")
        );

//        Double xirr = XIRRUtil.xirr(list);
//        System.out.println("xirr:" + xirr);

//        for (int i = -10; i < 10; i++) {
//            double yld = 0.000_001 * i + xirr;
//            double prc = DCFUtil.dcf_yld2prc(list, yld);
//            System.out.println("yld2prc：\t\t\t\t" + yld + "->" + prc);
//            System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, prc));
//        }

        double yld = -0.205000;
        double prc = DCFUtil.dcf_yld2prc(list, yld);
        System.out.println("yld2prc："+yld+"->"+prc);
        System.out.println("********************************");
        System.out.println("********************************");
        System.out.println("********************************");
        double[] x0s = new double[]{-0.9,-0.8,-0.7,-0.6,-0.5,-0.4,-0.3,-0.2,-0.1,0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
        for(double x0:x0s){
            try {
                System.out.println("********************************");
                System.out.printf("====>牛顿拉夫森算法反推prc2yld[%s]："+DCFUtil.dcf_prc2yld(list,prc,x0),x0);
                System.out.println();
                System.out.println("********************************");
                break;
            }catch (Exception e){
                continue;
            }
        }


//        System.out.println("牛顿拉夫森算法反推prc2yld："+DCFUtil.dcf_prc2yld(list,prc,-0.5));
//        System.out.println("********************************");
//        System.out.println("********************************");
//        System.out.println("********************************");
//        System.out.println("牛顿拉夫森算法反推prc2yld："+DCFUtil.dcf_prc2yld(list,prc,0.5));



//        DecimalFormat df = new DecimalFormat("#0.000000");
//        for (double i = -1+0.001; i < 1; i += 0.001) {
//                double yld = i;
//                double prc = DCFUtil.dcf_yld2prc(list, yld);
//                System.out.println("yld2prc：\t\t\t\t" + yld + "->" + prc);
//
//            try {
//                long start = System.currentTimeMillis();
//                double candidate_yld = DCFUtil.dcf_prc2yld(list, prc);
//                long end = System.currentTimeMillis();
//                System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, prc,-0.5));
//                bufferedWriterMethod("./dcf.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + df.format(candidate_yld) + "\t" + (end - start) + "\n");
//            } catch (ZeroValuedDerivativeException e) {
//                bufferedWriterMethod("./dcf.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + "0" + "\t" + "0" + "\n");
//                continue;
//            } catch (NonconvergenceException e) {
//                bufferedWriterMethod("./dcf.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + "99999" + "\t" + "0" + "\n");
//                continue;
//            }
//        }
//        System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, DCFUtil.dcf_yld2prc(list, 0.000_101)));

    }

    @Test
    public void coupon57_x0_multiple() throws IOException {

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2025-11-25");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100, "2022-11-29"),
                new Transaction(5.7, "2023-11-25"),
                new Transaction(5.7, "2024-11-25"),
                new Transaction(105.7, "2025-11-25")
        );


        DecimalFormat df = new DecimalFormat("#0.000000");
        for (double i = -1+0.001; i < 1; i += 0.001) {
                double yld = i;
                double prc = DCFUtil.dcf_yld2prc(list, yld);
                System.out.println("yld2prc：\t\t\t\t" + yld + "->" + prc);

            double target_x0 = 0.0;
            double candidate_yld = 0.0;
            try {
                double[] x0s = new double[]{-1,-0.9,-0.8,-0.7,-0.6,-0.5,-0.4,-0.3,-0.2,-0.1,0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1};
                long start = System.currentTimeMillis();
                for(double x0:x0s){
                    try {
                        candidate_yld = DCFUtil.dcf_prc2yld(list,prc,x0);
                        System.out.printf("====>牛顿拉夫森算法反推prc2yld[%s]："+candidate_yld+"\n",x0);
                        target_x0 = x0;
                        break;
                    }catch (Exception e){
                        continue;
                    }
                }
                long end = System.currentTimeMillis();
                bufferedWriterMethod("./dcf-x0.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + df.format(candidate_yld) + "\t" + target_x0 + "\n");
            } catch (ZeroValuedDerivativeException e) {
                bufferedWriterMethod("./dcf-x0.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + "0" + "\t" + target_x0 + "\n");
                continue;
            } catch (NonconvergenceException e) {
                bufferedWriterMethod("./dcf-x0.txt", df.format(yld) + "\t" + df.format(prc) + "\t" + "99999" + "\t" + target_x0 + "\n");
                continue;
            }
        }
        System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, DCFUtil.dcf_yld2prc(list, 0.000_101)));

    }


    @Test
    public void my_XIRR() throws IOException {

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2029-11-17");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100, "2022-11-29", 365.0),
                new Transaction(5.7, "2023-11-25", 365.0),
                new Transaction(5.7, "2024-11-25", 365.0),
                new Transaction(105.7, "2025-11-25", 365.0),

                new Transaction(-100, "2022-11-29", 365.0),
                new Transaction(3.85, "2023-11-17", 365.0),
                new Transaction(3.85, "2024-11-17", 365.0),
                new Transaction(3.85, "2025-11-17", 365.0),
                new Transaction(3.85, "2026-11-17", 365.0),
                new Transaction(3.85, "2027-11-17", 365.0),
                new Transaction(3.85, "2028-11-17", 365.0),
                new Transaction(103.85, "2029-11-17", 365.0),


                new Transaction(-100, "2022-11-29", 360.0),
                new Transaction(2.85, "2023-11-15", 360.0),
                new Transaction(2.85, "2024-11-15", 360.0),
                new Transaction(2.85, "2025-11-15", 360.0),
                new Transaction(2.85, "2026-11-15", 360.0),
                new Transaction(102.85, "2027-11-15", 360.0)
        );
        list.sort(Comparator.comparing(Transaction::getWhen));


        double [] ylds ={0.039036,0.039037,0.039038,0.039039,0.039040,0.039041};

//        long s, e1, e2;
//        for (double yld : ylds) {
//            s = System.currentTimeMillis();
//            double prc = DCFUtil.dcf_yld2prc(list, yld);
//            e1 = System.currentTimeMillis();
//            double v = DCFUtil.dcf_prc2yld(list, prc);
//            e2 = System.currentTimeMillis();
//            System.out.println();
//            System.out.println("yld2prc：\t\t\t\t" + yld + "->" + (300 + prc) + "\t" + (e1 - s) + "ms");
//            System.out.println("牛顿拉夫森算法反推prc2yld：\t" + v + "\t" + (e2 - e1) + "ms");
//        }

        double v = DCFUtil.dcf_prc2yld(list, 0);

//        double yld1 = 0.039038;
//        double prc1 = DCFUtil.dcf_yld2prc(list, yld1);
//        System.out.println();
//        System.out.println("yld2prc：\t\t\t\t"+yld1+"->"+prc1);
//        System.out.println("牛顿拉夫森算法反推prc2yld：\t"+DCFUtil.dcf_prc2yld(list,prc1));


//        double xirr = 0.039038;
//        for(int i=-5;i<5;i++){
//            double yld = 0.0001*i+xirr;
//            double prc = DCFUtil.dcf_yld2prc(list, yld);
//            System.out.println();
//            System.out.println("yld2prc：\t\t\t\t"+yld+"->"+prc);
//            System.out.println("牛顿拉夫森算法反推prc2yld：\t"+DCFUtil.dcf_prc2yld(list,prc));
//        }

//        DecimalFormat df = new DecimalFormat("#0.00000000");
//        for (double i = 0.000_001; i < 1; i += 0.000_001) {
//                double yld = i;
//                double prc = DCFUtil.dcf_yld2prc(list, yld);
//                System.out.println("yld2prc：\t\t\t\t" + yld + "->" + prc);
//
//            try {
//                long start = System.currentTimeMillis();
//                double candidate_yld = DCFUtil.dcf_prc2yld(list, prc);
//                long end = System.currentTimeMillis();
//                System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, prc));
//                bufferedWriterMethod("./dcf.txt", df.format(yld) + "\t" + prc + "\t" + candidate_yld + "\t" + (end - start) + "\n");
//            } catch (ZeroValuedDerivativeException e) {
//                bufferedWriterMethod("./dcf.txt", df.format(yld) + "\t" + prc + "\t" + "ZeroValuedDerivativeException" + "\t" + "\n");
//                continue;
//            }
//        }
//        System.out.println("牛顿拉夫森算法反推prc2yld：\t" + DCFUtil.dcf_prc2yld(list, DCFUtil.dcf_yld2prc(list, 0.000_101)));

    }


    @Test
    public void basketTest() throws IOException {
        List<Transaction> list = new ArrayList();
        List<String[]> basket = new ArrayList<>();
//        basket.add(new String[]{"365","2022-11-25","5.7","1","3"});
//        basket.add(new String[]{"365","2022-11-17","3.85","1","7"});
//        basket.add(new String[]{"365","2022-11-15","2.85","1","5"});
        Random random = new Random();
        int size = 1000;
        for(int index=0;index<size;index++){
            LocalDate targetDay = LocalDate.parse("2022-11-29").plus(random.nextInt(360), DAYS);
            Double targetInterest = (Double.valueOf(random.nextInt(21))+30.0)/100.0;
            int year = random.nextInt(5)+1;
            int round = random.nextInt(10)%2+1;
            basket.add(new String[]{
                    random.nextBoolean() ? "365" : "360",
//                    "365",
                    targetDay.toString(),
                    targetInterest.toString(),
//                    String.valueOf(round),
                    "1",
                    String.valueOf(year)});
        }

        for(String[] coupon: basket){
//            System.out.println("构造债券："+Arrays.toString(coupon));

            LocalDate startDate = LocalDate.parse(coupon[1]);
            Double interest = Double.valueOf(coupon[2]);
            int round = Integer.valueOf(coupon[3]);
            int year = Integer.valueOf(coupon[4]);
            for (int i=0;i<=year;i++){
                for(int j=1;j<=round;j++){
                    if(i==0&&j==1){
                        //start
                        list.add(new Transaction(-100, startDate.toString()));
                    }else if(i==year&&j==round){
                        //end
                        list.add(new Transaction(100+(interest / round), startDate.plus(12 / round *(round*(i-1)+j), ChronoUnit.MONTHS).toString()));
                    }else{
                        //middle-normal
                        list.add(new Transaction(interest / round, startDate.plus(12 / round *(round*(i-1)+j), ChronoUnit.MONTHS).toString()));
                    }
                }
            }
        }
        list.sort(Comparator.comparing(Transaction::getWhen));
        for (Transaction t :list){
            System.out.println(t.getWhen()+"\t"+t.getAmount());
        }

        System.out.println("构造债券个数："+basket.size()+";对应现金流数量："+list.size());
        Double xirr = XIRRUtil.xirr(list);
        System.out.println("XIRR:"+xirr);

//        //计算yld取值从-1到1，
//        for(double tmpyld = -0.000_001;tmpyld<1;tmpyld+=0.000_001){
//            double prc = DCFUtil.dcf_yld2prc(list, tmpyld);
//            double yld = DCFUtil.dcf_prc2yld(list, prc);
////            System.out.println(yld+"\t"+prc);
//            bufferedWriterMethod("./dcf.txt",tmpyld+"\t"+prc+"\t"+yld+"\n");
//        }


        double [] ylds ={xirr-0.000_003,xirr-0.000_002,xirr-0.000_001,xirr,xirr+0.000_001,xirr+0.000_002,xirr+0.000_003};

        long s, e1, e2;
        for (double yld : ylds) {
            s = System.currentTimeMillis();
            double prc = DCFUtil.dcf_yld2prc(list, yld);
            e1 = System.currentTimeMillis();
            double v = DCFUtil.dcf_prc2yld(list, prc);
            e2 = System.currentTimeMillis();
            System.out.println();
            System.out.println("yld2prc：\t\t\t\t" + yld + "->" + (300 + prc) + "\t" + (e1 - s) + "ms");
            System.out.println("牛顿拉夫森算法反推prc2yld：\t" + v + "\t" + (e2 - e1) + "ms");
        }



    }

}