package org.augustus.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: linyongjin
 * @create: 2021-03-22 22:11:19
 */
public class Server {

    private AsynchronousServerSocketChannel serverSocketChannel;

    private static List<AsynchronousSocketChannel> clients = new ArrayList<>();

    @SuppressWarnings("all")
    public void start() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AsynchronousChannelGroup group = AsynchronousChannelGroup.withThreadPool(executorService);
            this.serverSocketChannel = AsynchronousServerSocketChannel.open(group);
            this.serverSocketChannel.bind(new InetSocketAddress(8080));
            this.serverSocketChannel.accept(null, new ServerAcceptHandler(this.serverSocketChannel));
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static void addClient(AsynchronousSocketChannel socketChannel) {
        clients.add(socketChannel);
        System.out.println(clientName(socketChannel) + "连接了服务器");
    }

    public synchronized static void removeClient(AsynchronousSocketChannel socketChannel) {
        clients.remove(socketChannel);
        System.out.println(clientName(socketChannel) + "断开连接");
    }

    public static String clientName(AsynchronousSocketChannel socketChannel) {
        try {
            InetSocketAddress address = (InetSocketAddress) socketChannel.getRemoteAddress();
            return "客户端[" + address.getPort() + "]";
        } catch (IOException e) {
            e.printStackTrace();
            return "客户端[-1]";
        }
    }

    public static void forwardMessage(String message, AsynchronousSocketChannel socketChannel) {

    }
}
