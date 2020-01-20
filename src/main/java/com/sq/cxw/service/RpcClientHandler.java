package com.sq.cxw.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author chengxuwei
 * @date 2019-11-07 15:04
 * @description
 */
public class RpcClientHandler extends SimpleChannelInboundHandler {

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        setResult(msg);
    }
}