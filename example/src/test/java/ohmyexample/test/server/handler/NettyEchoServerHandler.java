package ohmyexample.test.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.AbstractNioByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */

// 这个注解的作用是标注一个Handler实例可以被多个通道安全地共享（多个通道的流水线可以加入同一个Handler实例）。这种共享操作，Netty默认是不允许的。
@ChannelHandler.Sharable
public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEchoServerHandler.class);

    public static final NettyEchoServerHandler INSTANCE = new NettyEchoServerHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        /**
         * 参考
         * @see io.netty.channel.nio.AbstractNioByteChannel.NioByteUnsafe#read   //     allocHandle.lastBytesRead(doReadBytes(byteBuf));
         */
        // 入站处理Netty从Java底层通道的数据读进ByteBuf,再传入通道流水线，随后开始入站处理
        ByteBuf in = (ByteBuf) msg;
        // TailContext 会自动释放ByteBuf占用内存
        //LOGGER.info("msg type: " + (in.hasArray() ? "堆内存" : "直接内存"));
        int len = in.readableBytes();
        byte[] arr = new byte[len];
        in.getBytes(0, arr);
        LOGGER.info("server received: {}", new String(arr, "UTF-8"));
        //LOGGER.info("写回前，msg.refCnt:{}", ((ByteBuf) msg).refCnt());
        // 服务端写回客户端
        ChannelFuture f = ctx.writeAndFlush(" ack");
        f.addListener((futureListener) -> {
            //LOGGER.info("写回后，msg.refCnt:" + ((ByteBuf) msg).refCnt());
        });

    }
}
