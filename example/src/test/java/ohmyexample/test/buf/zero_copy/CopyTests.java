package ohmyexample.test.buf.zero_copy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author Williami
 * @description
 * @date 2022/2/18
 */
public class CopyTests {


    @Test
    public void testMergeByteBuf() {

        ByteBuf header = Unpooled.directBuffer();
        header.writeBytes("accept:text/plain".getBytes());
        ByteBuf body = Unpooled.directBuffer();
        body.writeBytes("name=VIA Gra".getBytes());
        int len = header.readableBytes();
        int bodyLen = body.readableBytes();

        ByteBuf compositeByteBuf = Unpooled.directBuffer(len + bodyLen);

        compositeByteBuf.writeBytes(header);
        compositeByteBuf.writeBytes(body);

        byte[] array = new byte[len + bodyLen];
        //int offset = compositeByteBuf.arrayOffset(); // 堆外内存  java.lang.UnsupportedOperationException: direct buffer
        compositeByteBuf.readBytes(array);
        System.out.println(new String(array, 0, bodyLen + len));

        compositeByteBuf.release();
        header.release();
        body.release();
    }


    @Test
    public void testCompositeByteBuf() {
        ByteBuf header = Unpooled.buffer();
        header.writeBytes("accept:text/plain".getBytes());
        ByteBuf body = Unpooled.directBuffer();
        body.writeBytes("name=VIA Gra".getBytes());

        int len = header.readableBytes();
        int bodyLen = body.readableBytes();
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(header, body);

        byte[] array = new byte[len + bodyLen];
        // 设置writerIndex
        compositeByteBuf.writerIndex(29);
        compositeByteBuf.readBytes(array);
        System.out.println(new String(array, 0, bodyLen + len));

        compositeByteBuf.release();
        //header.release();
        //body.release();
        System.out.println(header.refCnt());
        System.out.println(body.refCnt());
    }


    static Charset utf8 = Charset.forName("UTF-8");

    /**
     * 复用Header
     */
    @Test
    public void byteBufComposite() {
        // 默认compositeDirectBuffer
        CompositeByteBuf cbuf = ByteBufAllocator.DEFAULT.compositeBuffer();

        //消息头
        ByteBuf headerBuf = Unpooled.copiedBuffer("疯狂创客圈:", utf8);
        //消息体1
        ByteBuf bodyBuf = Unpooled.copiedBuffer("高性能Netty", utf8);

        cbuf.addComponents(headerBuf, bodyBuf);
        System.out.println("headerBuf " + headerBuf.refCnt()); // 1
        System.out.println("bodyBuf " + bodyBuf.refCnt()); // 1
        System.out.println("cbuf " + cbuf.refCnt()); // 1

        sendMsg(cbuf);        //在refCnt为0前, retain

        System.out.println("headerBuf " + headerBuf.refCnt()); // 1
        System.out.println("bodyBuf " + bodyBuf.refCnt()); // 1
        System.out.println("cbuf " + cbuf.refCnt()); // 1

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>Retain<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        headerBuf.retain();
        cbuf.release();
        System.out.println("headerBuf " + headerBuf.refCnt()); // 1
        System.out.println("bodyBuf " + bodyBuf.refCnt()); // 0
        System.out.println("cbuf " + cbuf.refCnt()); // 0

        System.out.println(">>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<");

        cbuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //消息体2
        bodyBuf = Unpooled.copiedBuffer("高性能学习社群", utf8);

        cbuf.addComponents(headerBuf, bodyBuf);
        sendMsg(cbuf);

        System.out.println("headerBuf " + headerBuf.refCnt()); // 1
        System.out.println("bodyBuf " + bodyBuf.refCnt()); // 1
        System.out.println("cbuf " + cbuf.refCnt()); // 1
        cbuf.release();

        System.out.println(">>>>>>>>>>>>>>cbnf#release");
        System.out.println("headerBuf " + headerBuf.refCnt()); // 0
        System.out.println("bodyBuf " + bodyBuf.refCnt()); // 0
        System.out.println("cbuf " + cbuf.refCnt()); // 0

        cbuf.addComponents(headerBuf, bodyBuf);
        sendMsg(cbuf);
        cbuf.release();
    }

    private void sendMsg(CompositeByteBuf cbuf) {
        //处理整个消息
        for (ByteBuf b : cbuf) {
            int length = b.readableBytes();
            byte[] array = new byte[length];
            // 将CompositeByteBuf中的数据统一复制到数组中
            // getBytes不改变readerIndex
            b.getBytes(b.readerIndex(), array);
            //处理一下数组中的数据
            System.out.print(new String(array, utf8));
        }
        System.out.println();
    }
}

