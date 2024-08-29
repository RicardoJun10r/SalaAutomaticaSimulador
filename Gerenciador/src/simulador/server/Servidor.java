package simulador.server;

import java.util.Map;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class Servidor {

    private Scene scene;

    private GridPane gridPane;

    private TextArea responses;

    private TableView<Conexao> tabela;

    private Boolean ligado;

    private SocketServerSide servidor;

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    public Servidor() {
        this.ligado = false;
        page();
    }

    public Scene getScene() {
        return this.scene;
    }

    private VBox microcontrolador() {
        // Célula (0,0): RadioButton Group e Button
        Label titulo = new Label("USAR MICROCONTROLADOR");

        ToggleGroup group = new ToggleGroup();
        RadioButton radioButton1 = new RadioButton("DESLIGAR SALA");
        radioButton1.setToggleGroup(group);
        RadioButton radioButton2 = new RadioButton("LIGAR SALA");
        radioButton2.setToggleGroup(group);
        RadioButton radioButton3 = new RadioButton("DESCREVER SALA");
        radioButton3.setToggleGroup(group);

        TextField id_microcontrolador = new TextField();
        id_microcontrolador.setPromptText("ID DO MICROCONTROLADOR");
        id_microcontrolador.setPrefWidth(200.0);

        Button buttonGroup1 = new Button("ENVIAR");

        VBox vBox = new VBox(10, titulo, radioButton1, radioButton2, radioButton3, id_microcontrolador, buttonGroup1);
        vBox.setAlignment(Pos.CENTER); // Alinha o VBox no topo

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
        id_microcontrolador.setPromptText("ID DO MICROCONTROLADOR");

        Button buttonGroup1 = new Button("ENVIAR");

        VBox vBox = new VBox(10, titulo, radioButton1, radioButton2, radioButton3, radioButton4, id_servidor,
                id_microcontrolador, buttonGroup1);
        vBox.setAlignment(Pos.CENTER);

        return vBox;
    }

    private VBox ligarServidor() {
        // Célula (2,0): Formulário com dois campos de texto e um botão
        Label titulo = new Label("MEU SERVIDOR");

        // Criação do círculo
        Circle circulo = new Circle(5); // Raio do círculo, ajustável conforme necessário
        atualizarCorCirculo(circulo);

        // Coloca a Label e o Círculo em um HBox
        HBox hBoxTitulo = new HBox(10, titulo, circulo); // 10 é o espaçamento entre a Label e o círculo
        hBoxTitulo.setAlignment(Pos.CENTER);

        TextField textField1Form1 = new TextField();
        textField1Form1.setPromptText("ENDEREÇO");

        TextField textField2Form1 = new TextField();
        textField2Form1.setPromptText("PORTA");

        Button buttonForm1 = new Button("LIGAR");

        buttonForm1.setOnAction((event) -> {
            String endereco = textField1Form1.getText();

            int porta = Integer.parseInt(textField2Form1.getText());

            this.servidor = new SocketServerSide(endereco, porta, SocketType.TEXTO);

            this.servidor.iniciar();

            this.ligado = true;

            atualizarCorCirculo(circulo);

            // Célula (1,1): TableView com cabeçalhos "Endereço" e "Porta"
            connectionsTable();
            this.gridPane.add(this.tabela, 1, 1); // Coluna 1, Linha 1
            GridPane.setFillWidth(this.tabela, true);
            GridPane.setFillHeight(this.tabela, true);
            GridPane.setHalignment(this.tabela, javafx.geometry.HPos.CENTER); // Centraliza a tabela horizontalmente
            GridPane.setValignment(this.tabela, javafx.geometry.VPos.CENTER); // Centraliza a tabela verticalmente

        });

        VBox vBoxForm1 = new VBox(10, hBoxTitulo, textField1Form1, textField2Form1, buttonForm1);
        vBoxForm1.setAlignment(Pos.CENTER);

        return vBoxForm1;
    }

    private void atualizarCorCirculo(Circle circulo) {
        if (this.ligado) {
            circulo.setFill(Color.GREEN);
        } else {
            circulo.setFill(Color.RED);
        }
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
            colConstraints.setFillWidth(true); // Permite que o conteúdo se expanda para a largura total
            colConstraints.setPercentWidth(33.33); // Cada coluna ocupa 1/3 da largura
            gridPane.getColumnConstraints().add(colConstraints);
        }

        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS); // Permitir que a linha cresça
            rowConstraints.setFillHeight(true); // Permite que o conteúdo se expanda para a altura total
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
        // Célula (1,1): TableView com cabeçalhos "ID", "Endereço" e "Porta"
        this.tabela = new TableView<>();

        // Coluna de ID
        TableColumn<Conexao, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Coluna de Endereço
        TableColumn<Conexao, String> colEndereco = new TableColumn<>("Endereço");
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        // Coluna de Porta
        TableColumn<Conexao, Integer> colPorta = new TableColumn<>("Porta");
        colPorta.setCellValueFactory(new PropertyValueFactory<>("porta"));

        tabela.getColumns().add(colId);
        tabela.getColumns().add(colEndereco);
        tabela.getColumns().add(colPorta);

        // Adiciona os dados do Map para a tabela
        ObservableList<Conexao> data = FXCollections.observableArrayList();

        for (Map.Entry<Integer, SocketClientSide> entry : servidor.getConexoes().entrySet()) {
            Integer id = entry.getKey();
            SocketClientSide client = entry.getValue();
            data.add(new Conexao(id, client.getEndereco(), client.getPorta()));
        }

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

        // Célula (2,1): TextArea
        response();
        gridPane.add(this.responses, 2, 1); // Coluna 2, Linha 1
        GridPane.setFillWidth(this.responses, true);
        GridPane.setFillHeight(this.responses, true);
        GridPane.setHalignment(this.responses, javafx.geometry.HPos.CENTER); // Centraliza o TextArea horizontalmente
        GridPane.setValignment(this.responses, javafx.geometry.VPos.CENTER); // Centraliza o TextArea verticalmente

        this.scene = new Scene(gridPane, 800, 600);

    }

}
