package org.augustus.copydemo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * @author: linyongjin
 * @create: 2021-03-04 21:31:33
 */
public class CopyFileRunnerDemo {

    public static void main(String[] args) {
        CopyFileRunner ioStream = (source, target) -> {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(source);
                outputStream = new FileOutputStream(target);
                int result;
                while ((result = inputStream.read()) != -1) {
                    outputStream.write(result);
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(inputStream, outputStream);
            }
        };

        CopyFileRunner ioBufferStream = (source, target) -> {
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(target));
                byte[] buffer = new byte[2048];
                int result;
                while ((result = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(bufferedInputStream, bufferedOutputStream);
            }
        };

        CopyFileRunner nioBufferedCopy = (source, target) -> {
            FileChannel readChannel = null;
            FileChannel writeChannel = null;
            try {
                readChannel = new FileInputStream(source).getChannel();
                writeChannel = new FileOutputStream(target).getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                while (readChannel.read(buffer) != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        writeChannel.write(buffer);
                    }
                    buffer.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(readChannel, writeChannel);
            }
        };

        CopyFileRunner nioChannelCopy = (source, target) -> {
            FileChannel readChannel = null;
            FileChannel writeChannel = null;
            try {
                readChannel = new FileInputStream(source).getChannel();
                writeChannel = new FileOutputStream(target).getChannel();
                readChannel.transferTo(0, readChannel.size(), writeChannel);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(readChannel, writeChannel);
            }
        };

        File source = new File("/Users/linyongjin/IdeaProjects/io-starter/src/main/resources/a.doc");
        File target = new File("/Users/linyongjin/IdeaProjects/io-starter/src/main/resources/b.doc");
        long startTime = System.currentTimeMillis();
        nioChannelCopy.copyFile(source, target);
        System.out.println(System.currentTimeMillis() - startTime);
    }

    public static void close(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
