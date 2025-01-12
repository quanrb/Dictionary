package Dictionary.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Dictionary implements Initializable {
    @FXML
    public Tooltip tooltip1, tooltip2, tooltip3, tooltip4, tooltip5, tooltip6, tooltip7;
    @FXML
    public Button addBtn, translateBtn, searchBtn, quoteBtn, exitBtn, quizBtn, hangmanBtn;
    @FXML
    public AnchorPane container;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchBtn.setTooltip(tooltip1);
        addBtn.setTooltip(tooltip2);
        translateBtn.setTooltip(tooltip3);
        searchBtn.setOnAction(event -> showComponent("/View/SearchUI.fxml"));
        addBtn.setOnAction(event -> showComponent("/View/AdditionUI.fxml"));
        translateBtn.setOnAction(event -> showComponent("/View/TranslationUI.fxml"));
//        attachBtn.setOnAction(event -> showComponent("/View/ImageTranslationUI.fxml"));
        quoteBtn.setOnAction(event -> showComponent("/view/Danhngon.fxml"));
        quizBtn.setOnAction(event -> showComponent("/Quiz/quizView.fxml"));
        hangmanBtn.setOnAction(event -> showComponent("/Quiz/tempHangman.fxml"));


        tooltip1.setShowDelay(Duration.seconds(0.1));
        tooltip2.setShowDelay(Duration.seconds(0.1));
        tooltip3.setShowDelay(Duration.seconds(0.1));
        tooltip4.setShowDelay(Duration.seconds(0.1));
        tooltip5.setShowDelay(Duration.seconds(0.1));
        tooltip6.setShowDelay(Duration.seconds(0.1));
        tooltip7.setShowDelay(Duration.seconds(0.1));


//        try {
//            showComponent("/View/SearchUI.fxml");
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }

        exitBtn.setOnMouseClicked(e -> System.exit(0));
    }

    public void setNode(Node node) {
        container.getChildren().clear();
        container.getChildren().add(node);
    }

    @FXML
    public void showComponent(String path) {
        try {
            AnchorPane component = FXMLLoader.load(Objects.requireNonNull(Dictionary.class.getResource(path)));
            setNode(component);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
