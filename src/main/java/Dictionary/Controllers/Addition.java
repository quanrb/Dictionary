package Dictionary.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Addition {

    @FXML
    private TextArea exampleTextArea;

    @FXML
    private Button addButton;

    @FXML
    private TextArea newWord;

    @FXML
    private TextArea meaning;
    @FXML
    private TextArea pronunciation;
    @FXML
    private TextArea synonym;

    @FXML
    private TextArea antonyms;


    public void resetText() {// xóa nội dung văn bản của một số trường TextArea.
        exampleTextArea.setText("");
        meaning.setText("");
        pronunciation.setText("");
    }

    public void handleAdd()  {//ấy thông tin về một từ từ cơ sở dữ liệu
        String word = newWord.getText();
        if (word.isEmpty() || word.isBlank()) {
            resetText();
            return;
        }
    }

    @FXML
    protected void HandleClickBtn() {
        String word = newWord.getText();
        String ex = exampleTextArea.getText();
        String mn = meaning.getText();
        String pro = pronunciation.getText();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Tu tieng anh moi:");
        alert.showAndWait();
    }
}