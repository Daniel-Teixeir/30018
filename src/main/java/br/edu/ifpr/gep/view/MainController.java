package br.edu.ifpr.gep.view; // Ajuste se o pacote for diferente

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML
    private TableView<Portaria> portariaTable;

    @FXML
    private TableColumn<Portaria, Integer> portariaIdColumn;

    @FXML
    private TableColumn<Portaria, String> portariaDataColumn;

    @FXML
    private TableColumn<Portaria, String> portariaNomeColumn;

    @FXML
    private TableColumn<Portaria, String> portariaPorteiroColumn;

    @FXML
    private TableColumn<Portaria, String> portariaEmissorColumn;

    @FXML
    private TableView<Emissor> emissorTable;

    @FXML
    private TableColumn<Emissor, Integer> emissorIdColumn;

    @FXML
    private TableColumn<Emissor, String> emissorDataColumn;

    @FXML
    private TableColumn<Emissor, String> emissorNomeColumn;

    @FXML
    private TableColumn<Emissor, String> emissorPorteiroColumn;

    @FXML
    private TableColumn<Emissor, String> emissorEmissorColumn;

    @FXML
    private ImageView profileImage;

    @FXML
    private TextField nameField;

    @FXML
    private Label idLabel;

    @FXML
    private PasswordField passwordField; // Corrigido para PasswordField

    @FXML
    private TextField secondaryColorField;

    private ObservableList<Portaria> portariaData = FXCollections.observableArrayList();

    private ObservableList<Emissor> emissorData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar colunas da tabela Portaria
        portariaIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        portariaDataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        portariaNomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        portariaPorteiroColumn.setCellValueFactory(new PropertyValueFactory<>("porteiro"));
        portariaEmissorColumn.setCellValueFactory(new PropertyValueFactory<>("emissor"));
        portariaTable.setItems(portariaData);

        // Configurar colunas da tabela Emissor (similar)
        emissorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emissorDataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        emissorNomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        emissorPorteiroColumn.setCellValueFactory(new PropertyValueFactory<>("porteiro"));
        emissorEmissorColumn.setCellValueFactory(new PropertyValueFactory<>("emissor"));
        emissorTable.setItems(emissorData);

        // Carregar dados iniciais (placeholder)
        loadPortariaData();
        loadEmissorData();

        // Carregar dados do usuário (placeholder)
        idLabel.setText("12345"); // Exemplo
        nameField.setText("Usuário Exemplo");
    }

    private void loadPortariaData() {
        // Placeholder: Carregue do banco de dados
        portariaData.add(new Portaria(1, "2023-10-01", "Nome1", "Porteiro1", "Emissor1"));
        // Adicione mais...
    }

    private void loadEmissorData() {
        // Placeholder: Carregue do banco de dados
        emissorData.add(new Emissor(1, "2023-10-01", "Nome1", "Porteiro1", "Emissor1"));
        // Adicione mais...
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        // Lógica para adicionar item (depende da aba ativa, ex.: usar TabPane para detectar)
        showAlert("Adicionar", "Funcionalidade de adicionar implementada aqui.");
    }

    @FXML
    private void handleRemove(ActionEvent event) {
        // Lógica para remover item selecionado
        showAlert("Remover", "Funcionalidade de remover implementada aqui.");
    }

    @FXML
    private void handleSimulate(ActionEvent event) {
        // Lógica para simular
        showAlert("Simular", "Funcionalidade de simular implementada aqui.");
    }

    @FXML
    private void handleDeleteAll(ActionEvent event) {
        // Lógica para deletar tudo
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setContentText("Tem certeza que deseja deletar tudo?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            portariaData.clear(); // Exemplo para Portaria
            // Limpe outras tabelas...
        }
    }

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            profileImage.setImage(image);
            // Salve o caminho no banco de dados ou preferências
        }
    }

    @FXML
    private void handleDeleteAccount(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmação");
        confirm.setContentText("Tem certeza que deseja deletar sua conta?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Lógica para deletar conta
            switchToLoginView();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        switchToLoginView();
    }

    private void switchToLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) portariaTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Falha ao carregar a tela de login.");
        }
    }

    @FXML
    private void handleLightMode(ActionEvent event) {
        BorderPane root = (BorderPane) portariaTable.getScene().getRoot(); // Corrigido para BorderPane
        root.getStyleClass().remove("dark-mode");
        // Persista a preferência
    }

    @FXML
    private void handleDarkMode(ActionEvent event) {
        BorderPane root = (BorderPane) portariaTable.getScene().getRoot(); // Corrigido para BorderPane
        root.getStyleClass().add("dark-mode");
        // Persista a preferência
    }

    @FXML
    private void handleApplySecondaryColor(ActionEvent event) {
        String hex = secondaryColorField.getText();
        if (hex.matches("^#[0-9A-Fa-f]{6}$")) {
            // Aplique a cor, ex.: atualize CSS dinamicamente ou setStyle
            portariaTable.getScene().getRoot().setStyle("-fx-accent: " + hex + ";");
            // Persista
        } else {
            showAlert("Erro", "Formato HEX inválido. Use #RRGGBB.");
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        // Resetar configurações para padrão
        handleLightMode(null);
        secondaryColorField.setText("");
        portariaTable.getScene().getRoot().setStyle(""); // Remova estilos custom
        showAlert("Reset", "Configurações resetadas para fábrica.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classes modelo (crie arquivos separados se preferir)
    public static class Portaria {
        private int id;
        private String data;
        private String nome;
        private String porteiro;
        private String emissor;

        public Portaria(int id, String data, String nome, String porteiro, String emissor) {
            this.id = id;
            this.data = data;
            this.nome = nome;
            this.porteiro = porteiro;
            this.emissor = emissor;
        }

        // Getters
        public int getId() { return id; }
        public String getData() { return data; }
        public String getNome() { return nome; }
        public String getPorteiro() { return porteiro; }
        public String getEmissor() { return emissor; }
    }

    public static class Emissor {
        private int id;
        private String data;
        private String nome;
        private String porteiro;
        private String emissor;

        public Emissor(int id, String data, String nome, String porteiro, String emissor) {
            this.id = id;
            this.data = data;
            this.nome = nome;
            this.porteiro = porteiro;
            this.emissor = emissor;
        }

        // Getters
        public int getId() { return id; }
        public String getData() { return data; }
        public String getNome() { return nome; }
        public String getPorteiro() { return porteiro; }
        public String getEmissor() { return emissor; }
    }
}