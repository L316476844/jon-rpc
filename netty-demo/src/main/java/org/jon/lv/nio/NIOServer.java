package org.jon.lv.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Package: org.jon.lv.nio.NIOServer
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/9 13:59
 * Version: V1.0.0
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        receiveMsg();
    }

    public static void receiveMsg() throws IOException {
        // 1、开启服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 2、绑定服务地址
        serverSocketChannel.bind(new InetSocketAddress(6666));
        // 3、设置通道为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 4、开启选择器
        Selector selector = Selector.open();

        // 5、服务端通道注册到多路复用器---开启待应答监听
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6、多路复用器阻塞判断是否存在已经准备好的事件
        while(selector.select() > 0){
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            // 7、遍历已经准备好的事件
            while(iterator.hasNext()){
                SelectionKey iteratorKey = iterator.next();
                iterator.remove();
                if(iteratorKey.isAcceptable()){
                    // 8、处理待接收的客户端请求
                    SocketChannel channel = serverSocketChannel.accept();
                    // 9、配置通道为非阻塞
                    channel.configureBlocking(false);
                    // 10、注册选择器，并设置为读取模式，收到一个连接请求，然后起一个SocketChannel，并注册到selector上，之后这个连接的数据，就由这个SocketChannel处理
                    channel.register(selector, SelectionKey.OP_READ);
                    // 11、将此对应的channel设置为准备接受其他客户端请求
                    iteratorKey.interestOps(SelectionKey.OP_ACCEPT);
                }else if(iteratorKey.isReadable()){
                    // 12、处理客户端待读取的通道
                    SocketChannel channel = (SocketChannel)iteratorKey.channel();
                    // 13、申请缓冲区
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    StringBuilder sb = new StringBuilder();
                    while (channel.read(buffer) > 0){
                        buffer.flip();
                        sb.append(new String(buffer.array(), 0, buffer.limit()));
                        buffer.clear();
                    }

                    System.out.println(sb.toString());

                    // 14、将此对应的channel设置为准备下一次接受数据
                    iteratorKey.interestOps(SelectionKey.OP_READ);
                }
            }

        }

        serverSocketChannel.close();
    }
}
