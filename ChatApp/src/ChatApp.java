import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = FXMLLoader.load(getClass().getResource("ChatUI.fxml"));
        primaryStage.setTitle("Chat Application");
        primaryStage.setScene(new Scene(root, 300, 450));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
