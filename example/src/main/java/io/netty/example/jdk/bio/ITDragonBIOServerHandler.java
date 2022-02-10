package io.netty.example.jdk.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * server 任务处理类
 *
 * @author itdragon
 */
public class ITDragonBIOServerHandler implements Runnable {

    private Socket socket;

    public ITDragonBIOServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(this.socket.getOutputStream(), true);
//        System.out.println("打印客户端传来的数据 : " + reader.readLine());  加了这行代码，会导致程序无法进行 ，细节
            String body = null;
            while (true) {
                body = reader.readLine(); // 若客户端用的是 writer.print() 传值，那readerLine() 是不能获取值，细节
                if (null == body) {
                    break;
                }
                System.out.println("server服务端接收参数 : " + body);
                writer.println(body + " = " + CalculatorUtil.cal(body).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
            try {
                if (null != reader) {
                    reader.close();
                }
                if (null != this.socket) {
                    this.socket.close();
                    this.socket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static class CalculatorUtil {

        private final static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

        public static Object cal(String expression) {
            try {
                return jse.eval(expression);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
