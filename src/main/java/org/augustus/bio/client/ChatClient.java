package org.augustus.bio.client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * @author: linyongjin
 * @create: 2021-03-02 14:01:45
 */
public class ChatClient {

    public Socket socket;

    private final int port = 8080;

    private BufferedReader reader;

    private BufferedWriter writer;

    public void send(String message) throws IOException {
        if (writer != null && !socket.isOutputShutdown()) {
            writer.write(message);
            writer.flush();
        }
    }

    public String accept() throws IOException {
        if (reader != null && !socket.isInputShutdown()) {
            return reader.readLine();
        }
        return null;
    }

    @SuppressWarnings("all")
    public void start() {
        try {
            socket = new Socket("localhost", this.port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Thread thread = new Thread(new ChatInputHandler(this));
            thread.start();
            String message;
            while ((message = accept()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}
