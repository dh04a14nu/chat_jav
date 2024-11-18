package com.example.appchat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private ChatClient chatClient;
    private ObservableList<String> onlineUsers;
    private TextArea chatArea;
    private String selectedUser;
    private String username;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        // Username input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");
        String username = dialog.showAndWait().orElse("Anonymous");

        // Connect to the server
        chatClient = new ChatClient("localhost", 12345);
        try {
            chatClient.connect(username);
        } catch (IOException e) {
            showAlert("Error", "Failed to connect to the server.");
            return;
        }

        // Online users list
        onlineUsers = FXCollections.observableArrayList();
        ListView<String> userListView = new ListView<>(onlineUsers);
        userListView.setPrefWidth(200);
        userListView.setOnMouseClicked(event -> {
            selectedUser = userListView.getSelectionModel().getSelectedItem();
            chatArea.clear();
            chatArea.appendText("Chat with " + selectedUser + ":\n");
        });

        // Chat area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        // Message input
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            if (selectedUser != null) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    chatClient.sendMessage("CHAT:" + selectedUser + ":" + message);
                    chatArea.appendText("You: " + message + "\n");
                    messageField.clear();
                }
            } else {
                showAlert("Error", "Select a user to chat with.");
            }
        });

        // Layout
        VBox chatBox = new VBox(10, chatArea, new HBox(10, messageField, sendButton));
        chatBox.setPadding(new Insets(10));
        SplitPane splitPane = new SplitPane(new VBox(userListView), chatBox);

        // Start background listener for messages
        new Thread(this::listenForMessages).start();

        // Show the UI
        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Application");
        primaryStage.show();
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = chatClient.readMessage()) != null) {
                if (message.startsWith("USER_LIST:")) {
                    String[] users = message.substring(10).split(",");
                    Platform.runLater(() -> {
                        onlineUsers.setAll(users);
                        onlineUsers.remove(username);
                        // onlineUsers.removeIf(user -> user.equals(chatClient)); // Remove the
                        // logged-in user
                    });
                } else if (message.startsWith("CHAT:")) {
                    String[] parts = message.split(":", 3);
                    String sender = parts[1];
                    String chatMessage = parts[2];
                    Platform.runLater(() -> {
                        if (sender.equals(selectedUser)) {
                            chatArea.appendText(sender + ": " + chatMessage + "\n");
                        }
                    });
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Error", "Disconnected from the server."));
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        chatClient.disconnect();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
