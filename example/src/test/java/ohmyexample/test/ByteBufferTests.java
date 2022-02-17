package ohmyexample.test;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author Williami
 * @description
 * @date 2022/2/11
 */
public class ByteBufferTests {

    public static void main(String[] args) {
        // 分配堆外内存,内存未初始化
        // 分配10字节的内存，返回值为内存基础地址，之后的读写都是以它作为基准
        long address = unsafe.allocateMemory(10);

        // 初始化堆外内存
        unsafe.setMemory(address, 10L, (byte) 1);

        // 传入内存地址设置值
        unsafe.putChar(address, (char) 97);
        //unsafe.putByte(address, (byte) 2);
        unsafe.putByte(address + 2, (byte) 3);
        unsafe.putByte(address + 3, (byte) 4);
        unsafe.putByte(address + 4, (byte) 5);


        // 根据内存地址获取值
        System.out.println(unsafe.getByte(address)); // 小顶端 97
        System.out.println(unsafe.getByte(address + 1)); // 0
        System.out.println(unsafe.getChar(address)); // a
        System.out.println(unsafe.getByte(address + 2)); // 4
        System.out.println(unsafe.getByte(address + 3)); // 5
        System.out.println(unsafe.getByte(address + 4)); // 1
        System.out.println(unsafe.getByte(address + 12));// 0


        // 手动释放堆外内存
        unsafe.freeMemory(address);
    }


    // 操作堆外内存
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
