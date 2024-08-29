package simulador.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import util.api.SocketClientSide;
import util.api.Interface.IMySocket;

public class Servidor {

    private Scene scene;

    private GridPane gridPane;

    private TextArea responses;

    private TableView<IMySocket> tabela;

    public Servidor() {
        page();
    }

    public Scene getScene() {
        return this.scene;
    }

    private VBox microcontrolador() {
        // Célula (0,0): RadioButton Group e Button
        Label titulo = new Label("USAR MICROCONTROLADOR");
        titulo.setId("titulo-label"); // Adiciona o ID ao Label para aplicar o CSS
    
        ToggleGroup group = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("DESLIGAR SALA");
        radioButton1.setToggleGroup(group);
        RadioButton radioButton2 = new RadioButton("LIGAR SALA");
        radioButton2.setToggleGroup(group);
        RadioButton radioButton3 = new RadioButton("DESCREVER SALA");
        radioButton3.setToggleGroup(group);
    
        // Alinha os RadioButtons à esquerda aplicando a classe CSS
        radioButton1.getStyleClass().add("radio-button");
        radioButton2.getStyleClass().add("radio-button");
        radioButton3.getStyleClass().add("radio-button");
    
        TextField id_microcontrolador = new TextField();
        id_microcontrolador.setPromptText("ID DO MICROCONTROLADOR");
        id_microcontrolador.setId("microcontrolador-textfield"); // Adiciona o ID ao TextField para aplicar o CSS
    
        Button buttonGroup1 = new Button("ENVIAR");
    
        VBox vBox = new VBox(10, titulo, radioButton1, radioButton2, radioButton3, id_microcontrolador, buttonGroup1);
        vBox.setId("microcontrolador-vbox"); // Adiciona o ID ao VBox para aplicar o CSS
        vBox.setAlignment(Pos.TOP_CENTER); // Alinha o VBox no topo
    
        return vBox;
    }

    private VBox server() {
        // Célula (1,0): Outro RadioButton Group e Button
        Label titulo = new Label("USAR OUTRO SERVIDOR");

        ToggleGroup group = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("DESLIGAR SALA");
        radioButton1.setToggleGroup(group);
        RadioButton radioButton2 = new RadioButton("LIGAR SALA");
        radioButton2.setToggleGroup(group);
        RadioButton radioButton3 = new RadioButton("DESCREVER SALA");
        radioButton3.setToggleGroup(group);
        RadioButton radioButton4 = new RadioButton("LISTAR CONEXOES");
        radioButton4.setToggleGroup(group);

        TextField id_servidor = new TextField();
        id_servidor.setPromptText("ID DO SERVIDOR");

        TextField id_microcontrolador = new TextField();
        id_microcontrolador.setPromptText("ID DO SERVIDOR");

        Button buttonGroup1 = new Button("ENVIAR");
        VBox vBox = new VBox(10, titulo, radioButton1, radioButton2, radioButton3, radioButton4, id_servidor, id_microcontrolador, buttonGroup1);
        vBox.setAlignment(Pos.CENTER);

        return vBox;
    }

    private VBox ligarServidor() {
        // Célula (2,0): Formulário com dois campos de texto e um botão
        Label titulo = new Label("MEU SERVIDOR");

        TextField textField1Form1 = new TextField();
        textField1Form1.setPromptText("ENDEREÇO");

        TextField textField2Form1 = new TextField();
        textField2Form1.setPromptText("PORTA");

        Button buttonForm1 = new Button("LIGAR");
        VBox vBoxForm1 = new VBox(10, titulo, textField1Form1, textField2Form1, buttonForm1);
        vBoxForm1.setAlignment(Pos.CENTER);

        return vBoxForm1;
    }

    private VBox connectNewServer() {
        // Célula (0,1): Outro formulário com os mesmos campos
        Label titulo = new Label("ADICIONAR CONEXÕES");

        TextField textField1Form2 = new TextField();
        textField1Form2.setPromptText("ENDEREÇO");

        TextField textField2Form2 = new TextField();
        textField2Form2.setPromptText("PORTA");

        Button buttonForm2 = new Button("CONECTAR");
        VBox vBoxForm2 = new VBox(10, titulo, textField1Form2, textField2Form2, buttonForm2);
        vBoxForm2.setAlignment(Pos.CENTER);

        return vBoxForm2;
    }

    private void buildGrid() {
        this.gridPane = new GridPane();
        // Definindo colunas e linhas para crescer com a tela
        for (int i = 0; i < 3; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHgrow(Priority.ALWAYS); // Permitir que a coluna cresça
            colConstraints.setPercentWidth(33.33); // Cada coluna ocupa 1/3 da largura
            gridPane.getColumnConstraints().add(colConstraints);
        }

        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS); // Permitir que a linha cresça
            rowConstraints.setPercentHeight(50); // Cada linha ocupa 50% da altura
            gridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void response() {
        // Célula (2,1): TextArea
        this.responses = new TextArea();
        this.responses.setPromptText("respostas...");
        this.responses.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void connectionsTable() {
        // Célula (1,1): TableView com cabeçalhos "Endereço" e "Porta"
        this.tabela = new TableView<>();
        TableColumn<IMySocket, String> colEndereco = new TableColumn<>("Endereço");
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        TableColumn<IMySocket, String> colPorta = new TableColumn<>("Porta");
        colPorta.setCellValueFactory(new PropertyValueFactory<>("porta"));

        tabela.getColumns().add(colEndereco);
        tabela.getColumns().add(colPorta);

        ObservableList<IMySocket> data = FXCollections.observableArrayList(
                new SocketClientSide("192.168.1.1", 8080),
                new SocketClientSide("10.0.0.1", 9090));

        tabela.setItems(data);
    }

    private void page() {

        buildGrid();

        // Célula (0,0): RadioButton Group e Button
        gridPane.add(microcontrolador(), 0, 0); // Coluna 0, Linha 0

        // Célula (1,0): Outro RadioButton Group e Button
        gridPane.add(server(), 1, 0); // Coluna 1, Linha 0

        // Célula (2,0): Formulário com dois campos de texto e um botão
        gridPane.add(ligarServidor(), 2, 0); // Coluna 2, Linha 0

        // Célula (0,1): Outro formulário com os mesmos campos
        gridPane.add(connectNewServer(), 0, 1); // Coluna 0, Linha 1

        // Célula (1,1): TableView com cabeçalhos "Endereço" e "Porta"
        connectionsTable();
        gridPane.add(this.tabela, 1, 1); // Coluna 1, Linha 1
        GridPane.setFillWidth(this.tabela, true);
        GridPane.setFillHeight(this.tabela, true);
        GridPane.setHalignment(this.tabela, javafx.geometry.HPos.CENTER); // Centraliza a tabela horizontalmente
        GridPane.setValignment(this.tabela, javafx.geometry.VPos.CENTER); // Centraliza a tabela verticalmente

        // Célula (2,1): TextArea
        response();
        gridPane.add(this.responses, 2, 1); // Coluna 2, Linha 1
        GridPane.setFillWidth(this.responses, true);
        GridPane.setFillHeight(this.responses, true);
        GridPane.setHalignment(this.responses, javafx.geometry.HPos.CENTER); // Centraliza o TextArea horizontalmente
        GridPane.setValignment(this.responses, javafx.geometry.VPos.CENTER); // Centraliza o TextArea verticalmente

        this.scene = new Scene(gridPane, 800, 600);

        scene.getStylesheets().add(getClass().getResource("./servidor_style.css").toExternalForm());

    }

}
