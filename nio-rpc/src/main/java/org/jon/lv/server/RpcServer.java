package org.jon.lv.server;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Package: org.jon.lv.server.RpcServer
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2018/1/5 15:19
 * Version: V1.0.0
 */
public class RpcServer {
    /** 选择器 **/
    private static Selector selector;
    /** 服务端通道 **/
    private static ServerSocketChannel serverSocketChannel;

    /**
     * 存放service注册服务中心
     */
    private static final ConcurrentHashMap<String, Class> registerCenter = new ConcurrentHashMap<>(8);


    /**
     * 接收服务注册
     * @param service 服务接口
     * @param impl  服务实例
     */
    public static void  registerService(Class service, Class impl){
        registerCenter.put(service.getName(), impl);
    }

    /**
     * 启动服务执行客户端请求
     */
    public static void startServer( int port) throws IOException {

        serverSocketChannel = ServerSocketChannel.open();
        // 非阻塞通道
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        // 打开选择器
        selector = Selector.open();

        // 把通道注册到选择器中  ready to accept another connection
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("server starting ......" );

        try {
            // select方法是阻塞的方法 等待客户端请求
            while (selector.select() > 0){
                for (SelectionKey selectionKey : selector.selectedKeys()){
                    selector.selectedKeys().remove(selectionKey);
                    // 判断通道是否准备接受一个新的套接字
                    if(selectionKey.isAcceptable()){
                        // 调用accept方法接受连接，产生服务器端对应的SocketChannel
                        SocketChannel channel = serverSocketChannel.accept();
                        // 设置采用非阻塞模式
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        // 将selectionKey对应的Channel设置成准备接受其他请求
                        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
                    }else if(selectionKey.isReadable()){
                        // 获取该SelectionKey对应的Channel，该Channel中有可读的数据
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        try {
                            // 执行客户端请求
                            executeRemoteRequest(selectionKey, channel);
                        }catch (Exception e){
//                            e.printStackTrace();
                            //从Selector中删除指定的SelectionKey
                            selectionKey.cancel();
                            if(selectionKey.channel() != null){
                                selectionKey.channel().close();
                            }

                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private static void executeRemoteRequest(SelectionKey selectionKey, SocketChannel channel) throws IOException,
            IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        // 申请字节缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        int position = buffer.position();
        byte[] data = buffer.array();
        // 读取请求参数
        String message = new String(data,0, position);
        message = message.trim();
        buffer.clear();

        String[] requestData = message.split("@\\|\\|@");

        /** 接口类名-方法名-参数类型数组-参数值数组- **/
        String serviceName = requestData[0];
        String methodName = requestData[1];
        String parameters = requestData[2];

        Class[] parameterTypes = null;
        Object[] args = null;

        if(!"null".equals(parameters)){
            // 解析参数类型数组
            String[] paramsValues = decodeParamsTypeAndValue(parameters);
            parameterTypes = new Class[paramsValues.length];
            args = new Object[paramsValues.length];
            for (int i = 0; i < paramsValues.length; i++) {
                String[] tv = paramsValues[i].split("@\\*\\*@");
                String type = tv[0];
                String value = tv[1];
                parameterTypes[i] = Class.forName(type);

                if(type.contains("String")){
                    args[i] = String.valueOf(value);}
                else if(type.contains("Integer")||type.contains("int")){
                    args[i] = Integer.parseInt(value);}
                else if(type.contains("Float")||type.contains("float")){
                    args[i] = Float.parseFloat(value);}
                else if(type.contains("Double")||type.contains("double")){
                    args[i] = Double.parseDouble(value);}
                else if(type.contains("Long")||type.contains("long")){
                    args[i] = Long.parseLong(value);}
                else{
                    args[i] = JSON.parseObject(value, parameterTypes[i]);}
            }
        }
        // 获取执行实例
        Class serviceImpl = registerCenter.get(serviceName);
        if(serviceImpl != null){
            Method method = serviceImpl.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceImpl.newInstance(), args);
            if(result == null){
                result = "Void:null";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(result.getClass().getName());
            sb.append("@$$@");
            sb.append(JSON.toJSONString(result));

            // 发送结果回去
            channel.write(ByteBuffer.wrap(sb.toString().getBytes()));
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * 解析参数值类型及参数值
     * @param params
     * @return
     */
    private static String[] decodeParamsTypeAndValue(String params) {

        if (params.indexOf("@$$@") < 0) {
            return new String[] { params };
        }

        return params.split("@\\$\\$@");
    }

}
