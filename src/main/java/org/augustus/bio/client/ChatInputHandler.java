package org.augustus.bio.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: linyongjin
 * @create: 2021-03-02 14:09:13
 */
public class ChatInputHandler implements Runnable {

    private ChatClient chatClient;

    private BufferedReader reader;

    private final String QUIT = "quit";

    public ChatInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        try {
            String message;
            while (true) {
                if ((message = reader.readLine()) != null) {
                    chatClient.send(message + "\n");
                    if (QUIT.equalsIgnoreCase(message)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
