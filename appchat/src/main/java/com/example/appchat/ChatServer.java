package com.example.appchat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    // Map to store usernames and their corresponding ClientHandler instances
    private static final Map<String, ClientHandler> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a client to the map and broadcast the updated user list
    public static synchronized void addClient(String username, @SuppressWarnings("exports") ClientHandler handler) {
        clients.put(username, handler);
        broadcastUserList();
    }

    // Remove a client from the map and broadcast the updated user list
    public static synchronized void removeClient(String username) {
        clients.remove(username);
        broadcastUserList();
    }

    // Broadcast the list of users to all clients
    public static synchronized void broadcastUserList() {
        // Join all usernames into a comma-separated string
        String userList = String.join(",", clients.keySet());
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            try {
                // Filter out the current client from the user list
                String client = entry.getKey();
                ClientHandler handler = entry.getValue();

                String filteredList = userList.replace(client, "").replaceAll(",{2,}", ",");
                if (filteredList.startsWith(",")) {
                    filteredList = filteredList.substring(1); // Remove leading comma
                }

                // Send the filtered user list to the client
                handler.sendMessage("USER_LIST:" + filteredList);
            } catch (Exception e) {
                System.out.println("Error broadcasting user list to " + entry.getKey());
            }
        }
    }

    // Retrieve a client by username
    @SuppressWarnings("exports")
    public static synchronized ClientHandler getClient(String username) {
        return clients.get(username);
    }
}

// Handles communication with a single client
class ClientHandler implements Runnable {
    private final Socket socket;
    private String username;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);

            // Receive username
            username = in.readLine();
            ChatServer.addClient(username, this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CHAT:")) {
                    String[] parts = message.split(":", 3);
                    String targetUser = parts[1];
                    String chatMessage = parts[2];

                    ClientHandler targetClient = ChatServer.getClient(targetUser);
                    if (targetClient != null) {
                        targetClient.sendMessage("CHAT:" + username + ":" + chatMessage);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServer.removeClient(username);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Send a message to the client
    public void sendMessage(String message) {
        out.println(message);
    }
}
