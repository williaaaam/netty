package ohmyexample.test;

import java.nio.ByteBuffer;

/**
 * @author Williami
 * @description
 * @date 2022/2/11
 */
public class DirectByteBufferTests {


    public static void main(String[] args) throws InterruptedException {
        directByteBuffer();
    }

    static int position = 1;


    private static void directByteBuffer() {

        // 分配1M堆外内存
        // 当DirectByteBuffer成为垃圾对象的时候，调用对应的Cleaner#clean方法进行回收，即释放堆外内存
        ByteBuffer direct = ByteBuffer.allocateDirect(1024 * 1024);

        //TimeUnit.SECONDS.sleep(10L);

        direct.putChar('Z');
        // direct#getChar()不好用，从当前position读取两个字节组装成char,然后position再增加两个字节
        //direct.getChar();

        System.out.println(direct.getChar(0));

        // 手动回收对外内存
        direct.clear().clear();

    }
}
