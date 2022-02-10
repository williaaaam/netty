package io.netty.example.jdk.bio;


import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Williami
 * @description
 * @date 2022/2/9
 */
public class Server {

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket();) {
            serverSocket.bind(new InetSocketAddress("localhost", 8082));
            while (true) {
                // 阻塞调用
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    handle(socket);
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void handle(Socket socket) {
        try {
            byte[] buffer = new byte[1024];
            // 输入通道：read阻塞
            int len = socket.getInputStream().read(buffer);
            System.out.println("Client -> Server : " + new String(buffer, 0, len));
            // 输出通道
            OutputStream outputStream = socket.getOutputStream();
            // write 阻塞
            outputStream.write("Hello Cient".getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
