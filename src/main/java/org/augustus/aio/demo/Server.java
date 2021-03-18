package org.augustus.aio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * @author: linyongjin
 * @create: 2021-03-17 21:43:16
 */
public class Server {

    private AsynchronousServerSocketChannel serverSocketChannel;

    public void start() throws IOException {
        try {
            this.serverSocketChannel = AsynchronousServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(8080));
            while (true) {
                this.serverSocketChannel.accept(null, new AcceptHandle(this.serverSocketChannel));
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            serverSocketChannel.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
    }
}
