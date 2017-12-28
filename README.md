## RPC 学习计划

### bio-rpc 
bio rpc 是使用阻塞的io socket通信实现rpc调用的方式。</p>
原理：服务提供方(provider)把接口注册到注册中心使用ConcurrentHashMap,
启动服务方ServerSocket循环读取客户端socket请求,获取到线程之后挂载到线程池中后台处理客户端请求并响应。
主要实现代码在ServerProviderRegisterCenter类中。</p>
客户端消费（consumer）远程服务方注册的服务,创建代理对象使用socket发起实际请求sever socket传递请求的服务，
请求的接口，请求接口的参数类型，请求接口的参数值，并获取请求响应的结果。主要实现代码ConsumerRpcProxy类中。</p>
  
启动调试：先启动sever socket类ServerMain然后启动ClientMain进行调试。</br>
  
优点：通过socket快速模拟rpc框架调用原理。</br>

缺点：首先是性能问题bio方式同步阻塞线程开销巨大，
不具备弹性伸缩能力，当并发访问量增加后，服务端的线程个数和并发访问数成线性正比，
线程数膨胀之后，系统的性能急剧下降，随着并发量的继续增加，可能会发生句柄溢出、线程堆栈溢出等问题，并导致服务器最终宕机。
然后序列化问题该例子使用的是jdk自带的序列化字节流过大在传输过程中开销过高。可使用protobuf,kryo等序列化提高性能。
服务的注册中心简陋，未提供服务自动发现需要consumer必须要知道provider的端口和服务健康状况监测，没有实现集群情况路由故障转移等多处漏洞，
这也是后续升级学习的方向。</br>
