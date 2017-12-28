package org.jon.lv.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Package: org.jon.lv.client.ConsumerRpcProxy
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/28 11:07
 * Version: V1.0.0
 */
public class ConsumerRpcProxy {

    /**
     * 获取接口远程代理实例
     * @param consumerService  消费的服务
     * @param socketAddress socket 服务端地址
     * @param <T>
     * @return
     */
    public static <T> T getRemoteService(Class<?> consumerService, SocketAddress socketAddress){
        return (T) Proxy.newProxyInstance(consumerService.getClassLoader(),
                new Class<?>[]{consumerService},
                new RemoteServiceConsumer(consumerService, socketAddress));
    }

    /**
     * 发起socket调用远程服务--消费者
     */
    public static class RemoteServiceConsumer implements InvocationHandler{

        /**
         * 需要调用的远程接口
         */
        private Class<?> consumerService = null ;

        /**
         * 远程服务调用地址
         */
        private SocketAddress socketAddress = null;

        public RemoteServiceConsumer(Class<?> consumerService, SocketAddress socketAddress) {
            this.consumerService = consumerService;
            this.socketAddress = socketAddress;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            /**
             * 客户端
             */
            Socket socket = null;
            /**
             * 输入流
             */
            ObjectInputStream inputStream = null;

            /**
             * 输出流
             */
            ObjectOutputStream outputStream = null;

            try {

                socket = new Socket();
                socket.connect(socketAddress);

                /**
                 * 获取客户端socket写入流
                 */
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                /**
                 * 写入的顺序与接收顺序保持一致
                 */
                outputStream.writeUTF(consumerService.getName());
                outputStream.writeUTF(method.getName());
                outputStream.writeObject(method.getParameterTypes());
                outputStream.writeObject(args);

                /**
                 * 获取接口执行结果
                 */
                inputStream = new ObjectInputStream(socket.getInputStream());

                return inputStream.readObject();

            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
                try{
                    if(inputStream != null ) {inputStream.close();}
                    if(outputStream != null ) {outputStream.close();}
                    if(socket != null ) {socket.close();}
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }
}
