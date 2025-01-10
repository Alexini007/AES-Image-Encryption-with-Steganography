package controllers;
import shared.HelperFunctions;
import models.KeyManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Base64;

public class ViewController {

    @FXML
    private TextField passwordField;
    @FXML
    private Button uploadButton;
    @FXML
    private Button generateButton;
    @FXML
    private Label generatedKeyLabel;
    @FXML
    private Label selectedImageLabel;
    @FXML
    private TextField embedTextField;
    @FXML
    private TextField generatedKeyField;
    @FXML
    private Label extractedTextField;
    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private Button encodeButton;

    @FXML
    private Button decodeButton;

    @FXML
    private ImageView originalImageView;

    @FXML
    private Label originalSizeLabel;

    private String originalFormat;

    private String userPassword;
    private String selectedImagePath;

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );

        // Get the current stage
        Stage stage = (Stage) originalImageView.getScene().getWindow();

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            // Display the selected image in the ImageView
            Image image = new Image(selectedFile.toURI().toString());
            originalImageView.setImage(image);
            selectedImagePath = selectedFile.getAbsolutePath();

            // Update the label to show the selected image name
            selectedImageLabel.setText("Selected image: " + selectedFile.getName());
        } else {
            showAlert("No file selected.");
        }
    }

    @FXML
    private void handleEncrypt() {
        String password = passwordField.getText();
        if (!validatePasswordAndImage(password, "encrypt")) return;

        String imageFormat = getImageFormat(selectedImagePath);
        if (imageFormat == null) {
            showAlert("Unsupported image format. Please select a valid image.");
            return;
        }

        String extension = imageFormat.equals("png") ? ".png" : ".bmp";
        Stage stage = (Stage) originalImageView.getScene().getWindow();
        File outputFile = showSaveDialog(stage, "encrypted_image" + extension, "*.png", "*.bmp");
        if (outputFile != null) {
            String outputFilePath = appendFileExtension(outputFile.getAbsolutePath(), extension);
            if (MainController.buttonImageEncryption(password, selectedImagePath, outputFilePath)) {
                clear();
                showAlert("Image encrypted successfully!\nSaved as: " + outputFile.getName());
                generatedKeyField.clear();
            } else {
                showAlert("Image encryption failed.");
            }
        }
    }

    private String getImageFormat(String filePath) {
        String lowerCasePath = filePath.toLowerCase();
        if (lowerCasePath.endsWith(".png")) {
            return "png";
        } else if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg")) {
            return "jpg";
        } else if (lowerCasePath.endsWith(".bmp")) {
            return "bmp";
        }
        return null;
    }


    @FXML
    private void handleDecrypt() {
        String password = passwordField.getText();
        if (!validatePasswordAndImage(password, "decrypt")) return;

        Stage stage = (Stage) originalImageView.getScene().getWindow();
        File outputFile = showSaveDialog(stage, "decrypted_image.png", "*.png");
        if (outputFile != null) {
            String outputFilePath = appendFileExtension(outputFile.getAbsolutePath(), ".png");
            if (MainController.buttonImageDecryption(password, selectedImagePath, outputFilePath)) {
                originalImageView.setImage(new Image(outputFile.toURI().toString()));
                selectedImageLabel.setText("Decrypted image: " + outputFile.getName());
                showAlert("Image decrypted successfully!\nSaved as: " + outputFile.getName());
                generatedKeyField.clear();
            } else {
                showAlert("Image decryption failed.");
            }
        }
    }

    @FXML
    private void handleEncode() {
        String password = passwordField.getText();
        if (!validatePasswordAndImage(password, "encode")) return;

        String textToEmbed = embedTextField.getText();
        if (isTextFieldEmpty(textToEmbed)) return;

        Stage stage = (Stage) originalImageView.getScene().getWindow();
        File outputFile = showSaveDialog(stage, "encoded_image.png", "*.png", "*.bmp");
        if (outputFile != null) {
            String outputFilePath = appendFileExtension(outputFile.getAbsolutePath(), ".png");
            if (MainController.buttonEncode(password, selectedImagePath, textToEmbed, outputFilePath)) {
                clear();
                showAlert("Text embedded successfully!\nSaved as: " + outputFile.getName());
                generatedKeyField.clear();
            } else {
                showAlert("Text embedding failed.");
            }
        }
    }

    @FXML
    private void handleDecode() {
        String password = passwordField.getText();
        if (!validatePasswordAndImage(password, "decode")) return;

        String extractedText = MainController.buttonDecode(password, selectedImagePath);
        if (extractedText == null || extractedText.isEmpty()) {
            showAlert("No text found in the image or incorrect password.");
        } else {
            extractedTextField.setText("Decoded message: " + extractedText);
            showAlert("Text successfully decoded!");
            generatedKeyField.clear();
        }
    }

    public void handleGeneratePassword() {
        byte[] randomKeyBytes = KeyManager.generateRandomKey(16);

        String randomKey = Base64.getEncoder().encodeToString(randomKeyBytes);

        generatedKeyField.setText(randomKey);
    }

    private boolean validatePasswordAndImage(String password, String operation) {
        if (password == null || password.trim().isEmpty()) {
            showAlert("Password is required for this operation.");
            return false;
        }

        byte[] aesKey = HelperFunctions.decodeBase64Password(password);
        if (aesKey == null) {
            showAlert("Invalid password! Ensure it is a valid Base64 string and decodes to a 16-byte key.");
            return false;
        }

        if (selectedImagePath == null) {
            showAlert("Please select an image to " + operation + ".");
            return false;
        }

        return true; // Validation successful
    }

    private boolean isTextFieldEmpty(String text) {
        if (text == null || text.trim().isEmpty()) {
            showAlert("Text to embed is required for this operation!");
            return true;
        }
        return false;
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private FileChooser createFileChooser(String title, String initialFileName, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        if (initialFileName != null) {
            fileChooser.setInitialFileName(initialFileName);
        }
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", extensions)
        );
        return fileChooser;
    }

    private File showSaveDialog(Stage stage, String initialFileName, String... extensions) {
        FileChooser fileChooser = createFileChooser("Save File", initialFileName, extensions);
        return fileChooser.showSaveDialog(stage);
    }

    private String appendFileExtension(String filePath, String extension) {
        return filePath.endsWith(extension) ? filePath : filePath + extension;
    }

    private void clear() {
        originalImageView.setImage(null); // Clear the image displayed in the ImageView
        selectedImageLabel.setText("Selected image:"); // Reset the label to its default text
        selectedImagePath = null; // Clear the selected image path
    }
}
