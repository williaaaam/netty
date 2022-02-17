package ohmyexample.test.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import ohmyexample.test.handler.InHandlerDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端程序不一定非的是用Netty编写，OIO, NIO均可，因为Netty底层也是使用Java NIO开发的，都使用了TCP通信协议
 *
 * @author Williami
 * @description
 * @date 2022/2/16
 */
public class NettyDiscardServer {


    private static final Logger LOGGER = LoggerFactory.getLogger(NettyDiscardServer.class);

    private final int serverPort;
    // 创建服务端引导类
    // Netty服务引导类将组件组装在一起，帮助快速实现Netty服务器的监听和启动
    private ServerBootstrap b = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }


    public static void main(String[] args) {
        int port = 8899;
        new NettyDiscardServer(port).runServer();
    }

    /**
     * Netty中对应的反应器组件有多种，不同应用通信场景用到的反应器组件各不相同。
     * 一般来说，对应于多线程的Java NIO通信的应用场景，Netty对应的反应器组件为NioEventLoopGroup。
     */
    public void runServer() {
        // 创建反应器Reactor轮询组,Reactor负责IO事件的查询和分发
        // boss和worker共同构成主从Reactor模式

        // bossLoopGroup负责通道新连接的IO事件的监听
        // 负责新连接的监听和接受
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);

        // workerLoopGroup线程池负责传输通道的IO时间的处理和数据传输
        // 负责IO传输事件的轮询与分发
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            //1. 为引导类对象设置反应器轮询组
            b.group(bossLoopGroup, workerLoopGroup);
            //2. 设置nio类型的通道，当然也可以设置BIO同步阻塞式通道
            b.channel(NioServerSocketChannel.class);
            //3. 设置监听端口
            b.localAddress(serverPort);
            //4. 给父通道设置参数,开启底层TCP心跳机制，true表示开启
            b.option(ChannelOption.SO_KEEPALIVE, true);
            //4.1 给父通道设置参数
            //b.childOption()

            //5. 装配子通道的流水线
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                // 有连接到达时会创建一个子通道，并初始化
                // 子通道创建后会执行initChannel方法
                // SocketChannel 表示初始化的通道类型，要和配置在引导器上的通道类型对应上
                // ch表示新接收的通道
                protected void initChannel(SocketChannel ch) { // 每当父通道成功接收到一个连接并创建成功一个子通道后，就会初始化子通道，此时这里配置的ChannelInitializer实例就会被调用。
                    // 流水线的职责：负责管理通道中的处理器
                    // 向“子通道”（传输通道）流水线装配一个处理器
                    ch.pipeline().addLast(new NettyDiscardHandler(), new InHandlerDemo());
                }
            });
            // 6. 开始绑定服务器
            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = b.bind().sync();
            // Logger.info(" 服务器启动成功，监听端口: " +
            // channelFuture.channel().localAddress();
            // 7. 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭的异步任务结束

            // Future异步回调
            // 如果要阻塞当前线程直到通道关闭，可以调用通道的closeFuture()方法，以获取通道关闭的异步任务。当通道被关闭时，closeFuture实例的sync()方法会返回。
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8. 优雅关闭EventLoopGroup
            // 释放掉所有资源，包括创建的反应器线程
            /**
             * 关闭反应器轮询组，同时会关闭内部的子反应器线程，也会关闭内部的选择器、内部的轮询线程以及负责查询的所有子通道。
             */
            workerLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 处理业务逻辑的Handler
     * <p>
     * Netty的Handler需要处理多种IO事件（如读就绪、写就绪），对应于不同的IO事件，Netty提供了一些基础方法。这些方法都已经提前封装好，应用程序直接继承或者实现即可。
     * 比如说，对于处理入站的IO事件，其对应的接口为ChannelIn-boundHandler，并且Netty提供了ChannelInboundHandler-Adapter适配器作为入站处理器的默认实现。
     * 简单理解，入站指的是输入:channel->Handler，出站指的是输出：Handler->Channel
     */
    private static class NettyDiscardHandler extends ChannelInboundHandlerAdapter {
        /**
         * Channel数据过来之后触发Handler的channelRead()入站处理方法
         * <p>
         * 回调方法
         * 当通道缓冲区可读时，Netty会调用fireChannelRead()方法，触发通道可读事件，而在通道流水线注册过的入站处理器的channelRead()回调方法会被调用，以便完成入站数据的读取和处理。
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 入站业务逻辑
            ByteBuf in = (ByteBuf) msg;
            try {
                while (in.isReadable()) {
                    // 打印客户端输入直接丢弃不管了
                    System.out.print(in.readChar());
                    System.out.println();
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }

}

