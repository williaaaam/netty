package ohmyexample.test.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */
public class ByteBufTypeTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ByteBufTypeTests.class);

    @Test
    public void testHeapAndDirectByteBuf() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

        LOGGER.info(">>> ByteBufAllocator.DEFAULT.buffer()默认是{}缓冲区", buffer.hasArray() ? "堆内" : "直接");

        // 释放缓冲区内存
        buffer.release();

        buffer = ByteBufAllocator.DEFAULT.directBuffer();

        buffer.writeBytes("好好学习天天向上".getBytes(Charset.forName("UTF-8")));

        if (buffer.hasArray()) { // 如果是Heap内存
            byte[] array = buffer.array();
            int offset = buffer.arrayOffset();
            int len = buffer.readableBytes(); // writerIndex - readerIndex
            LOGGER.info(">>> 从缓冲区中读：{}", new String(array, offset, len));
        } else {
            // Native 堆，需要从Native Heap拷贝到Java Heap
            // 24=3*8 对应 "好好学习天天向上"字节数
            //CharSequence charSequence = buffer.readCharSequence(24, Charset.forName("UTF-8"));
            //LOGGER.info(">>> 从堆外缓冲区读= {}", charSequence);

            int len = buffer.readableBytes();

            byte[] array = new byte[len];

            // 把数据读取到堆内存array中，再进行Java处理
            buffer.getBytes(buffer.readerIndex(), array);

            LOGGER.info(">>> 从直接内存缓冲区读 {}", new String(array, StandardCharsets.UTF_8));
        }

        // 减少一次引用
        ReferenceCountUtil.release(buffer);
    }


    @Test
    public void testUnpooledByteBuf() {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        // 堆外内存
        LOGGER.info(">> {}", buffer.hasArray());
        LOGGER.info(">> {}", buffer instanceof CompositeByteBuf); // false

        // 创建堆内内存缓冲区
        ByteBuf heapBuf = Unpooled.buffer();

        // 直接内存
        ByteBuf directBuf = Unpooled.directBuffer();

        // 创建符合缓冲区
        CompositeByteBuf compByteBuf = Unpooled.compositeBuffer();
    }

}
