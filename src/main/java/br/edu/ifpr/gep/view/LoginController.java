package br.edu.ifpr.gep.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button actionButton;

    @FXML
    private Button switchButton;

    private boolean isLoginMode = true; // Modo padrão: login

    @FXML
    private void handleAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (isLoginMode) {
            // Lógica de login (ex.: verificar credenciais no banco de dados)
            if (authenticate(username, password)) {
                switchToMainView();
            } else {
                showAlert("Erro de Login", "Usuário ou senha inválidos.");
            }
        } else {
            // Lógica de registro (ex.: criar novo usuário no banco de dados)
            if (register(username, password)) {
                showAlert("Sucesso", "Registro concluído. Agora faça login.");
                switchToLoginMode();
            } else {
                showAlert("Erro de Registro", "Falha ao registrar. Tente outro nome de usuário.");
            }
        }
    }

    @FXML
    private void handleSwitch(ActionEvent event) {
        if (isLoginMode) {
            switchToRegisterMode();
        } else {
            switchToLoginMode();
        }
    }

    private void switchToLoginMode() {
        isLoginMode = true;
        titleLabel.setText("Login");
        actionButton.setText("Entrar");
        switchButton.setText("Registrar");
    }

    private void switchToRegisterMode() {
        isLoginMode = false;
        titleLabel.setText("Registro");
        actionButton.setText("Registrar");
        switchButton.setText("Login");
    }

    private boolean authenticate(String username, String password) {
        // Placeholder: Implemente a autenticação real (ex.: consulta ao banco)
        return "admin".equals(username) && "password".equals(password);
    }

    private boolean register(String username, String password) {
        // Placeholder: Implemente o registro real (ex.: inserção no banco)
        return true; // Simule sucesso
    }

    private void switchToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Painel Principal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao carregar a tela principal.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}