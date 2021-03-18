package org.augustus.aio.demo;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;

/**
 * @author: linyongjin
 * @create: 2021-03-17 21:48:04
 */
public class ClientHandle implements CompletionHandler<Integer, Map<String, Object>> {

    private AsynchronousSocketChannel socketChannel;

    public ClientHandle(AsynchronousSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void completed(Integer result, Map<String, Object> attachment) {
        String type = attachment.get("type").toString();
        if ("read".equalsIgnoreCase(type)) {
            ByteBuffer buffer = (ByteBuffer) attachment.get("buffer");
            attachment.put("type", "write");
            buffer.flip();
            this.socketChannel.write(buffer, attachment, this);
            buffer.clear();
        } else if ("write".equals(type)) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            attachment.put("type", "read");
            attachment.put("buffer", byteBuffer);
            this.socketChannel.read(byteBuffer, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, Map<String, Object> attachment) {
        exc.printStackTrace();
    }
}
