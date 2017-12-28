package org.jon.lv.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Package: org.jon.lv.server.ServerProviderRegisterCenter
 * Description: 描述
 * Copyright: Copyright (c) 2017
 *
 * @author lv bin
 * Date: 2017/12/27 15:08
 * Version: V1.0.0
 */
public class ServerProviderRegisterCenter {

    /**
     * 存放service注册服务中心
     */
    private static final ConcurrentHashMap<String, Class> registerCenter = new ConcurrentHashMap<>(8);

    /**
     * 线程池后台执行客户端sock的请求
     * ExecutorService executorService = Executors.newFixedThreadPool(8);
     */
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16,
            60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), Executors.defaultThreadFactory());


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
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("socket server starting ......" );

        while (true){
            /**
             * 服务端应答
             */
            threadPoolExecutor.execute(new ProviderServerThead(serverSocket.accept()));
        }

    }

    /**
     * 后台开启server socket 提供服务对外调用
     */
    public static class ProviderServerThead implements Runnable{

        private Socket socket;

        public ProviderServerThead(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {
                // 获取客户端输入流
                inputStream = new ObjectInputStream(socket.getInputStream()) ;
                // 获取输出流
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                // 请求的服务名称
                String serviceName =  inputStream.readUTF();
                // 请求的方法名称
                String methodName = inputStream.readUTF();
                // 请求接口接口的参数类型
                Class[] paramTypes =  (Class[])inputStream.readObject();
                // 获取请求接口参数
                Object[] args = (Object[])inputStream.readObject();

                // 获取执行实例
                Class serviceImpl = registerCenter.get(serviceName);
                if(serviceImpl != null){
                    Method method = serviceImpl.getMethod(methodName, paramTypes);
                    Object object = method.invoke(serviceImpl.newInstance(), args);
                    outputStream.writeObject(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try{
                    if(inputStream != null ) {inputStream.close();}
                    if(outputStream != null ) {outputStream.close();}
                    if(socket != null ) {socket.close();}
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }

        }
    }
}
