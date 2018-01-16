package org.jon.lv.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Package: org.jon.lv.netty.DemoServer
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/12 11:08
 * Version: V1.0.0
 */
public class DemoServer {

    public void bind(int port){
        // 处理服务器端接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 进行网络通信（读写）
        EventLoopGroup workGroup =new NioEventLoopGroup();

        try {
            // 辅助工具类，用于服务器通道的一系列配置
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 绑定线程组
            serverBootstrap.group(bossGroup, workGroup).
                    //指定NIO的模式
                    channel(NioServerSocketChannel.class).
                    //配置具体的数据处理方式
                    childHandler(new ChildServerHandler()).
                    /**
                     * 对于ChannelOption.SO_BACKLOG的解释：
                     * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
                     * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                     * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
                     * 从B队列中取出完成了三次握手的连接。
                     * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
                     * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
                     * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
                     */
                    //设置TCP缓冲区
                    option(ChannelOption.SO_BACKLOG, 1024).
                    //设置发送数据缓冲大小
                    option(ChannelOption.SO_SNDBUF, 32 * 1024).
                    //设置接受数据缓冲大小
                    option(ChannelOption.SO_RCVBUF, 32 * 1024).
                    //保持连接
                    childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口 同步等待成功
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 等待服务端监听端口关闭
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭线程组
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public class ChildServerHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast("serverHandler", new ServerHandler());
        }
    }

    public static void main(String[] args) {
        DemoServer demoServer = new DemoServer();

        demoServer.bind(9000);
    }

}
