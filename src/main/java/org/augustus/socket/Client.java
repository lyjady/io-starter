package org.augustus.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author: linyongjin
 * @create: 2021-02-25 22:01:16
 */
public class Client {

    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader reader = null;
        Scanner scanner = new Scanner(System.in);
        BufferedWriter writer = null;
        try {
            socket = new Socket("127.0.0.1", 8080);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message;
            while (true) {
                System.out.print("请输入要发送的内容: ");
                message = scanner.nextLine();
                writer.write(message + "\n");
                writer.flush();
                if ("QUIT".equalsIgnoreCase(message)) {
                    System.out.println("与服务器断开连接");
                    break;
                }
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
