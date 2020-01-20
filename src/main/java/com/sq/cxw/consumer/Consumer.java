package com.sq.cxw.consumer;

import com.sq.cxw.api.ICalc;
import com.sq.cxw.consumer.proxy.RpcProxy;


/**
 * @author chengxuwei
 * @date 2019-11-07 15:00
 * @description
 */
public class Consumer {
    public static void main(String[] args) {
        int a = 100;
        int b = 10;
        ICalc calc = RpcProxy.create(ICalc.class);
        System.out.println("a+b="+calc.add(a,b));
        System.out.println("a-b="+calc.sub(a,b));
        System.out.println("a*b="+calc.mult(a,b));
        System.out.println("a/b="+calc.div(a,b));
    }


}