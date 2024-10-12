package simulador_v2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import util.ServerReq;

public class Main extends Application {

    private Circle statusCircle;
    private Label addressPortLabel;
    private Servidor servidor;
    private static Thread server_thread;
    private TextArea responses;
    private String endereco;
    private int porta;

    private ObservableList<Device> conexoesList = FXCollections.observableArrayList();
    private ObservableList<Appliance> dispositivosList = FXCollections.observableArrayList();

    private TableView<Device> tabela_conexoes;
    private TableView<Appliance> tabela_dispositivos;

    @Override
    public void start(Stage primaryStage) {
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

        Button ligar = new Button("Iniciar Servidor");
        Button microcontrolador = new Button("Microcontrolador");
        Button sair = new Button("Sair");

        ligar.setOnAction(e -> ligarDialog());
        microcontrolador.setOnAction(e -> microcontroladorDialog());
        sair.setOnAction(e -> Platform.exit());

        sideBar.getChildren().addAll(ligar, microcontrolador, sair);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        grid.setAlignment(Pos.CENTER);

        grid.setEffect(new DropShadow());

        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(50);
            grid.getRowConstraints().add(rowConstraints);

            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(50);
            grid.getColumnConstraints().add(colConstraints);
        }

        tabela_conexoes = tabelaConexoes();
        grid.add(tabela_conexoes, 0, 0);

        tabela_dispositivos = tabelaDispositivos();
        grid.add(tabela_dispositivos, 1, 0);

        responses = new TextArea();
        grid.add(responses, 0, 1);

        // Célula 4: Pode ser deixada vazia ou adicionar algo
        Label cell4 = new Label("Célula 4");
        cell4.setStyle("-fx-border-color: black; -fx-alignment: center;");
        grid.add(cell4, 1, 1);

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                GridPane.setHgrow(grid.getChildren().get(row * 2 + col), Priority.ALWAYS);
                GridPane.setVgrow(grid.getChildren().get(row * 2 + col), Priority.ALWAYS);
            }
        }

        root.setCenter(grid);
        BorderPane.setAlignment(grid, Pos.CENTER);
        BorderPane.setMargin(grid, new Insets(10));

        root.setTop(appBar);
        root.setLeft(sideBar);

        // Configuração da cena
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Simulador");
        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);

        primaryStage.show();
    }

    private void ligarDialog() {
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
            endereco = addressField.getText();
            String port = portField.getText();

            if (!endereco.isEmpty() && !port.isEmpty()) {
                porta = Integer.parseInt(port);
                this.servidor = new Servidor(endereco, porta, false, responses);

                servidor.configurarUpdateConnections(() -> {
                    Platform.runLater(() -> {
                        conexoesList.clear();
                        servidor.listarConexoes().forEach((id, conexao) -> {
                            conexoesList.add(new Device(id, conexao.getEndereco(), String.valueOf(conexao.getPorta())));
                        });
                    });
                });
                servidor.configurarAtualizarTabelaDispositivos(appliance -> {
                    boolean salaExistente = false;
                    for (Appliance a : dispositivosList) {
                        if (a.getId().equals(appliance.getId())) {
                            a.setOn(appliance.getOn());
                            a.setOff(appliance.getOff());
                            salaExistente = true;
                            break;
                        }
                    }
                    if (!salaExistente) {
                        dispositivosList.add(appliance);
                    }
                });
                

                server_thread = new Thread(() -> {
                    this.servidor.start();
                });
                server_thread.setDaemon(true);
                server_thread.start();
                statusCircle.setFill(Color.GREEN);
                addressPortLabel.setText("Servidor ligado (" + endereco + ":" + port + ")");
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

        Scene scene = new Scene(vbox, 400, 300); // Ajuste do tamanho do diálogo
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void microcontroladorDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Microcontrolador");

        Label group1Label = new Label("Opções:");
        ToggleGroup opcao_microcontrolador = new ToggleGroup();
        RadioButton ligar = new RadioButton("Ligar");
        RadioButton desligar = new RadioButton("Desligar");
        RadioButton descrever = new RadioButton("Descrever");
        ligar.setToggleGroup(opcao_microcontrolador);
        desligar.setToggleGroup(opcao_microcontrolador);
        descrever.setToggleGroup(opcao_microcontrolador);

        VBox group1Box = new VBox(5, ligar, desligar, descrever);
        group1Box.setAlignment(Pos.CENTER);

        VBox card1 = new VBox(10, group1Label, group1Box);
        card1.setAlignment(Pos.CENTER);
        card1.setPadding(new Insets(10));
        card1.setStyle(
                "-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        Label group2Label = new Label("Opções:");
        ToggleGroup id_microcontrolador = new ToggleGroup();
        RadioButton enviar_para_um = new RadioButton("Uma Sala");
        RadioButton enviar_para_todos = new RadioButton("Todas as salas");
        enviar_para_um.setToggleGroup(id_microcontrolador);
        enviar_para_todos.setToggleGroup(id_microcontrolador);

        VBox group2Box = new VBox(5, enviar_para_um, enviar_para_todos);
        group2Box.setAlignment(Pos.CENTER);

        Label inputLabel = new Label("Digite o ID da sala:");
        TextField inputField = new TextField();
        inputLabel.setVisible(false);
        inputField.setVisible(false);

        id_microcontrolador.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == enviar_para_um) {
                inputLabel.setVisible(true);
                inputField.setVisible(true);
            } else {
                inputLabel.setVisible(false);
                inputField.setVisible(false);
            }
        });

        VBox card2 = new VBox(10, group2Label, group2Box, inputLabel, inputField);
        card2.setAlignment(Pos.CENTER);
        card2.setPadding(new Insets(10));
        card2.setStyle(
                "-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");

        Button submit = new Button("Enviar");
        Button cancelar = new Button("Cancelar");

        submit.setOnAction(e -> {
            RadioButton opcaoSelecionada = (RadioButton) opcao_microcontrolador.getSelectedToggle();
            RadioButton destinatarioSelecionado = (RadioButton) id_microcontrolador.getSelectedToggle();

            if (opcaoSelecionada != null && destinatarioSelecionado != null) {
                int op;
                switch (opcaoSelecionada.getText()) {
                    case "Ligar":
                        op = 0;
                        break;
                    case "Desligar":
                        op = 1;
                        break;
                    case "Descrever":
                        op = 2;
                        break;
                    default:
                        op = 2;
                        break;
                }

                if (destinatarioSelecionado == enviar_para_um) {
                    String idSalaStr = inputField.getText();
                    if (!idSalaStr.isEmpty()) {
                        int idSala = Integer.parseInt(idSalaStr);
                        System.out.println(op + " " + idSala);
                        servidor.addCommand(new ServerReq(endereco, porta, "SERVER", "unicast", op, idSala));
                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Erro");
                        alert.setHeaderText(null);
                        alert.setContentText("Por favor, digite o ID da sala.");
                        alert.showAndWait();
                        return;
                    }
                } else {
                    servidor.addCommand(new ServerReq(endereco, porta, "SERVER", "broadcast", op, -1));
                }
                dialog.close();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, selecione todas as opções.");
                alert.showAndWait();
            }
        });

        cancelar.setOnAction(e -> {
            dialog.close();
        });

        HBox buttonBox = new HBox(10, submit, cancelar);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(20, card1, card2, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 400, 500); // Ajuste do tamanho do diálogo
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    @SuppressWarnings("unchecked")
    private TableView<Device> tabelaConexoes() {
        TableView<Device> table = new TableView<>();

        TableColumn<Device, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Device, String> addressColumn = new TableColumn<>("Endereço");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Device, String> portColumn = new TableColumn<>("Porta");
        portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

        table.getColumns().addAll(idColumn, addressColumn, portColumn);

        table.setItems(conexoesList);

        return table;
    }

    @SuppressWarnings("unchecked")
    private TableView<Appliance> tabelaDispositivos() {
        TableView<Appliance> table = new TableView<>();

        TableColumn<Appliance, Number> idColumn = new TableColumn<>("ID Sala");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Appliance, Number> onColumn = new TableColumn<>("Ligados");
        onColumn.setCellValueFactory(cellData -> cellData.getValue().onProperty());

        TableColumn<Appliance, Number> offColumn = new TableColumn<>("Desligados");
        offColumn.setCellValueFactory(cellData -> cellData.getValue().offProperty());

        table.getColumns().addAll(idColumn, onColumn, offColumn);

        table.setItems(dispositivosList);

        return table;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Device {
        private Integer id;
        private String address;
        private String port;

        public Device(Integer id, String address, String port) {
            this.id = id;
            this.address = address;
            this.port = port;
        }

        public Integer getId() {
            return id;
        }

        public String getAddress() {
            return address;
        }

        public String getPort() {
            return port;
        }
    }

    public static class Appliance {
        private IntegerProperty id;
        private IntegerProperty on;
        private IntegerProperty off;

        public Appliance(Integer id, Integer on, Integer off) {
            this.id = new SimpleIntegerProperty(id);
            this.on = new SimpleIntegerProperty(on);
            this.off = new SimpleIntegerProperty(off);
        }

        public Integer getId() {
            return id.get();
        }

        public void setId(Integer id) {
            this.id.set(id);
        }

        public IntegerProperty idProperty() {
            return id;
        }

        public Integer getOn() {
            return on.get();
        }

        public void setOn(Integer on) {
            this.on.set(on);
        }

        public IntegerProperty onProperty() {
            return on;
        }

        public Integer getOff() {
            return off.get();
        }

        public void setOff(Integer off) {
            this.off.set(off);
        }

        public IntegerProperty offProperty() {
            return off;
        }
    }

}
