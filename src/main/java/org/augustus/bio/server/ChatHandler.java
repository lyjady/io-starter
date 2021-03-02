package org.augustus.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author: linyongjin
 * @create: 2021-03-01 20:48:35
 */
public class ChatHandler implements Runnable {

    private ChatServer chatServer;

    private Socket socket;

    private static final String QUIT = "quit";

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    private boolean isQuited(String message) {
        return QUIT.equalsIgnoreCase(message);
    }

    @Override
    public void run() {
        try {
            chatServer.addClient(socket);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.printf("客户端[%s]的消息: %s", socket.getPort(), message);
                System.out.println();
                this.chatServer.forwardMessage(socket, message + "\n");
                if (isQuited(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.chatServer.removeClient(this.socket.getPort());
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
