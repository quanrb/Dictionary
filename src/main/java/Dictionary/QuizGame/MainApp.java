package Dictionary.QuizGame;
/** Main Class */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/Quiz/temp.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 816, 688);
        stage.setTitle("Trivia Game");
        stage.setScene(scene);
        stage.show();
    }

     public static void main(String[] args) {
     launch();
     }
}