package com.example.appchat;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

public class PrimaryController {
    @FXML
    private ListView<String> messageList;
    @FXML
    private TextField messageInput;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    @FXML
    public void initialize() {
        connectToServer();
        new Thread(this::receiveMessages).start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeConnection));
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> messageList.getItems().add("Failed to connect to server."));
        }
    }

    private void receiveMessages() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                String finalMessage = message;
                Platform.runLater(() -> messageList.getItems().add(finalMessage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageInput.getText();
        if (!message.isEmpty()) {
            try {
                out.write(message + "\n");
                out.flush();
                Platform.runLater(() -> messageList.getItems().add("Me: " + message));
                messageInput.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
