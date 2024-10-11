package simulador_v2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import simulador_v2.server.Servidor;

public class Main extends Application {

    private Circle statusCircle;

    private Label addressPortLabel;

    private Servidor servidor;

    private static Thread server_thread;

    private TextArea responses;

    @Override
    public void start(Stage primaryStage) {
        // Criação do layout raiz
        BorderPane root = new BorderPane();

        // AppBar no topo
        BorderPane appBar = new BorderPane();
        appBar.setPadding(new Insets(10));
        appBar.setStyle("-fx-background-color: #f0f0f0;");

        Label titleLabel = new Label("Simulador");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        statusCircle = new Circle(10, Color.RED);

        addressPortLabel = new Label("");
        addressPortLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox centerAppBar = new HBox();
        centerAppBar.setAlignment(Pos.CENTER);
        centerAppBar.getChildren().add(addressPortLabel);

        appBar.setLeft(titleLabel);
        appBar.setCenter(centerAppBar);
        appBar.setRight(statusCircle);
        BorderPane.setAlignment(statusCircle, Pos.CENTER_RIGHT);

        // Sidebar à esquerda
        VBox sideBar = new VBox(10);
        sideBar.setPadding(new Insets(10));
        sideBar.setStyle("-fx-background-color: #e0e0e0;");
        sideBar.setPrefWidth(250); // Aumentar a largura da Sidebar

        Button option1 = new Button("Iniciar Servidor");
        Button option2 = new Button("Microcontrolador");
        Button option3 = new Button("Sair");

        // Eventos para os botões
        option1.setOnAction(e -> openOption1Dialog());
        option2.setOnAction(e -> openOption2Dialog());
        option3.setOnAction(e -> Platform.exit());

        sideBar.getChildren().addAll(option1, option2, option3);

        // Grid 2x2 no centro
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Ajustar o grid para ocupar todo o espaço disponível
        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.setAlignment(Pos.CENTER);

        // Aplicar sombreamento ao grid
        grid.setEffect(new DropShadow());

        // Configurar restrições de linhas e colunas
        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(50);
            grid.getRowConstraints().add(rowConstraints);

            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(50);
            grid.getColumnConstraints().add(colConstraints);
        }

        // Célula 1: Tabela com id, endereço e porta
        TableView<Device> table1 = createTable1();
        grid.add(table1, 0, 0);

        // Célula 2: Tabela com id, aparelhos, ligados e desligados
        TableView<Appliance> table2 = createTable2();
        grid.add(table2, 1, 0);

        // Célula 3: TextArea
        responses = new TextArea();
        grid.add(responses, 0, 1);

        // Célula 4: Pode ser deixada vazia ou adicionar algo
        Label cell4 = new Label("Célula 4");
        cell4.setStyle("-fx-border-color: black; -fx-alignment: center;");
        grid.add(cell4, 1, 1);

        // Ajustar as células para expandir
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                GridPane.setHgrow(grid.getChildren().get(row * 2 + col), Priority.ALWAYS);
                GridPane.setVgrow(grid.getChildren().get(row * 2 + col), Priority.ALWAYS);
            }
        }

        // Permitir que o grid expanda
        root.setCenter(grid);
        BorderPane.setAlignment(grid, Pos.CENTER);
        BorderPane.setMargin(grid, new Insets(10));

        // Adicionando os componentes ao layout raiz
        root.setTop(appBar);
        root.setLeft(sideBar);

        // Configuração da cena
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Simulador");
        primaryStage.setScene(scene);

        // Iniciar em modo tela cheia
        primaryStage.setFullScreen(true);

        primaryStage.show();
    }

    // Método para abrir o diálogo da Opção 1
    private void openOption1Dialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ligar Servidor");

        Label addressLabel = new Label("Endereço:");
        TextField addressField = new TextField();

        Label portLabel = new Label("Porta:");
        TextField portField = new TextField();

        Button ligar = new Button("Ligar");

        Button cancelar = new Button("Cancelar");

        ligar.setOnAction(e -> {
            String address = addressField.getText();
            String port = portField.getText();

            if (!address.isEmpty() && !port.isEmpty()) {
                this.servidor = new Servidor(address, Integer.parseInt(port), false, responses);
                server_thread = new Thread(() -> {
                    this.servidor.start();
                });
                server_thread.setDaemon(true);
                server_thread.start();
                statusCircle.setFill(Color.GREEN);
                addressPortLabel.setText("Servidor ligado (" + address + ":" + port + ")");
                dialog.close();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, preencha todos os campos.");
                alert.showAndWait();
            }
        });

        cancelar.setOnAction(e -> {
            dialog.close();
        });

        VBox vbox = new VBox(10, addressLabel, addressField, portLabel, portField, ligar, cancelar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 600, 600); // Aumentar o tamanho do diálogo
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Método para abrir o diálogo da Opção 2
    private void openOption2Dialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Microcontrolador");

        // Primeiro Radio Group dentro de um Card
        Label group1Label = new Label("Opções:");
        ToggleGroup group1 = new ToggleGroup();
        RadioButton optionG1_1 = new RadioButton("Ligar");
        RadioButton optionG1_2 = new RadioButton("Desligar");
        RadioButton optionG1_3 = new RadioButton("Descrever");
        optionG1_1.setToggleGroup(group1);
        optionG1_2.setToggleGroup(group1);
        optionG1_3.setToggleGroup(group1);

        VBox group1Box = new VBox(5, optionG1_1, optionG1_2, optionG1_3);
        group1Box.setAlignment(Pos.CENTER);

        // Estilo do Card para o Grupo 1
        VBox card1 = new VBox(10, group1Label, group1Box);
        card1.setAlignment(Pos.CENTER);
        card1.setPadding(new Insets(10));
        card1.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        // Segundo Radio Group dentro de um Card
        Label group2Label = new Label("Opções:");
        ToggleGroup group2 = new ToggleGroup();
        RadioButton optionG2_1 = new RadioButton("Uma Sala");
        RadioButton optionG2_2 = new RadioButton("Todas as salas");
        optionG2_1.setToggleGroup(group2);
        optionG2_2.setToggleGroup(group2);

        VBox group2Box = new VBox(5, optionG2_1, optionG2_2);
        group2Box.setAlignment(Pos.CENTER);

        // Input que aparece ao selecionar a primeira opção do segundo grupo
        Label inputLabel = new Label("Digite o ID da sala:");
        TextField inputField = new TextField();
        inputLabel.setVisible(false);
        inputField.setVisible(false);

        group2.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == optionG2_1) {
                inputLabel.setVisible(true);
                inputField.setVisible(true);
            } else {
                inputLabel.setVisible(false);
                inputField.setVisible(false);
            }
        });

        // Estilo do Card para o Grupo 2
        VBox card2 = new VBox(10, group2Label, group2Box, inputLabel, inputField);
        card2.setAlignment(Pos.CENTER);
        card2.setPadding(new Insets(10));
        card2.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        // Botões de Submeter e Cancelar
        Button submitButton = new Button("Enviar");
        Button cancelButton = new Button("Cancelar");

        submitButton.setOnAction(e -> {
            // Aqui você pode adicionar a lógica de submissão
            dialog.close();
        });

        cancelButton.setOnAction(e -> {
            dialog.close();
        });

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, card1, card2, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 600, 600); // Aumentar o tamanho do diálogo
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // Método para criar a tabela da célula 1
    @SuppressWarnings("unchecked")
    private TableView<Device> createTable1() {
        TableView<Device> table = new TableView<>();

        TableColumn<Device, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Device, String> addressColumn = new TableColumn<>("Endereço");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Device, String> portColumn = new TableColumn<>("Porta");
        portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

        table.getColumns().addAll(idColumn, addressColumn, portColumn);

        // Adicionar dados de exemplo
        table.getItems().addAll(
                new Device(1, "192.168.0.1", "8080"),
                new Device(2, "192.168.0.2", "8081"),
                new Device(3, "192.168.0.3", "8082")
        );

        return table;
    }

    // Método para criar a tabela da célula 2
    @SuppressWarnings("unchecked")
    private TableView<Appliance> createTable2() {
        TableView<Appliance> table = new TableView<>();

        TableColumn<Appliance, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appliance, String> deviceColumn = new TableColumn<>("Aparelhos");
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("device"));

        TableColumn<Appliance, String> onColumn = new TableColumn<>("Ligados");
        onColumn.setCellValueFactory(new PropertyValueFactory<>("on"));

        TableColumn<Appliance, String> offColumn = new TableColumn<>("Desligados");
        offColumn.setCellValueFactory(new PropertyValueFactory<>("off"));

        table.getColumns().addAll(idColumn, deviceColumn, onColumn, offColumn);

        // Adicionar dados de exemplo
        table.getItems().addAll(
                new Appliance(1, "TV", "Sim", "Não"),
                new Appliance(2, "Computador", "Não", "Sim"),
                new Appliance(3, "Geladeira", "Sim", "Não")
        );

        return table;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Classes auxiliares para as tabelas
    public static class Device {
        private Integer id;
        private String address;
        private String port;

        public Device(Integer id, String address, String port) {
            this.id = id;
            this.address = address;
            this.port = port;
        }

        public Integer getId() { return id; }
        public String getAddress() { return address; }
        public String getPort() { return port; }
    }

    public static class Appliance {
        private Integer id;
        private String device;
        private String on;
        private String off;

        public Appliance(Integer id, String device, String on, String off) {
            this.id = id;
            this.device = device;
            this.on = on;
            this.off = off;
        }

        public Integer getId() { return id; }
        public String getDevice() { return device; }
        public String getOn() { return on; }
        public String getOff() { return off; }
    }
}
