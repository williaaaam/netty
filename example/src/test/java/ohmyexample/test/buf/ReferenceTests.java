package ohmyexample.test.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */
public class ReferenceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceTests.class);

    @Test
    public void testReferenceCount() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});

        //LOGGER.info(">>> writeBytes [1 2 3 4]");
        LOGGER.info(">>> after create: {}", byteBuf.refCnt()); // 1

        // 增加一次引用计数
        byteBuf.retain(); // 2

        LOGGER.info(">>> after retain: {}", byteBuf.refCnt());

        // 减少一次引用计数
        byteBuf.release(); // 1
        LOGGER.info(">>> after release: {}", byteBuf.refCnt());

        // 减少一次引用计数
        byteBuf.release(); // 0
        LOGGER.info(">>> after release: {}", byteBuf.refCnt());

        //LOGGER.info(">>>> readByte {}", byteBuf.readByte());

        // readBoolean 报错
        LOGGER.info(">>>> readByte {}", byteBuf.readBoolean()); // refCnt已经为0，不能再使用ByteBuf对象了


        // readByte 报错
        LOGGER.info(">>>> readByte {}", byteBuf.readByte()); // refCnt已经为0，不能再使用ByteBuf对象了

        // 增加一次引用计数
        byteBuf.retain(); // refCnt已经为0，不能再retain
        LOGGER.info(">>> after retain: {}", byteBuf.refCnt());

    }
}
