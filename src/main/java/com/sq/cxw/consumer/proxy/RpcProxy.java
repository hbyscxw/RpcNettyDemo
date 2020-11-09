package com.sq.cxw.consumer.proxy;
import java.lang.reflect.Proxy;

import com.sq.cxw.common.RpcMsg;
import com.sq.cxw.service.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author chengxuwei
 * @date 2019-11-07 15:21
 * @description
 */
public class RpcProxy {
    public static <T> T create(Class<?> clazz) {
        MethodProxy methodProxy = new MethodProxy(clazz);
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz},methodProxy);
    }

}

class MethodProxy implements InvocationHandler {
    Class<?> clazz;
    MethodProxy(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //实现类
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }
        return rpcInvoke(method,args);
    }

    private Object rpcInvoke(Method method, Object[] args) {
        RpcMsg msg = new RpcMsg();
        msg.setClassName(clazz.getName());
        msg.setMethodName(method.getName());
        msg.setParams(method.getParameterTypes());
        msg.setValues(args);
        return rpc(msg);
    }

    private Object rpc(RpcMsg msg){
        EventLoopGroup group = new NioEventLoopGroup();
        RpcClientHandler handler = new RpcClientHandler();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    //用于向你的Channel当中添加ChannelInboundHandler的实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                            // LengthFieldPrepender是一个编码器，主要是在响应字节数据前面添加字节长度字段
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            // object解码
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            // object编码
                            ch.pipeline().addLast(new ObjectEncoder());
                            //ChannelPipeline用于存放管理ChannelHandel
                            //ChannelHandler用于处理请求响应的业务逻辑相关代码
                            ch.pipeline().addLast(handler);
                        }
                    })
                    //对Channel进行一些配置
                    //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
                    .option(ChannelOption.SO_KEEPALIVE, true);
            // 客户端开启
            ChannelFuture cf = b.connect("localhost", 8888).sync();
            cf.channel().writeAndFlush(msg);
            // 等待直到连接中断
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
        Object obj = null;
        try {
            obj = handler.getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}