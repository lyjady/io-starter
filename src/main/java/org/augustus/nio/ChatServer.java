package org.augustus.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author: linyongjin
 * @create: 2021-03-09 20:41:44
 */
public class ChatServer {

    private int port;

    private final int CAPACITY = 1024;

    private final String QUIT = "quit";

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(CAPACITY);

    private final ByteBuffer writeBuffer = ByteBuffer.allocate(CAPACITY);

    public ChatServer() {
        this(8080);
    }

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            // 构建ServerSocketChannel并绑定端口
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.socket().bind(new InetSocketAddress(this.port));
            // 构建Selector
            this.selector = Selector.open();
            // 向Selector注册ServerSocketChannel并监听Accept事件
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            while (true) {
                this.selector.select();
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    handles(selectionKey);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close(this.selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handles(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            SocketChannel socketChannel = this.serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            System.out.printf("客户端%s已经连接到服务器", socketChannel.socket().getPort());
            System.out.println();
        } else if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String message = this.readMessageFormSocketChannel(socketChannel);
            if (message.isEmpty()) {
                selectionKey.cancel();
                this.selector.wakeup();
            } else {
                System.out.printf("客户端%s的消息: %s", socketChannel.socket().getPort(), message);
                System.out.println();
                if (QUIT.equalsIgnoreCase(message)) {
                    selectionKey.cancel();
                    this.selector.wakeup();
                    System.out.printf("客户端%s已断开", socketChannel.socket().getPort());
                    System.out.println();
                }
                this.forwardMessage(socketChannel, message);
            }
        }
    }

    private String readMessageFormSocketChannel(SocketChannel socketChannel) throws IOException {
        this.readBuffer.clear();
        while (socketChannel.read(readBuffer) > 0) {}
        this.readBuffer.flip();
        return String.valueOf(StandardCharsets.UTF_8.decode(readBuffer));
    }

    private void forwardMessage(SocketChannel socketChannel, String message) throws IOException {
        Set<SelectionKey> selectionKeys = this.selector.keys();
        for (SelectionKey selectionKey : selectionKeys) {
            if (selectionKey.isValid() && !selectionKey.channel().equals(this.serverSocketChannel)) {
                if (!selectionKey.channel().equals(socketChannel)) {
                    this.writeBuffer.clear();
                    this.writeBuffer.put(StandardCharsets.UTF_8.encode(socketChannel.socket().getPort() + ": " + message));
                    this.writeBuffer.flip();
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    if (this.writeBuffer.hasRemaining()) {
                        channel.write(this.writeBuffer);
                    }
                }
            }
        }
    }

    private void close(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}
