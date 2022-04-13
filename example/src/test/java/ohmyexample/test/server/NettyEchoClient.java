package ohmyexample.test.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import ohmyexample.test.server.handler.NettyEchoClientHandler;
import ohmyexample.test.server.handler.NettyEchoServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * 客户端程序不一定非的是用Netty编写，OIO, NIO均可，因为Netty底层也是使用Java NIO开发的，都使用了TCP通信协议
 *
 * @author Williami
 * @description
 * @date 2022/2/16
 */
public class NettyEchoClient {


    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEchoClient.class);

    private final int serverPort;
    // 创建服务端引导类
    // Netty服务引导类将组件组装在一起，帮助快速实现Netty服务器的监听和启动
    private Bootstrap b = new Bootstrap();

    public NettyEchoClient(int serverPort) {
        this.serverPort = serverPort;
    }


    public static void main(String[] args) {
        int port = 8899;
        new NettyEchoClient(port).runClient();
    }

    /**
     * Netty中对应的反应器组件有多种，不同应用通信场景用到的反应器组件各不相同。
     * 一般来说，对应于多线程的Java NIO通信的应用场景，Netty对应的反应器组件为NioEventLoopGroup。
     */
    public void runClient() {

        // 创建反应器轮询组
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            //1. 为引导类对象设置反应器轮询组
            b.group(workerLoopGroup);
            //2. 设置nio类型的通道，当然也可以设置BIO同步阻塞式通道
            b.channel(NioSocketChannel.class);
            //3. 设置监听端口
            b.remoteAddress("127.0.0.1", serverPort);
            // 设置通道的内存分配器
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            //5. 装配子通道的流水线
            b.handler(new ChannelInitializer<SocketChannel>() {
                // 有连接到达时会创建一个子通道，并初始化
                // 子通道创建后会执行initChannel方法
                // SocketChannel 表示初始化的通道类型，要和配置在引导器上的通道类型对应上
                // ch表示新接收的通道
                protected void initChannel(SocketChannel ch) { // 每当父通道成功接收到一个连接并创建成功一个子通道后，就会初始化子通道，此时这里配置的ChannelInitializer实例就会被调用。
                    // 流水线的职责：负责管理通道中的处理器
                    // 向“子通道”（传输通道）流水线装配一个处理器
                    ch.pipeline().addLast(NettyEchoClientHandler.INSTANCE);
                }
            });

            ChannelFuture channelFuture = b.connect();
            channelFuture.addListener((futureListener) -> {
                if (futureListener.isSuccess()) {
                    LOGGER.info("EchoClient客户端连接成功!");
                } else {
                    LOGGER.info("EchoClient客户端连接失败!");
                }
            });

            // 阻塞直到连接成功
            channelFuture.sync();

            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入发送内容：");
            while (scanner.hasNext()) {
                String next = scanner.next();
                if ("quit".equals(next)) {
                    break;
                }
                byte[] bytes = ("2022-02-18" + " >> " + next).getBytes("UTF-8");
                // PooledByteBufAllocator
                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes(bytes);
                channel.writeAndFlush(buffer);
                System.out.println("请输入发送内容：");
            }
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8. 优雅关闭EventLoopGroup
            // 释放掉所有资源，包括创建的反应器线程
            /**
             * 关闭反应器轮询组，同时会关闭内部的子反应器线程，也会关闭内部的选择器、内部的轮询线程以及负责查询的所有子通道。
             */
            workerLoopGroup.shutdownGracefully();
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

