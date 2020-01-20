package com.sq.cxw.api.impl;

import com.sq.cxw.api.ICalc;

/**
 * @author chengxuwei
 * @date 2019-11-07 09:42
 * @description
 */
public class CalcImpl implements ICalc {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}