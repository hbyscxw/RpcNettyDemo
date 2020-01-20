package com.sq.cxw.common;

import java.io.Serializable;

/**
 * @author chengxuwei
 * @date 2019-11-07 14:22
 * @description
 */
public class RpcMsg implements Serializable {

    private static final long serialVersionUID = -7583819234960530295L;

    private String className;
    private String methodName;
    private Class<?>[] params;
    private Object[] values;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}