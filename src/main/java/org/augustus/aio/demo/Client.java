package org.augustus.aio.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * @author: linyongjin
 * @create: 2021-03-18 14:04:49
 */
public class Client {

    private AsynchronousSocketChannel socketChannel;

    @SuppressWarnings("all")
    private void start() {
        try {
            this.socketChannel = AsynchronousSocketChannel.open();
            Future<Void> connectFuture = this.socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
            connectFuture.get();
            new Thread(() -> {
                try {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        String message = consoleReader.readLine();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(message.getBytes());
                        Future<Integer> writeFuture = this.socketChannel.write(byteBuffer);
                        writeFuture.get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                Future<Integer> readFuture = this.socketChannel.read(byteBuffer);
                readFuture.get();
                System.out.println("服务器: " + new String(byteBuffer.array()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
