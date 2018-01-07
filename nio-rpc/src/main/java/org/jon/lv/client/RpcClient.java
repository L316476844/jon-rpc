package org.jon.lv.client;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Package: org.jon.lv.client.RpcClient
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/5 15:41
 * Version: V1.0.0
 */
public class RpcClient {

    /**
     * 获取接口远程代理实例
     * @param consumerService  消费的服务
     * @param socketAddress socket 服务端地址
     * @param <T>
     * @return
     */
    public static <T> T getRemoteService(Class<?> consumerService, SocketAddress socketAddress) throws IOException {
        return (T) Proxy.newProxyInstance(consumerService.getClassLoader(),
                new Class<?>[]{consumerService},
                new RemoteServiceConsumer(consumerService, socketAddress));
    }

    /**
     * 发起socket调用远程服务--消费者
     */
    public static class RemoteServiceConsumer implements InvocationHandler {

        /**
         * 需要调用的远程接口
         */
        private Class<?> consumerService = null ;

        /**
         * 远程服务调用地址
         */
        private SocketAddress socketAddress = null;

        private static SocketChannel socketChannel;

        private static Selector selector;

        /** 分隔符 **/
        private final String separate = "@||@";

        public RemoteServiceConsumer(Class<?> consumerService, SocketAddress socketAddress) throws IOException {
            this.consumerService = consumerService;
            this.socketAddress = socketAddress;

            selector = Selector.open();
            socketChannel = SocketChannel.open(socketAddress);
            // 设置为非阻塞通道
            socketChannel.configureBlocking(false);
            // 注册选择器
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            String interfaceName = consumerService.getName();
            String methodName = method.getName();

            /** 参数类型数组 : 参数值 **/
            StringBuilder parameters = null;
            if (args != null && args.length > 0) {
                // 存在参数的情况
                int size = args.length;
                String[] types = new String[size];
                parameters = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    types[i] = args[i].getClass().getName();
                    parameters.append(types[i]).append("@**@").append(
                           JSON.toJSONString(args[i])
                    );
                    if (i != size - 1){
                        parameters.append("@$$@");
                    }
                }
            }

            /** 向缓冲区拼接请求内容使用  @||@  分隔**/
            StringBuilder builder = new StringBuilder(interfaceName);
            /** 接口类名-方法名-参数类型数组-参数值数组- **/
            builder.append(separate).append(methodName).append(separate).append(parameters);
            ByteBuffer buffer = ByteBuffer.wrap(builder.toString().getBytes());

            socketChannel.write(buffer);

            System.out.println("------- send request success -----");

            return getResult();
        }

        /**
         * 获取服务端响应的数据
         * @return
         */
        private Object getResult(){
            try {
                while (selector.select() > 0){
                    for (SelectionKey selectionKey : selector.selectedKeys()){
                        selector.selectedKeys().remove(selectionKey);
                        // 判断通道是否已经准备好读取
                        if(selectionKey.isReadable()){

                            // 读取通道中返回的数据
                            SocketChannel channel = (SocketChannel)selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            channel.read(buffer);
                            int position = buffer.position();

                            String result = new String(buffer.array(), 0, position);
                            result = result.trim();
                            // 返回为空处理
                            if(result == null || result.endsWith("null") ||
                                    result.endsWith("NULL")) {
                                return null;
                            }

                            String[] typeValue = result.split("@\\$\\$@");
                            String type = typeValue[0];
                            String value = typeValue[1];
                            if (type.contains("Integer") || type.contains("int")){
                                return Integer.parseInt(value);}
                            else if (type.contains("Float") || type.contains("float")){
                                return Float.parseFloat(value);}
                            else if(type.contains("Long")||type.contains("long")){
                                return Long.parseLong(value);}
                            else{
                                return JSON.parseObject(value, Class.forName(type)) ;}
                        }
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }
}
