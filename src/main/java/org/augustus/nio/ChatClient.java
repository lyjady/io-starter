package org.augustus.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author: linyongjin
 * @create: 2021-03-14 18:30:19
 */
public class ChatClient {

    private final int CAPACITY = 1024;

    private final String QUIT = "quit";

    private Selector selector;

    private final ByteBuffer readBuffer = ByteBuffer.allocate(CAPACITY);

    private final ByteBuffer writeBuffer = ByteBuffer.allocate(CAPACITY);

    private SocketChannel client;

    private void start() {
        try {
            this.client = SocketChannel.open();
            this.client.configureBlocking(false);
            this.selector = Selector.open();
            this.client.register(this.selector, SelectionKey.OP_CONNECT);
            this.client.connect(new InetSocketAddress("127.0.0.1", 8080));
            while (true) {
                this.selector.select();
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                handle(selectionKeys);
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClosedSelectorException e) {

        } finally {
            try {
                this.selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(Set<SelectionKey> selectionKeys) throws IOException {
        for (SelectionKey selectionKey : selectionKeys) {
            if (selectionKey.isConnectable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                if (socketChannel.isConnectionPending()) {
                    socketChannel.finishConnect();
                    new Thread(new ChatInputHandler(this)).start();
                }
                socketChannel.register(this.selector, SelectionKey.OP_READ);
            } else if (selectionKey.isReadable()) {
                String message = receive((SocketChannel) selectionKey.channel());
                if (message.isEmpty()) {
                    this.selector.close();
                } else {
                    System.out.println(message);
                }
            }
        }
    }

    private String receive(SocketChannel channel) throws IOException {
        this.readBuffer.clear();
        while (channel.read(this.readBuffer) > 0) {}
        this.readBuffer.flip();
        return String.valueOf(StandardCharsets.UTF_8.decode(this.readBuffer));
    }


    public void send(String message) throws IOException {
        if (message.isEmpty()) {
            return;
        }
        this.writeBuffer.clear();
        this.writeBuffer.put(StandardCharsets.UTF_8.encode(message));
        this.writeBuffer.flip();
        if (this.writeBuffer.hasRemaining()) {
            this.client.write(this.writeBuffer);
        }
        if (QUIT.equalsIgnoreCase(message)) {
            this.selector.close();
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
