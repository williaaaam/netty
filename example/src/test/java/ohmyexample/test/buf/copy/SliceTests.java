package ohmyexample.test.buf.copy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */
public class SliceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(SliceTests.class);


    /**
     * slice浅复制
     */
    @Test
    public void testSlice() {

        ByteBuf byteBuf = Unpooled.buffer(4, 10);

        byteBuf.writeBytes(new byte[]{1, 2, 3, 4});

        IntStream.rangeClosed(1, 2).forEach(i -> {
            int readerIndex = byteBuf.readerIndex();
            int readableBytes = byteBuf.readableBytes();
            ByteBuf slice = byteBuf.slice(readerIndex, readableBytes);
            getByteBuf(slice);
            System.out.println("**********************************************");
            System.out.println("开始修改原ByteBuf, 索引1对应的值改为-1"); // 1 -1 3 4
            byteBuf.setByte(1, -1);
        });

        getByteBuf(byteBuf);
        byteBuf.release();

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

    /**
     * 测试源ByteBuf与分片slice引用计数
     */
    @Test
    public void testSliceReferenceCount() {
        ByteBuf sourceHeapByteBuf = Unpooled.buffer();

        sourceHeapByteBuf.writeBytes(new byte[]{1, 2, 3, 4, 5});
        int readerIndex = sourceHeapByteBuf.readerIndex();
        int readableBytes = sourceHeapByteBuf.readableBytes();
        // slice不影响源引用计数
        ByteBuf slice = sourceHeapByteBuf.slice(readerIndex, readableBytes);

        // slice支持修改，但不支持write
        //slice.writeByte(0);
        slice.retain();
        slice.setByte(0, -1);
        System.out.println(sourceHeapByteBuf.refCnt()); // 2
        System.out.println(slice.refCnt()); // 2
        slice.release();
        //System.out.println(sourceHeapByteBuf.getByte(0)); // -1

        //System.out.println(sourceHeapByteBuf.refCnt()); // 1
        sourceHeapByteBuf.release();


    }


}
