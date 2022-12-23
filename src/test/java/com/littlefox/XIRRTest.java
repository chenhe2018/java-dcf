package com.littlefox;

import com.littlefox.dcf.DCFUtil;
import com.littlefox.xirr.XIRRUtil;
import com.littlefox.pojo.Transaction;
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
public class XIRRTest {

    public long startTime = 0;

    @Before
    public void before(){
        startTime = System.currentTimeMillis();
    }

    @After
    public void after(){
        long endTime = System.currentTimeMillis();
        System.out.println("耗时："+(endTime-startTime)+"ms");
    }

    @Test
    public void XIRR() {
        List list = Arrays.asList(
                new Transaction(-10000,"2008-01-01"),
                new Transaction(3250,"2009-02-15"),
                new Transaction( 2750,"2009-04-01"),
                new Transaction(4250,"2008-10-30"),
                new Transaction(2750,"2008-03-01")
        );
        Assert.assertEquals("0.3733625335190808", XIRRUtil.xirr(list).toString());
        Assert.assertEquals("0.3733625335188315",XIRRUtil.xirr(list,0.1D).toString());
    }


    @Test
    public void getDays(){

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2025-11-25");

        System.out.println(DAYS.between(today.getWhen(), target.getWhen()));


        LocalDate parse = LocalDate.parse("2020-02-29");
        LocalDate plus = parse.plus(1,ChronoUnit.YEARS);
        System.out.println(plus);
        LocalDate plus1 = parse.plus(6, ChronoUnit.MONTHS);
        System.out.println(plus1);
    }


    @Test
    public void coupon57(){

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2025-11-25");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100,"2022-11-29"),
                new Transaction(5.7,"2023-11-25"),
                new Transaction(5.7,"2024-11-25"),
                new Transaction(105.7,"2025-11-25")
        );

        Double xirr = XIRRUtil.xirr(list);
        System.out.println("xirr:"+xirr);
        System.out.println("********************************");
        System.out.println("********************************");
        System.out.println("********************************");

        for(int i=-10;i<10;i++){
            double yld = 0.000_001*i+xirr;
            double prc = DCFUtil.dcf_yld2prc(list, yld);
            System.out.println("yld2prc："+yld+"->"+prc);
            System.out.println("牛顿拉夫森算法反推prc2yld："+DCFUtil.dcf_prc2yld(list,prc));
        }

//        double yld =0.02;
//        double prc = DCFUtil.dcf_yld2prc(list, yld);
//        System.out.println("yld2prc："+yld+"->"+prc);
//        System.out.println("牛顿拉夫森算法反推prc2yld："+DCFUtil.dcf_prc2yld(list,prc));
    }

    @Test
    public void coupon385(){

        Transaction start = new Transaction(0.0, "2022-11-29");
        Transaction end = new Transaction(0.0, "2029-11-17");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100,"2022-11-29"),
                new Transaction(3.85,"2023-11-17"),
                new Transaction(3.85,"2024-11-17"),
                new Transaction(3.85,"2025-11-17"),
                new Transaction(3.85,"2026-11-17"),
                new Transaction(3.85,"2027-11-17"),
                new Transaction(3.85,"2028-11-17"),
                new Transaction(103.85,"2029-11-17")
        );
        System.out.println(XIRRUtil.xirr(list).toString());
//        System.out.println(XIRRUtil.xirr(list).toString());
//        System.out.println(XIRRUtil.xirr(list,126.95-100).toString());
//        System.out.println(XIRRUtil.xirr(list,127-100).toString());


        double yld = 0.03867;
        double allPrc = 0.0;
        for(int i=1;i<list.size();i++){
            Transaction transaction = list.get(i);
            double year = Double.valueOf(DAYS.between(transaction.getWhen(),end.getWhen()))/365;
            allPrc += transaction.getAmount() * Math.pow(1+yld, year);
        }
        System.out.println(allPrc);
    }

    @Test
    public void coupon285(){

        Transaction start = new Transaction(0.0, "2022-11-29");
        Transaction end = new Transaction(0.0, "2027-11-15");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100,"2022-11-29",365.0),
                new Transaction(2.85,"2023-11-15",365.0),
                new Transaction(2.85,"2024-11-15",365.0),
                new Transaction(2.85,"2025-11-15",365.0),
                new Transaction(2.85,"2026-11-15",365.0),
                new Transaction(102.85,"2027-11-15",365.0)
        );
        list.stream().sorted(Comparator.comparing(Transaction::getWhen));
        System.out.println("XIRR:"+XIRRUtil.xirr(list).toString());
        Assert.assertEquals("0.028719759377813556",XIRRUtil.xirr(list).toString());

        //更换XIRR的计息基准
        for (Transaction t :list){
            t.setTy(360.0);
        }
        Assert.assertEquals("0.028320819228495588",XIRRUtil.xirr(list).toString());
        Double prepare_xirr_rate = XIRRUtil.xirr(list);
        System.out.println("XIRR(prepare_xirr_rate):"+prepare_xirr_rate.toString());

        /**
         * discounted cash flow
         */
        ArrayList<Transaction> dcfList = new ArrayList<>();
        for (Transaction t :list){
            if(t.getAmount()>0){
                dcfList.add(t);
            }
        }
        dcfList.sort(Comparator.comparing(Transaction::getWhen));
        //注意：当预设现金流收益率为XIRR时，未来现金流总现值为0，即与当前时刻的现金价格保持一致。
        System.out.println("DCF(dcfList):"+ DCFUtil.dcf_yld2prc(dcfList,LocalDate.parse("2022-11-29"),prepare_xirr_rate));
        System.out.println("DCF(list):"+DCFUtil.dcf_yld2prc(list,LocalDate.parse("2022-11-29"),prepare_xirr_rate));


        /**
         * 试算XIRR，带入目标收益target
         */
//        double guess_dcf_rate = 0.039030;
        double guess_dcf_rate = prepare_xirr_rate+0.001;
        double guss_dcf_allprc = DCFUtil.dcf_yld2prc(dcfList, LocalDate.parse("2022-11-29"), guess_dcf_rate);
        System.out.println("(试算DCF)收益率/总现值:"+guess_dcf_rate+"=>"+guss_dcf_allprc);
        Double xirr = XIRRUtil.xirr(list, guss_dcf_allprc);
        System.out.println("(试算DCF验证):"+xirr);


    }

    @Test
    public void pow(){
        System.out.println(Math.pow(4,2));//16
        System.out.println(Math.pow(4,-2));//1/16
        System.out.println(Math.pow(4,1/2));//注意：1/2=0
        System.out.println(Math.pow(4,1/2.0));//注意：1/2.0=0.5
        System.out.println(Math.pow(4,0.5));//根号4
        System.out.println(Math.sqrt(4));
    }

    @Test
    public void testDivide(){
        List<Transaction> list = Arrays.asList(
                new Transaction(-100,"2022-11-29"),
                new Transaction(2.85,"2023-11-15"),
                new Transaction(2.85,"2024-11-15"),
                new Transaction(2.85,"2025-11-15"),
                new Transaction(2.85,"2026-11-15"),
                new Transaction(102.85,"2027-11-15")
        );
        long between = DAYS.between(list.get(0).getWhen(), list.get(1).getWhen());
        System.out.println(between/360);

        BigDecimal bigDecimal = new BigDecimal(between);
        System.out.println(bigDecimal.divide(new BigDecimal(360)));
    }

    @Test
    public void my_XIRR() {

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2029-11-17");

        List<Transaction> list = Arrays.asList(
                new Transaction(-100,"2022-11-29",365.0),
                new Transaction(5.7,"2023-11-25",365.0),
                new Transaction(5.7,"2024-11-25",365.0),
                new Transaction(105.7,"2025-11-25",365.0),

                new Transaction(-100,"2022-11-29",365.0),
                new Transaction(3.85,"2023-11-17",365.0),
                new Transaction(3.85,"2024-11-17",365.0),
                new Transaction(3.85,"2025-11-17",365.0),
                new Transaction(3.85,"2026-11-17",365.0),
                new Transaction(3.85,"2027-11-17",365.0),
                new Transaction(3.85,"2028-11-17",365.0),
                new Transaction(103.85,"2029-11-17",365.0),


                new Transaction(-100,"2022-11-29",360.0),
                new Transaction(2.85,"2023-11-15",360.0),
                new Transaction(2.85,"2024-11-15",360.0),
                new Transaction(2.85,"2025-11-15",360.0),
                new Transaction(2.85,"2026-11-15",360.0),
                new Transaction(102.85,"2027-11-15",360.0)
        );
        //假设调整22国新控股MTN006(能源保供特别债)的计息基准为A/360，参考收益率不同
        //文档参考收益率3.9038%，
        System.out.println("XIRR(计息基准调整前)："+XIRRUtil.xirr(list).toString());
        for(Transaction t:list){
            t.setTy(365.0);
        }
        System.out.println("XIRR(计息基准调整后)："+XIRRUtil.xirr(list).toString());//参考收益率3.9218%

        /**
         * discounted cash flow
         */
        list.sort(Comparator.comparing(Transaction::getWhen));
        for (Transaction t :list){
            System.out.println(t.getWhen()+"\t"+t.getAmount());
        }
        System.out.println("总现值："+DCFUtil.dcf_yld2prc(list,LocalDate.parse("2022-11-29"),0.039217));
        System.out.println("总现值："+DCFUtil.dcf_yld2prc(list,LocalDate.parse("2022-11-29"),0.039218));

//        double rate2allprc = DCFUtil.dcf_yld2prc(list, LocalDate.parse("2022-11-29"), 0.039037);
//        System.out.println("总现值："+rate2allprc);
//        Double allprc2rate = XIRRUtil.xirr(list, rate2allprc);
//        System.out.println("(试算DCF验证):"+allprc2rate);

        /**
         * 试算XIRR，带入目标收益target
         */
        System.out.println("*************************************************************");
        System.out.println("*************************************************************");
        double guess_dcf_rate = XIRRUtil.xirr(list);//默认使用XIRR作为预设报酬率
        System.out.println("默认使用XIRR作为预设值："+guess_dcf_rate);
        Double dcf = XIRRUtil.xirr(list, 0);
        System.out.println("总现值为0时收益率："+dcf);
        //收益率推算全价
        double guss_dcf_allprc = DCFUtil.dcf_yld2prc(list, LocalDate.parse("2022-11-29"), guess_dcf_rate);
        System.out.println("rate2allprc:"+guess_dcf_rate+"=>"+guss_dcf_allprc);
        //全价推送收益率
        Double guess_dcf_rate_reverse = XIRRUtil.xirr(list, guss_dcf_allprc);
        System.out.println("allprc2rate:"+guss_dcf_allprc+"=>"+guess_dcf_rate_reverse);
        //再推算下全价
        System.out.println(DCFUtil.dcf_yld2prc(list, LocalDate.parse("2022-11-29"), guess_dcf_rate_reverse));
    }

    @Test
    public void testRandom(){
        Random random = new Random();
        for (int i=0;i<10;i++){
//            System.out.println(random.nextInt(6));
//            System.out.println(random.nextDouble());
            System.out.println((Double.valueOf(random.nextInt(21))+30.0)/100.0);
            System.out.println(random.nextInt(10)%2+1);
        }
    }

    @Test
    public void basketTest() throws IOException {
        List<Transaction> list = new ArrayList();
        List<String[]> basket = new ArrayList<>();
//        basket.add(new String[]{"365","2022-11-25","5.7","1","3"});
//        basket.add(new String[]{"365","2022-11-17","3.85","1","7"});
//        basket.add(new String[]{"365","2022-11-15","2.85","1","5"});
        Random random = new Random();
        int size = 100;
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
            System.out.println("构造债券："+Arrays.toString(coupon));

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

        /**
         * XIRR
         */
        long start = System.currentTimeMillis();
        String yld = XIRRUtil.xirr(list, 365.0).toString();
        long end = System.currentTimeMillis();
        System.out.println("债券个数："+basket.size()+";计息轮数："+list.size()+";耗时："+(end-start)+"ms;XIRR:"+yld);
//        Assert.assertEquals("0.03897872757746953",XIRRUtil.xirr(list, 365.0));


        /**
         * discounted cash flow
         */
//        ArrayList<Transaction> dcfList = new ArrayList<>();
//        ArrayList<Transaction> capitalList = new ArrayList<>();
//        for (Transaction t :list){
//            if(t.getAmount()>0){
//                dcfList.add(t);
//            }else{
//                capitalList.add(t);
//            }
//        }
//        dcfList.sort(Comparator.comparing(Transaction::getWhen));
//        for (Transaction t :dcfList){
//            System.out.println(t.getWhen()+"\t"+t.getAmount());
//        }

        /**
         * 批量正向求解，输入预设收益率rate，求DV
         */
        long s1 = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat("#0.0000000");
        //遍历正向算法，遍历6位
        for(double dcf_yld = 0.000_0;dcf_yld<1;dcf_yld+=0.000_1){
            double dcf_allprc = DCFUtil.dcf_yld2prc(list, LocalDate.parse("2022-11-29"), dcf_yld);
            double dcf_xirr = XIRRUtil.xirr(list, dcf_allprc);
            System.out.println(df.format(dcf_yld)+"\t"+dcf_allprc+"\t"+dcf_xirr);
            bufferedWriterMethod("./dcf.txt",df.format(dcf_yld)+"\t"+dcf_allprc+"\t"+dcf_xirr+"\n");
        }
        long e1 = System.currentTimeMillis();
        System.out.println("债券个数："+basket.size()+";计息轮数："+list.size()+";耗时："+(e1-s1)+"ms");


        /**
         * 如果预设收益值rate等于XIRR，则收益DV=0
         */
//        double dcf = DCFUtil.dcf_yld2prc(dcfList, LocalDate.parse("2022-11-29"), Double.valueOf(yld));
//        System.out.println("债券个数：" + basket.size() + ";计息轮数：" + dcfList.size()
//                + ";本金：" + capitalList.stream().mapToDouble(Transaction::getAmount).sum()
//                + ";现金流贴现：" + dcf
//                + "");

        /**
         * 反向求解，输入总现价为0，推测收益率
         */
//        long s1 = System.currentTimeMillis();
//        DecimalFormat df = new DecimalFormat("#0.0000000");
//        Double dcf = XIRRUtil.xirr(list, 0);
//        System.out.println(df.format(dcf)+"\t"+dcf);
//        long e1 = System.currentTimeMillis();
//        System.out.println("债券个数："+basket.size()+";计息轮数："+dcfList.size()+";耗时："+(e1-s1)+"ms");

    }

}
