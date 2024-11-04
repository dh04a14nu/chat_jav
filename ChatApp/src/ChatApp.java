import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("ChatUI.fxml");
        System.out.println("FXML URL: " + fxmlUrl); // Debugging line
        VBox root = FXMLLoader.load(fxmlUrl); // Ensure the FXML is loaded properly
        primaryStage.setScene(new Scene(root, 300, 450));
        primaryStage.setTitle("Chat Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
