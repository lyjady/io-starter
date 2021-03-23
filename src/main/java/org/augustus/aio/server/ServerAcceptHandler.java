package org.augustus.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author: linyongjin
 * @create: 2021-03-22 22:15:04
 */
public class ServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    private AsynchronousServerSocketChannel serverSocketChannel;

    public ServerAcceptHandler(AsynchronousServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
        if (this.serverSocketChannel.isOpen()) {
            this.serverSocketChannel.accept(null, this);
        }
        if (socketChannel.isOpen()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            ClientIoHandler clientIoHandler = new ClientIoHandler(socketChannel);
            Server.addClient(socketChannel);
            socketChannel.read(byteBuffer, byteBuffer, clientIoHandler);
        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {

    }
}
