package ohmyexample.test.embedded;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import ohmyexample.test.handler.InHandlerDemo;
import ohmyexample.test.handler.InHandlerDemo2;
import ohmyexample.test.handler.OutboundHanderA;
import ohmyexample.test.handler.OutboundHanderB;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Williami
 * @description
 * @date 2022/2/17
 */
public class InHandlerDemoTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(InHandlerDemoTests.class);

    /**
     * 20:15:17.746 [main] INFO  o.test.handler.InHandlerDemo - 被调用 handlerAdded（）
     * 20:15:17.747 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 handlerAdded（）
     * 20:15:17.749 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelRegistered（）
     * 20:15:17.749 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelRegistered（）
     * 20:15:17.749 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelActive（）
     * 20:15:17.749 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelActive（）
     * 20:15:17.762 [main] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
     * 20:15:17.763 [main] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
     * 20:15:17.763 [main] DEBUG i.n.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@14acaea5
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelRead（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelRead（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelReadComplete（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelReadComplete（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelRead（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelRead（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelReadComplete（）
     * 20:15:17.766 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelReadComplete（）
     * 20:15:17.840 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
     * 20:15:17.840 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
     * 20:15:17.840 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
     * 20:15:17.841 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
     * 20:15:17.841 [main] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.delayedQueue.ratio: 8
     * 20:15:17.842 [main] INFO  o.test.handler.OutboundHanderB - B出站处理器 write
     * 20:15:17.842 [main] INFO  o.test.handler.OutboundHanderA - A出站处理器 write
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelInactive（）
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelInactive（）
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelUnregistered（）
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 channelUnregistered（）
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo2 - II 被调用 handlerRemoved（）
     * 20:15:17.877 [main] INFO  o.test.handler.InHandlerDemo - 被调用 handlerRemoved（）
     */
    @Test
    public void testInHandlerLifeCircle() {
        final InHandlerDemo inHandler = new InHandlerDemo();
        final InHandlerDemo2 inHandlerDemo2 = new InHandlerDemo2();

        OutboundHanderA outboundHanderA = new OutboundHanderA();
        OutboundHanderB outboundHanderB = new OutboundHanderB();
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) {
                // 同一个非@Sharable Handler对象不能重复添加或删除
                // 出站处理器，先执行B处理器的写，再执行A处理器的写
                ch.pipeline().addLast(inHandler, inHandlerDemo2, outboundHanderA, outboundHanderB);
            }
        };
        // 创建嵌入式通道
        EmbeddedChannel channel = new EmbeddedChannel(i);
        // 堆内存
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        // 模拟入站，向嵌入式通道写一个入站数据包
        channel.writeInbound(buf);
        // 将缓存数据立即刷新到对端
        channel.flush();
        // 模拟入站，再写一个入站数据包
        channel.writeInbound(buf);
        channel.flush();
        // 通道关闭，回调
        // * 17:59:37.321 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelInactive（）
        //     * 17:59:37.321 [main] INFO  o.test.handler.InHandlerDemo - 被调用 channelUnregistered（）
        //     * 17:59:37.321 [main] INFO  o.test.handler.InHandlerDemo - 被调用 handlerRemoved（）

        // 出站处理器执行逻辑
        // 20:15:17.842 [main] INFO  o.test.handler.OutboundHanderB - B出站处理器 write
        // 20:15:17.842 [main] INFO  o.test.handler.OutboundHanderA - A出站处理器 write
        channel.writeOutbound(buf);
        channel.flush();


        channel.close();
    }
}
