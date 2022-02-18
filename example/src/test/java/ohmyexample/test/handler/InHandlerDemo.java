package ohmyexample.test.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Williami
 * @description
 * @date 2022/2/17
 */
public class InHandlerDemo extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(InHandlerDemo.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 channelRegistered（）");
        // 如果注释下面这行代码，则流水线被截断
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 channelUnregistered（）");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 channelActive（）");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 channelInactive（）");
        super.channelInactive(ctx);
    }

    /**
     * 第一个入站处理器，msg一定是ByteBuf类型
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("被调用 channelRead（）");
        // 如果不调用父类的channelRead（）,则InHandlerDemo2#channelRead()方法不会被调用;并且需要手动释放ByteBuf
        // 入站消息没有被处理，或者说来到了流水线末尾，释放缓冲区
        super.channelRead(ctx, msg);
        // byteBuf.release() // 如果没有调用super.channelRead() 手动释放内存
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 channelReadComplete（）");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 handlerAdded（）");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("被调用 handlerRemoved（）");
        super.handlerRemoved(ctx);
    }
}
