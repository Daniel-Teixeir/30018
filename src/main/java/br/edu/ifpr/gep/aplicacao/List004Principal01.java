package br.edu.ifpr.gep.aplicacao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Objects;

/**
 * Classe principal do sistema GREB.
 * Responsável por inicializar a aplicação JavaFX e carregar a tela principal.
 */
public class List004Principal01 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o painel principal
            URL resource = getClass().getResource("/br/edu/ifpr/gep/view/MainView.fxml");
            if (resource == null) {
                throw new IOException("Recurso FXML não encontrado: /br/edu/ifpr/gep/view/MainView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(resource);
            TabPane root = loader.load();

            // Cena com dimensões ajustadas conforme FXML (usando doubles explícitos)
            Scene scene = new Scene(root, 1000.0, 700.0);

            // Aplica o CSS, se existir
            URL cssResource = getClass().getResource("/br/edu/ifpr/gep/view/DesignView.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            } else {
                System.err.println("Aviso: CSS não encontrado: /br/edu/ifpr/gep/view/DesignView.css");
            }

            // Configura janela
            primaryStage.setTitle("Sistema GREB");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // Não passa primaryStage como owner (é null scene), usa null para evitar NPE no dialog
            showAlert(Alert.AlertType.ERROR, "Falha no Carregamento",
                    "Não foi possível carregar o arquivo FXML.", e, null);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método utilitário para exibir alertas de forma flexível e informativa.
     * Suporta mensagens simples ou exceções completas com stack trace para depuração.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        showAlert(alertType, title, message, null, null); // Sobrecarga interna para exceções opcionais
    }

    /**
     * Sobrecarga para exibir alertas com exceção (stack trace incluído).
     * @param alertType Tipo do alerta (ERROR, INFO, WARNING, etc.).
     * @param title Título do alerta.
     * @param message Mensagem principal (opcional; será sobrescrita se exception != null).
     * @param exception Exceção a ser exibida (se null, usa só a mensagem).
     * @param ownerStage Stage owner para centralização (opcional; se null, pula initOwner para evitar NPE).
     */
    private void showAlert(Alert.AlertType alertType, String title, String message, Exception exception, Stage ownerStage) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title != null ? title : "Atenção");
        alert.initModality(Modality.APPLICATION_MODAL); // Modal para bloquear interação
        if (ownerStage != null) {
            alert.initOwner(ownerStage); // Owner para centralização relativa (só se stage válido)
        }
        alert.setHeaderText(null); // Sem header para simplicidade

        String content;
        if (exception != null) {
            // Formata stack trace de forma legível
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String stackTrace = sw.toString();

            // Mensagem mais explícita com causa e stack trace
            content = String.format(
                    "Erro inesperado:\n\n%s\n\nDetalhes técnicos (stack trace):\n%s\n\nDica: Copie este texto para relatar o problema.",
                    message != null ? message : exception.getMessage(),
                    stackTrace
            );

            // Adiciona área de texto expansível para stack trace longo
            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);

            // Ajusta tamanho da janela para caber o conteúdo
            alert.getDialogPane().setPrefSize(600, 400);
        } else {
            content = message != null ? message : "Ocorreu um problema não especificado.";
            alert.setContentText(content);
        }

        // Botão opcional para copiar (útil para erros com stack trace)
        if (exception != null) {
            ButtonType copyButton = new ButtonType("Copiar Erro");
            alert.getButtonTypes().add(copyButton);
            Button copyBtn = (Button) alert.getDialogPane().lookupButton(copyButton);
            if (copyBtn != null) {
                copyBtn.setOnAction(e -> {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent contentToCopy = new ClipboardContent();
                    contentToCopy.putString(content);
                    clipboard.setContent(contentToCopy);
                    // Alerta simples para confirmação (evita recursão profunda; usa ownerStage se disponível)
                    Alert copyAlert = new Alert(Alert.AlertType.INFORMATION);
                    copyAlert.setTitle("Copiado");
                    copyAlert.setHeaderText(null);
                    copyAlert.setContentText("Stack trace copiado para a área de transferência!");
                    if (ownerStage != null) {
                        copyAlert.initOwner(ownerStage);
                    }
                    copyAlert.initModality(Modality.APPLICATION_MODAL);
                    copyAlert.showAndWait();
                });
            }
        }

        alert.showAndWait();
    }

    /**
     * Sobrecarga simplificada para erros rápidos (mantém compatibilidade com o código antigo).
     * @param message Mensagem de erro.
     */
    private void showAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Erro Crítico", message);
    }
}