package com.littlefox;

import com.littlefox.xirr.IRRUtil;
import com.littlefox.xirr.XIRRUtil;
import com.littlefox.pojo.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Unit test for simple IRR.
 */
public class IRRTest {

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
    public void XIRR() {
        List list = Arrays.asList(
                new Transaction(-10000, "2008-01-01"),
                new Transaction(3250, "2009-02-15"),
                new Transaction(2750, "2009-04-01"),
                new Transaction(4250, "2008-10-30"),
                new Transaction(2750, "2008-03-01")
        );
        Assert.assertEquals("0.3733625335190808", XIRRUtil.xirr(list).toString());
        Assert.assertEquals("0.3733625335188315", XIRRUtil.xirr(list, 0.1D).toString());
    }

    @Test
    public void IRR() {
        Double[] arrays = {-700000.0, 120000.0, 150000.0, 180000.0, 210000.0, 260000.0};

        List<Transaction> list = Arrays.asList(arrays).stream().map(x -> {
            return new Transaction(x);
        }).collect(Collectors.toList());

        Assert.assertEquals("0.08663094803653153", IRRUtil.irr(list).toString());
        Assert.assertEquals("0.0866309480365316", IRRUtil.irr(list, 0.3D).toString());
    }


    @Test
    public void getDays() {

        Transaction today = new Transaction(0.0, "2022-11-29");
        Transaction target = new Transaction(0.0, "2025-11-25");

        System.out.println(DAYS.between(today.getWhen(), target.getWhen()));


        LocalDate parse = LocalDate.parse("2020-02-29");
        LocalDate plus = parse.plus(1, ChronoUnit.YEARS);
        System.out.println(plus);
        LocalDate plus1 = parse.plus(6, ChronoUnit.MONTHS);
        System.out.println(plus1);
    }

}
