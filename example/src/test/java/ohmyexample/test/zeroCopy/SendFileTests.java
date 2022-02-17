package ohmyexample.test.zeroCopy;

import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Williami
 * @description
 * @date 2022/2/14
 */
public class SendFileTests {


    public static void main(String[] args) {
        try {
            FileChannel readChannel = FileChannel.open(Paths.get("F:\\MI\\netty\\example\\src\\test\\java\\ohmyexample\\test\\zeroCopy\\source.txt"), StandardOpenOption.READ);
            long len = readChannel.size();
            long position = readChannel.position();

            FileChannel writeChannel = FileChannel.open(Paths.get("F:\\MI\\netty\\example\\src\\test\\java\\ohmyexample\\test\\zeroCopy\\target.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            // 数据传输
            // len: 最多传输字节个数
            // 输入文件起始位置

            // Windows不支持sendfile(),走mmap，如果mmap失败就走堆外内存
            readChannel.transferTo(position, len, writeChannel);
            readChannel.close();
            writeChannel.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
