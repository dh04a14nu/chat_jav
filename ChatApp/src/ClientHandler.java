import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.*;
import java.net.*;
import javafx.application.Platform;

public class ChatController {
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
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
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
                messageInput.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
