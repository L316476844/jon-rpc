package org.jon.lv.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Package: org.jon.lv.netty.DemoClient
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/12 14:33
 * Version: V1.0.0
 */
public class DemoClient {

    public void connect(String host, int port){

        // 配置客户端NIO 线程组
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        try {
            client.group(clientGroup).
                    channel(NioSocketChannel.class).
                    option(ChannelOption.TCP_NODELAY, true).
                    handler(new ClientHandler());
            //绑定端口, 异步连接操作
            ChannelFuture channelFuture = client.connect(host, port).sync();
            //等待客户端链接关闭端口关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅关闭线程组
            clientGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        DemoClient client = new DemoClient();

        client.connect("127.0.0.1", 9000);
    }
}
