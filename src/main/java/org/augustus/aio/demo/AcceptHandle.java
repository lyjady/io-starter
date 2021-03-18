package org.augustus.aio.demo;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: linyongjin
 * @create: 2021-03-17 21:46:04
 */
public class AcceptHandle implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private AsynchronousServerSocketChannel serverSocketChannel;

    public AcceptHandle(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
        if (this.serverSocketChannel.isOpen()) {
            this.serverSocketChannel.accept(null, this);
        }
        System.out.println("客户端已连接");
        if (socketChannel != null && socketChannel.isOpen()) {
            ClientHandle clientHandle = new ClientHandle(socketChannel);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Map<String, Object> properties = new HashMap<>(2);
            properties.put("type", "read");
            properties.put("buffer", buffer);
            socketChannel.read(buffer, properties, clientHandle);
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        exc.printStackTrace();
    }
}
