package org.augustus.bio.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: linyongjin
 * @create: 2021-03-01 20:37:38
 */
public class ChatServer {

    private ServerSocket serverSocket = null;

    private final int port = 8080;

    private final ConcurrentMap<Integer, Writer> clients;

    public ChatServer() {
        clients = new ConcurrentHashMap<>();
    }

    public void addClient(Socket socket) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot not null");
        }
        int port = socket.getPort();
        System.out.printf("客户端[%s]已连接", port);
        System.out.println();
        Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        clients.put(port, writer);
    }

    public void removeClient(int port) {
        if (clients.containsKey(port)) {
            System.out.printf("客户端[%s]以断开连接", port);
            System.out.println();
            clients.remove(port);
        }
    }

    public void forwardMessage(Socket socket, String message) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot not null");
        }
        for (Map.Entry<Integer, Writer> entry : clients.entrySet()) {
            if (entry.getKey() != socket.getPort()) {
                Writer writer = entry.getValue();
                writer.write(String.format("客户端[%s]: %s", socket.getPort(), message));
                writer.flush();
            }
        }
    }

    private synchronized void close() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    @SuppressWarnings("all")
    public void start() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            System.out.printf("服务器已启动, 监听%s端口", this.port);
            System.out.println();
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ChatHandler(this, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}
