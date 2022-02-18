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
public class WriterReaderBufTests {


    private static final Logger LOGGER = LoggerFactory.getLogger(WriterReaderBufTests.class);

    @Test
    public void testReadAndWrite() {
        // 默认使用PooledByteBufAllocator分配器
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(9, 100);

        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});

        getByteBuf(byteBuf);

        readByteBuf(byteBuf);

        // 将读写指针设置为0，不同于Java NIO ByteBuffer#clear
        byteBuf.clear();

    }

    /**
     * 取字节，改变reader指针
     *
     * @param buffer
     */
    private void readByteBuf(ByteBuf buffer) {
        while (buffer.isReadable()) { // readerIndex < writerIndex
            LOGGER.info("取一个字节:" + buffer.readByte());
        }
    }

    /**
     * 读字节，不改变读指针
     *
     * @param buffer
     */
    private void getByteBuf(ByteBuf buffer) {
        for (int i = 0; i < buffer.readableBytes(); i++) { // writerIndex - readerIndex
            LOGGER.info("读一个字节:" + buffer.getByte(i));
        }
    }

}
