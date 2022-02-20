package ohmyexample.test.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.Test;

import java.util.List;

/**
 * @author william
 * @title
 * @desc
 * @date 2022/2/20
 **/
public class ByteToIntMessageDecoderTests {

    public static void main(String[] args) {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new IntegerProcessHandler());
                ch.pipeline().addLast(new IntegerProcessHandler1());
                ch.pipeline().addLast(new IntegerProcessHandler2());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);
        for (int i = 0; i < 10; i++) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(i);
            // ByteToMessageDecoder 将ByteBuf释放
            embeddedChannel.writeInbound(buf);
            // 如果直接执行writeInbound(int) 则不会执行解码工作
            //embeddedChannel.writeInbound(i);
        }
    }

}


class IntegerProcessHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int i = in.readInt();
            System.out.println("解码器解码整数: " + i);
            out.add(i);
        }
    }


}


class IntegerProcessHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int i = (Integer) msg;
        System.out.println("ChannelHandler1打印： " + i);
        super.channelRead(ctx, msg);
    }
}

class IntegerProcessHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ChannelHandler2打印 " + msg);
        System.out.println("---------------------------");
        super.channelRead(ctx, msg);
    }

}
