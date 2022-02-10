package ohmyexample;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        // addLast方法将一个个ChannelHandler添加到责任链上，形成链式结构，在请求进来或者响应出去时都会经过链上ChannelHandler的处理
        // 处理http消息的编解码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        // 添加自定义的ChannelHandler
        pipeline.addLast("httpServerHandler", new HttpServerChannelHandler());
    }
}