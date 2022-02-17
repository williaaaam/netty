package ohmyexample.test.zeroCopy;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Williami
 * @description
 * @date 2022/2/14
 */
public class MmapTests {

    /**
     * mmap+write
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Channel相当于操作系统的内核缓冲区
            FileChannel readChannel = FileChannel.open(Paths.get("F:\\MI\\netty\\example\\src\\test\\java\\ohmyexample\\test\\zeroCopy\\source.txt"), StandardOpenOption.READ);
            // Buffer相当于操作系统的用户缓冲区
            // MappedByteBuffer只能通过调用FileChannel的map()取得，再没有其他方式。

            // map在打开的文件和MappedByteBuffer之间建立虚拟内存映射，map方法底层是通过mmap实现的，因此将文件内存从磁盘读取到内核缓冲区后，用户空间和内核空间共享该缓冲区
            // fileSize >= position+size
            MappedByteBuffer data = readChannel.map(FileChannel.MapMode.READ_ONLY, 0, 10);
            FileChannel writeChannel = FileChannel.open(Paths.get("F:\\MI\\netty\\example\\src\\test\\java\\ohmyexample\\test\\zeroCopy\\target.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            //数据传输
            writeChannel.write(data);
            readChannel.close();
            writeChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
