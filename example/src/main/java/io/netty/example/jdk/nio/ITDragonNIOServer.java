package io.netty.example.jdk.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO 也称 New IO， Non-Block IO，非阻塞同步通信方式
 * 从BIO的阻塞到NIO的非阻塞，这是一大进步。功归于Buffer，Channel，Selector三个设计实现。
 * Buffer   ： 缓冲区。NIO的数据操作都是在缓冲区中进行。缓冲区实际上是一个数组。而BIO是将数据直接写入或读取到Stream对象。
 * Channel  ：  通道。NIO可以通过Channel进行数据的读，写和同时读写操作。
 * Selector ：  多路复用器。NIO编程的基础。多路复用器提供选择已经就绪状态任务的能力。
 * <p>
 * NIO过人之处：
 * 客户端和服务器通过Channel连接，而这些Channel都要注册在Selector。Selector通过一个线程不停的轮询这些Channel。找出已经准备就绪的Channel执行IO操作。
 * NIO通过一个线程轮询，实现千万个客户端的请求，这就是非阻塞NIO的特点。
 *
 * @author itdragon
 */
public class ITDragonNIOServer implements Runnable {

    private final int BUFFER_SIZE = 1024; // 缓冲区大小
    private final int PORT = 8888;        // 监听的端口
    private Selector selector;            // 多路复用器，NIO编程的基础，负责管理通道Channel
    // 缓冲区Buffer，和BIO的一个重要区别（NIO读写数据是在缓冲区中进行，而BIO是通过流的形式）
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    public ITDragonNIOServer() {
        startServer();
    }

    private void startServer() {
        try {
            // 1.开启多路复用器
            selector = Selector.open();
            // 2.打开服务器通道(网络读写通道)
            /**
             * ServerSocketChannel仅应用于服务端，而SocketChannel同时处于服务端和客户端。所以，对于一个连接，两端都有一个负责传输的SocketChannel。
             */
            ServerSocketChannel channel = ServerSocketChannel.open();
            // 3.设置服务器通道为非阻塞模式，true为阻塞，false为非阻塞
            channel.configureBlocking(false);
            // 4.绑定端口
            channel.socket().bind(new InetSocketAddress(PORT));
            // 5.把通道注册到多路复用器上，并监听阻塞事件
            /**
             * SelectionKey.OP_READ 	: 表示关注读数据就绪事件
             * SelectionKey.OP_WRITE 	: 表示关注写数据就绪事件
             * SelectionKey.OP_CONNECT: 表示关注socket channel的连接完成事件
             * SelectionKey.OP_ACCEPT : 表示关注server-socket channel的accept事件
             *
             * 注册到选择器的通道必须处于非阻塞模式下，否则将抛出IllegalBlockingModeException异常。这意味着，FileChannel不能与选择器一起使用，因为FileChannel只有阻塞模式，不能切换到非阻塞模式；而socket相关的所有通道都可以。其次，一个通道并不一定支持所有的四种IO事件。例如，服务器监听通道ServerSocketChannel仅支持Accept（接收到新连接）IO事件，而传输通道SocketChannel则不同，它不支持Ac-cept类型的IO事件。
             */
            channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server start >>>>>>>>> port :" + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 需要一个线程负责Selector的轮询
    @Override
    public void run() {
        while (true) {
            try {
                /**
                 * a.select() 阻塞到至少有一个通道在你注册的事件上就绪
                 * b.select(long timeOut) 阻塞到至少有一个通道在你注册的事件上就绪或者超时timeOut
                 * c.selectNow() 立即返回。如果没有就绪的通道则返回0
                 * select方法的返回值表示就绪通道的个数。
                 */
                // 1.多路复用器监听阻塞
                // select()方法的返回值是整数类型（int），表示发生了IO事件的数量，即从上一次select到这一次select之间有多少通道发生了IO事件，更加准确地说是发生了选择器感兴趣（注册过）的IO事件数。
                selector.select(); // 客户端执行后socketChannel.connect或者socketChannel.write，Server代码继续执行
                // 2.多路复用器已经选择的结果集
                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                // 3.不停的轮询
                while (selectionKeys.hasNext()) {
                    // 4.获取一个选中的key
                    SelectionKey key = selectionKeys.next();
                    // 5.获取后便将其从容器中移除
                    selectionKeys.remove();
                    // 6.只获取有效的key
                    if (!key.isValid()) {
                        continue;
                    }
                    // 阻塞状态处理：建立来自客户端的连接
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    // 可读状态处理
                    if (key.isReadable()) {
                        read(key); // 读取客户端发送的内容
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 设置阻塞，等待Client请求。
    // 在传统IO编程中，用的是ServerSocket和Socket。在NIO中采用的ServerSocketChannel和SocketChannel
    private void accept(SelectionKey selectionKey) {
        try {
            // 1.获取通道服务
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            // 2.执行阻塞方法
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 3.设置服务器通道为非阻塞模式，true为阻塞，false为非阻塞
            socketChannel.configureBlocking(false);
            // 4.把通道注册到多路复用器上，并设置读取标识
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey selectionKey) {
        try {
            // 1.清空缓冲区数据
            readBuffer.clear();
            // 2.获取在多路复用器上注册的通道
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            // 3.读取数据，返回
            int count = socketChannel.read(readBuffer);
            // 4.返回内容为-1 表示没有数据
            if (-1 == count) {
                selectionKey.channel().close();
                selectionKey.cancel();
                return;
            }
            // 5.有数据则在读取数据前进行复位操作
            // 复位position
            readBuffer.flip();
            // 6.根据缓冲区大小创建一个相应大小的bytes数组，用来获取值
            byte[] bytes = new byte[readBuffer.remaining()];
            // 7.接收缓冲区数据
            readBuffer.get(bytes);
            // 8.打印获取到的数据
            System.out.println("NIO Server : " + new String(bytes)); // 不能用bytes.toString()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new ITDragonNIOServer()).start();
    }

}