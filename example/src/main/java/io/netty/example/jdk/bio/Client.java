package io.netty.example.jdk.bio;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author Williami
 * @description
 * @date 2022/2/9
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8082);
        socket.getOutputStream().write("HelloServer".getBytes());
        socket.getOutputStream().flush();
        //socket.getOutputStream().close();
        System.out.println("Client waiting msg back...");
        byte[] buffer = new byte[1024];
        TimeUnit.SECONDS.sleep(20);
        int read = socket.getInputStream().read(buffer);
        System.out.println(new String(buffer, 0, read));
        socket.close();
    }
}
