package com.sq.cxw.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author chengxuwei
 * @date 2019-11-07 15:04
 * @description
 */
public class RpcClientHandler extends SimpleChannelInboundHandler {

    private CompletableFuture<Object> future = new CompletableFuture<>();

    public Object getResult() throws Exception{
        return future.get();
    }

    public Object getResult(long timeout, TimeUnit unit) throws Exception{
        return future.get(timeout, unit);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        future.complete(msg);
    }
}