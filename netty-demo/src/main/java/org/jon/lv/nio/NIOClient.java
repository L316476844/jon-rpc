package org.jon.lv.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Package: org.jon.lv.nio.NIOClient
 * Description: java nio 客户端
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/9 11:24
 * Version: V1.0.0
 */
public class NIOClient {


    public static void main(String[] args) throws IOException {
        sendMsg();
    }

    private static void sendMsg() throws IOException {

        // 1、打开客户端通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 6666));

        // 2、设置通道为非阻塞通道
        socketChannel.configureBlocking(false);

        // 3、新建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 4、获取传输数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String str = scanner.next();
            if(str.equalsIgnoreCase("quit")){
                break;
            }
            buffer.put((LocalDateTime.now().toString() + ": " + str).getBytes());
            // 切换读模式
            buffer.flip();
            socketChannel.write(buffer);
            // 清空缓冲区
            buffer.clear();
        }

        scanner.close();
        socketChannel.close();
    }
}
