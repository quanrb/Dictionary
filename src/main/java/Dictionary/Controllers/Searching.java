package Dictionary.Controllers;

import Dictionary.Alerts.AlertStyler;
import Dictionary.Alerts.DialogStyler;
import Dictionary.Models.English;
import Dictionary.Services.StringUtils;
import Dictionary.Services.VoiceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static Dictionary.DBConfig.englishDAO;
import static Dictionary.MyDictionaryApp.AppStage;

public class Searching implements Initializable {
    private ArrayList<String> searchedWords = new ArrayList<>();
    @FXML
    public ListView<String> searchResultsListView;
    @FXML
    public TextField searchBox;
    @FXML
    public ObservableList<String> observableWord = FXCollections.observableArrayList();
    @FXML
    public Label notAvailableLabel = new Label("");

    @FXML
    public Label countRes = new Label("");
    @FXML
    public TextArea wordDefinition = new TextArea();

    @FXML
    public English currentWord = new English();
    @FXML
    private Tooltip tooltip1,tooltip2,tooltip3;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        searchResultsListView.setOnMouseClicked(event -> {
            try {
                if(!englishDAO.sortedWord()){
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String selectedWord = searchResultsListView.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                //searchedWords.add(searchTerm);
                saveSearchWordToFile(selectedWord);
                try {
                    English english = englishDAO.getWord(selectedWord);
                    if (english != null) {
                        currentWord = english;
                        wordDefinition.setText(englishDAO.renderDefinition(english));
                    } else {
                        wordDefinition.setText("Definition not found for: " + selectedWord);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    wordDefinition.setText("Error fetching definition.");
                }
            }
        });
        tooltip1.setShowDelay(Duration.seconds(0.1));
        tooltip2.setShowDelay(Duration.seconds(0.1));
        tooltip3.setShowDelay(Duration.seconds(0.1));
    }

    @FXML
    public void handleSearch(KeyEvent keyEvent) {
        String searchTerm = searchBox.getText();
        if (searchTerm.isEmpty() || searchTerm.isBlank()) {
            countRes.setText("");
            searchResultsListView.getItems().clear();
            notAvailableLabel.setText("");
            wordDefinition.setText("");
            return;
        }
        try {
            searchResultsListView.getItems().clear();
            searchTerm = StringUtils.normalizeString(searchTerm);
            List<English> list = englishDAO.containWord(searchTerm);
            if (list.isEmpty()) {
                clearSearchResultsView();
                notAvailableLabel.setText("Sorry, We don't have word " + searchTerm);
                return;
            }

            currentWord = list.get(0);
            wordDefinition.setText(englishDAO.renderDefinition(currentWord));

            for (English english : list) {
                System.out.println(english.getWord());
                searchResultsListView.getItems().add(english.getWord());
            }
            Collections.sort(searchResultsListView.getItems());
            countRes.setText(searchResultsListView.getItems().size() + " Related Results");
        } catch (SQLException e) {
            e.printStackTrace();
            wordDefinition.setText("Error fetching definition.");
        }
    }

    public void saveSearchWordToFile(String word) {
        // Đường dẫn của tệp tin để lưu từ vừa tìm kiếm
        String filePath = "D:\\Dictionary\\src\\main\\java\\Dictionary\\Hangman\\Words.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Ghi từ tìm kiếm vào cuối tệp tin
            writer.write(word);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý nếu có lỗi khi ghi vào tệp tin
        }
    }

    public ArrayList<String> getSearchedWords() {
        return searchedWords;
    }

    public void setSearchedWords(ArrayList<String> searchedWords) {
        this.searchedWords = searchedWords;
    }
    @FXML
    public void speakWord() {
        VoiceService.playVoice(currentWord.getWord());
    }


    public void deleteWord(ActionEvent actionEvent) throws SQLException {
        if (currentWord.getWord().isEmpty()) {
            return;
        }
        if (englishDAO.deleteWord(currentWord.getWord())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            AlertStyler.on(alert)
                    .applyVintageStyle()
                    .setTitle("Success")
                    .setWindowTitle("Delete Success")
                    .setButtonStyle()
                    .setMinSize()
                    .build();
            alert.setContentText("Xóa thành công");
            alert.showAndWait();
            searchBox.setText("");
            searchResultsListView.getItems().remove(currentWord.getWord());
            countRes.setText(searchResultsListView.getItems().size() + " Related Results");
            notAvailableLabel.setText("");
            if(!searchResultsListView.getItems().isEmpty()){
                currentWord = englishDAO.getWord(searchResultsListView.getItems().get(0));
                wordDefinition.setText(englishDAO.renderDefinition(currentWord));
            } else{
                wordDefinition.setText("");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            AlertStyler.on(alert)
                    .applyVintageStyle()
                    .setTitle("Failed")
                    .setWindowTitle("Delete Success")
                    .setButtonStyle()
                    .setMinSize()
                    .build();
            alert.setContentText("Xóa thất bại");
            alert.showAndWait();
        }
    }

    public void updateWord() {
        if (searchResultsListView.getItems().size() <=0){
            return;
        }
        Dialog<String> dialog = new Dialog<>();
        DialogStyler.on(dialog)
                .applyVintageStyle()
                .setTitle("Update Word Information")
                .setWindowTitle("Update Word Information")
                .setMinSize()
                .build();
        dialog.setHeaderText(null);

        Label nameLabel = new Label("Word:");
        TextField nameField = new TextField(currentWord.getWord());

        Label definitionLabel = new Label("Definition:");
        TextArea definitionField = new TextArea(currentWord.getMeaning());
        definitionField.setWrapText(true);

        Label typeLabel = new Label("Part of speech:");
        TextField typeField = new TextField(currentWord.getType());

        Label pronunciationLabel = new Label("Pronunciation:");
        TextField pronunciationField = new TextField(currentWord.getPronunciation());

        Label exampleLabel = new Label("Example:");
        TextArea exampleField = new TextArea(currentWord.getExample());
        exampleField.setWrapText(true);

        Label synonymLabel = new Label("Synonym:");
        TextField synonymField = new TextField(currentWord.getSynonym());

        Label antonymLabel = new Label("Antonym:");
        TextField antonymField = new TextField(currentWord.getAntonyms());

        GridPane gridPane = new GridPane();
        gridPane.add(nameLabel, 1, 1);
        gridPane.add(nameField, 2, 1);
        gridPane.add(definitionLabel, 1, 2);
        gridPane.add(definitionField, 2, 2);
        gridPane.add(typeLabel, 1, 3);
        gridPane.add(typeField, 2, 3);
        gridPane.add(pronunciationLabel, 1, 4);
        gridPane.add(pronunciationField, 2, 4);
        gridPane.add(exampleLabel, 1, 5);
        gridPane.add(exampleField, 2, 5);
        gridPane.add(synonymLabel, 1, 6);
        gridPane.add(synonymField, 2, 6);
        gridPane.add(antonymLabel, 1, 7);
        gridPane.add(antonymField, 2, 7);

        dialog.getDialogPane().setContent(gridPane);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
        dialog.initOwner(AppStage);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return "OK";
            } else if (dialogButton == cancelButton) {
                return "Cancel";
            } else {
                return null;
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response.equals("OK")) {
                String word = nameField.getText();
                String definition = definitionField.getText();
                String type = typeField.getText();
                String pronunciation = pronunciationField.getText();
                String example = exampleField.getText();
                String synonym = synonymField.getText();
                String antonym = antonymField.getText();
                English english = new English(word, type, definition, pronunciation, example, synonym, antonym);
                try {
                    englishDAO.updateWord(english);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    AlertStyler.on(successAlert)
                            .applyVintageStyle()
                            .setTitle("Update Successful")
                            .setWindowTitle("Update Success")
                            .setButtonStyle()
                            .setMinSize()
                            .build();
                    successAlert.setContentText("Word information updated successfully!");
                    successAlert.showAndWait();
                    currentWord = english;
                    wordDefinition.setText(englishDAO.renderDefinition(currentWord));
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    AlertStyler.on(errorAlert)
                            .applyVintageStyle()
                            .setTitle("Update Failed")
                            .setWindowTitle("Update Failed")
                            .setButtonStyle()
                            .setMinSize()
                            .build();
                    errorAlert.setHeaderText("Failed to update word information.");
                }
            }
            else {
                dialog.close();
            }
        });

    }

    public void clearSearchResultsView() {
        searchResultsListView.getItems().clear();
        countRes.setText(searchResultsListView.getItems().size() + " Related Results");
        wordDefinition.setText("");
    }

    public void initializeWithStage(Stage stage) {
        this.initializeWithStage(stage);
    }
}
