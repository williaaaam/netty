package ohmyexample.test.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */

// 这个注解的作用是标注一个Ha-dler实例可以被多个通道安全地共享（多个通道的流水线可以加入同一个Handler实例）。这种共享操作，Netty默认是不允许的。
@ChannelHandler.Sharable
public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEchoClientHandler.class);

    public static final NettyEchoClientHandler INSTANCE = new NettyEchoClientHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf in = (ByteBuf) msg;
        // TailContext 会自动释放ByteBuf占用内存
        int len = in.readableBytes();
        byte[] arr = new byte[len];
        in.getBytes(0, arr);
        LOGGER.info("client received: {}", new String(arr, "UTF-8"));
        // 方法一：手动释放ByteBuf
        in.release();
        //方法二：调用父类的入站方法，将msg向后传递
        //super.channelRead(ctx, msg);

    }
}
