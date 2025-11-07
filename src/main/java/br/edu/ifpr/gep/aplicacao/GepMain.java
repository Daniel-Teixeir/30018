package br.edu.ifpr.gep.aplicacao;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;

public class GepMain extends Application {

    private MongoCollection<Document> peopleCollection;
    private MongoCollection<Document> usersCollection;
    private ObservableList<Person> personList = FXCollections.observableArrayList();
    private TableView<Person> tableView = new TableView<>();
    private TextField nameField = new TextField();
    private TextField ageField = new TextField();

    private String currentUser;
    private boolean isAdmin = false;
    private String jwtToken;

    // Chave secreta para JWT (use env var em produção!)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000; // 24h

    // Credenciais padrão admin
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("crud_db");
        peopleCollection = database.getCollection("people");
        usersCollection = database.getCollection("users");

        createAdminIfNotExists();
        showLoginScene(primaryStage);
    }

    private void createAdminIfNotExists() {
        if (usersCollection.find(Filters.eq("username", ADMIN_USERNAME)).first() != null) {
            return;
        }

        String hashedPw = BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt());
        List<String> roles = Arrays.asList("user", "admin");

        Document userDoc = new Document("username", ADMIN_USERNAME)
                .append("hashedPassword", hashedPw)
                .append("roles", roles);

        usersCollection.insertOne(userDoc);
        System.out.println("Usuário admin criado: admin / admin123");
    }

    private void showLoginScene(Stage primaryStage) {
        VBox loginBox = new VBox(20);
        loginBox.setPadding(new Insets(30));
        loginBox.setStyle("-fx-alignment: center;");

        Label title = new Label("Bem-vindo ao Sistema CRUD");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(200);
        loginButton.setOnAction(e -> showLoginDialog(primaryStage));

        Button registerButton = new Button("Registrar");
        registerButton.setPrefWidth(200);
        registerButton.setOnAction(e -> showRegisterDialog(primaryStage));

        loginBox.getChildren().addAll(title, loginButton, registerButton);

        Scene scene = new Scene(loginBox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login - Sistema CRUD");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showLoginDialog(Stage primaryStage) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Login");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField username = new TextField();
        username.setPromptText("Usuário");
        PasswordField password = new PasswordField();
        password.setPromptText("Senha");

        grid.add(new Label("Usuário:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Entrar", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Map<String, String> result = new HashMap<>();
                result.put("username", username.getText().trim());
                result.put("password", password.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(creds -> {
            if (performLogin(creds.get("username"), creds.get("password"))) {
                Platform.runLater(() -> showMainScene(primaryStage));
            } else {
                showAlert("Erro", "Usuário ou senha inválidos!");
            }
        });
    }

    private boolean performLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) return false;

        Document userDoc = usersCollection.find(Filters.eq("username", username)).first();
        if (userDoc == null) return false;

        String hashedPw = userDoc.getString("hashedPassword");
        if (!BCrypt.checkpw(password, hashedPw)) return false;

        List<String> roles = userDoc.getList("roles", String.class, Collections.emptyList());

        jwtToken = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        currentUser = claims.getSubject();
        isAdmin = roles.contains("admin");

        return true;
    }

    private void showRegisterDialog(Stage primaryStage) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Novo Usuário");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField username = new TextField();
        username.setPromptText("Usuário");
        PasswordField password = new PasswordField();
        password.setPromptText("Senha");

        grid.add(new Label("Usuário:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL
        );

        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Map<String, String> result = new HashMap<>();
                result.put("username", username.getText().trim());
                result.put("password", password.getText());
                return result;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(creds -> {
            if (performRegister(creds.get("username"), creds.get("password"))) {
                showAlert("Sucesso", "Usuário criado com sucesso!");
            } else {
                showAlert("Erro", "Usuário já existe!");
            }
        });
    }

    private boolean performRegister(String username, String password) {
        if (username.isEmpty() || password.isEmpty() || password.length() < 4) return false;
        if (usersCollection.find(Filters.eq("username", username)).first() != null) return false;

        String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
        List<String> roles = Collections.singletonList("user");

        Document userDoc = new Document("username", username)
                .append("hashedPassword", hashedPw)
                .append("roles", roles);

        usersCollection.insertOne(userDoc);
        return true;
    }

    private void showMainScene(Stage primaryStage) {
        loadData();

        TableColumn<Person, String> nameCol = new TableColumn<>("Nome");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, Integer> ageCol = new TableColumn<>("Idade");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        tableView.getColumns().setAll(nameCol, ageCol);
        tableView.setItems(personList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameField.setPromptText("Nome");
        ageField.setPromptText("Idade");

        Button addBtn = new Button("Adicionar");
        addBtn.setOnAction(e -> addPerson());

        Button editBtn = new Button("Editar");
        editBtn.setOnAction(e -> editPerson());

        Button delBtn = new Button("Excluir");
        delBtn.setOnAction(e -> deletePerson());

        Button logoutBtn = new Button("Sair");
        logoutBtn.setOnAction(e -> performLogout(primaryStage));

        HBox inputBox = new HBox(10, new Label("Nome:"), nameField, new Label("Idade:"), ageField, addBtn, editBtn, delBtn, logoutBtn);
        inputBox.setPadding(new Insets(10));

        Label userLabel = new Label("Usuário: " + currentUser + (isAdmin ? " (Admin)" : ""));
        userLabel.setStyle("-fx-font-weight: bold;");

        VBox root = new VBox(10, userLabel, tableView, inputBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CRUD - " + currentUser);
    }

    private void performLogout(Stage primaryStage) {
        currentUser = null;
        isAdmin = false;
        jwtToken = null;
        personList.clear();
        showLoginScene(primaryStage);
    }

    private void loadData() {
        personList.clear();
        FindIterable<Document> docs = isAdmin
                ? peopleCollection.find()
                : peopleCollection.find(Filters.eq("createdBy", currentUser));

        for (Document doc : docs) {
            personList.add(new Person(
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("name"),
                    doc.getInteger("age", 0),
                    doc.getString("createdBy")
            ));
        }
    }

    private void addPerson() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Erro", "Nome é obrigatório!");
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age < 0 || age > 150) throw new Exception();
        } catch (Exception e) {
            showAlert("Erro", "Idade inválida!");
            return;
        }

        Document doc = new Document("name", name)
                .append("age", age)
                .append("createdBy", currentUser);

        peopleCollection.insertOne(doc);
        loadData();
        clearFields();
    }

    private void editPerson() {
        Person p = tableView.getSelectionModel().getSelectedItem();
        if (p == null) {
            showAlert("Erro", "Selecione uma pessoa!");
            return;
        }
        if (!isAdmin && !p.getCreatedBy().equals(currentUser)) {
            showAlert("Erro", "Você só pode editar seus próprios registros!");
            return;
        }

        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Editar Pessoa");

        TextField nameTF = new TextField(p.getName());
        TextField ageTF = new TextField(String.valueOf(p.getAge()));

        VBox box = new VBox(10,
                new Label("Nome:"), nameTF,
                new Label("Idade:"), ageTF
        );
        box.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return new Person(p.getId(), nameTF.getText(), Integer.parseInt(ageTF.getText()), p.getCreatedBy());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            peopleCollection.updateOne(
                    Filters.eq("_id", new ObjectId(p.getId())),
                    Updates.combine(
                            Updates.set("name", updated.getName()),
                            Updates.set("age", updated.getAge())
                    )
            );
            loadData();
        });
    }

    private void deletePerson() {
        Person p = tableView.getSelectionModel().getSelectedItem();
        if (p == null) {
            showAlert("Erro", "Selecione uma pessoa!");
            return;
        }
        if (!isAdmin && !p.getCreatedBy().equals(currentUser)) {
            showAlert("Erro", "Você só pode excluir seus próprios registros!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Excluir " + p.getName() + "?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                peopleCollection.deleteOne(Filters.eq("_id", new ObjectId(p.getId())));
                loadData();
            }
        });
    }

    private void clearFields() {
        nameField.clear();
        ageField.clear();
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static class Person {
        private final String id;
        private final String name;
        private final int age;
        private final String createdBy;

        public Person(String id, String name, int age, String createdBy) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.createdBy = createdBy;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getCreatedBy() { return createdBy; }
    }
}