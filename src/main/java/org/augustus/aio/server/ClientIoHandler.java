package org.augustus.aio.server;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author: linyongjin
 * @create: 2021-03-22 22:18:33
 */
public class ClientIoHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel socketChannel;

    public ClientIoHandler(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if (byteBuffer != null) {
            if (result <= 0) {
                Server.removeClient(this.socketChannel);
            } else {
                byteBuffer.flip();
                String message = receive(byteBuffer);
                byteBuffer.clear();
                System.out.println(Server.clientName(this.socketChannel) + ": " + message);
                Server.forwardMessage(message, this.socketChannel);
                if ("quit".equalsIgnoreCase(message)) {
                    Server.removeClient(this.socketChannel);
                } else {
                    this.socketChannel.read(byteBuffer, byteBuffer, this);
                }
            }
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }

    private String receive(ByteBuffer byteBuffer) {
        CharBuffer decode = StandardCharsets.UTF_8.decode(byteBuffer);
        return String.valueOf(decode);
    }

}
