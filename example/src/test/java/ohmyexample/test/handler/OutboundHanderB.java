package ohmyexample.test.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Williami
 * @description
 * @date 2022/2/17
 */
public class OutboundHanderB extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundHanderB.class);

    /**
     * 出站处理器完成Netty通道到底层通道的操作
     *
     * @param ctx
     * @param msg
     * @param promise
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        LOGGER.info("B出站处理器 write");
        // 如果注释下面这行代码，则A处理器的write方法不会执行
        super.write(ctx, msg, promise);
    }
}
