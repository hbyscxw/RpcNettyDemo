package com.sq.cxw.service;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import	java.util.ArrayList;

import com.sq.cxw.common.RpcMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chengxuwei
 * @date 2019-11-07 10:03
 * @description
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String,Object> objectMap = new ConcurrentHashMap<>();
    private List<Class<?>> classList = new ArrayList<>();

    public RpcServerHandler(){
        scanClass("com.sq.cxw.api");
        doRegister();
    }

    private void doRegister() {
        for (Class<?> clazz : classList) {
            try {
                if(clazz.isInterface()){
                    continue;
                }
                Object obj = clazz.newInstance();
                objectMap.put(clazz.getInterfaces()[0].getName(), obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scanClass(String packageName) {
        String packagePath = packageName.replaceAll("\\.","/");
        URL url = this.getClass().getClassLoader().getResource(packagePath);
        File f = new File(url.getFile());
        for(File file : f.listFiles()){
            if(file.isDirectory()){
                scanClass(packageName+"."+file.getName());
            }else{
                try {
                    Class<?> clazz = Class.forName(packageName+"."+file.getName().replaceAll(".class",""));
                    classList.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object response = new Object();
        if(!(msg instanceof RpcMsg)){
            return;
        }
        RpcMsg rpcMsg = (RpcMsg) msg;
        Object o = objectMap.get(rpcMsg.getClassName());
        if(o!=null){
            Method m = o.getClass().getMethod(rpcMsg.getMethodName(),rpcMsg.getParams());
            response = m.invoke(o,rpcMsg.getValues());
        }
        ctx.writeAndFlush(response);
        ctx.close();
    }
}