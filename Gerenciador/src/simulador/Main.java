package simulador;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;

public class Main extends Application {

    public static class Conexao {
        private final String endereco;
        private final String porta;

        public Conexao(String endereco, String porta) {
            this.endereco = endereco;
            this.porta = porta;
        }

        public String getEndereco() {
            return endereco;
        }

        public String getPorta() {
            return porta;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Criação do GridPane
        GridPane gridPane = new GridPane();

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

        // Célula (0,0): RadioButton Group e Button
        ToggleGroup group1 = new ToggleGroup();
        RadioButton rb1Option1 = new RadioButton("Opção 1");
        rb1Option1.setToggleGroup(group1);
        RadioButton rb1Option2 = new RadioButton("Opção 2");
        rb1Option2.setToggleGroup(group1);
        RadioButton rb1Option3 = new RadioButton("Opção 3");
        rb1Option3.setToggleGroup(group1);

        Button buttonGroup1 = new Button("Confirmar");
        VBox vBoxGroup1 = new VBox(10, rb1Option1, rb1Option2, rb1Option3, buttonGroup1); // Espaçamento de 10 entre os elementos
        vBoxGroup1.setAlignment(Pos.CENTER);  // Centralizar elementos dentro do VBox
        gridPane.add(vBoxGroup1, 0, 0);  // Coluna 0, Linha 0

        // Célula (1,0): Outro RadioButton Group e Button
        ToggleGroup group2 = new ToggleGroup();
        RadioButton rb2OptionA = new RadioButton("Opção A");
        rb2OptionA.setToggleGroup(group2);
        RadioButton rb2OptionB = new RadioButton("Opção B");
        rb2OptionB.setToggleGroup(group2);
        RadioButton rb2OptionC = new RadioButton("Opção C");
        rb2OptionC.setToggleGroup(group2);

        Button buttonGroup2 = new Button("Enviar");
        VBox vBoxGroup2 = new VBox(10, rb2OptionA, rb2OptionB, rb2OptionC, buttonGroup2); // Espaçamento de 10 entre os elementos
        vBoxGroup2.setAlignment(Pos.CENTER);  // Centralizar elementos dentro do VBox
        gridPane.add(vBoxGroup2, 1, 0);  // Coluna 1, Linha 0

        // Célula (2,0): Formulário com dois campos de texto e um botão
        TextField textField1Form1 = new TextField();
        textField1Form1.setPromptText("Campo 1");

        TextField textField2Form1 = new TextField();
        textField2Form1.setPromptText("Campo 2");

        Button buttonForm1 = new Button("Enviar Formulário");
        VBox vBoxForm1 = new VBox(10, textField1Form1, textField2Form1, buttonForm1); // Espaçamento de 10 entre elementos
        vBoxForm1.setAlignment(Pos.CENTER);  // Centralizar elementos dentro do VBox
        gridPane.add(vBoxForm1, 2, 0);  // Coluna 2, Linha 0

        // Célula (0,1): Outro formulário com os mesmos campos
        TextField textField1Form2 = new TextField();
        textField1Form2.setPromptText("Campo 1");

        TextField textField2Form2 = new TextField();
        textField2Form2.setPromptText("Campo 2");

        Button buttonForm2 = new Button("Enviar Formulário");
        VBox vBoxForm2 = new VBox(10, textField1Form2, textField2Form2, buttonForm2); // Espaçamento de 10 entre elementos
        vBoxForm2.setAlignment(Pos.CENTER);  // Centralizar elementos dentro do VBox
        gridPane.add(vBoxForm2, 0, 1);  // Coluna 0, Linha 1

        // Célula (1,1): TableView com cabeçalhos "Endereço" e "Porta"
        TableView<Conexao> tableView = new TableView<>();
        TableColumn<Conexao, String> colEndereco = new TableColumn<>("Endereço");
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        TableColumn<Conexao, String> colPorta = new TableColumn<>("Porta");
        colPorta.setCellValueFactory(new PropertyValueFactory<>("porta"));

        tableView.getColumns().add(colEndereco);
        tableView.getColumns().add(colPorta);

        ObservableList<Conexao> data = FXCollections.observableArrayList(
            new Conexao("192.168.1.1", "8080"),
            new Conexao("10.0.0.1", "9090")
        );

        tableView.setItems(data);
        gridPane.add(tableView, 1, 1);  // Coluna 1, Linha 1
        GridPane.setFillWidth(tableView, true);
        GridPane.setFillHeight(tableView, true);
        GridPane.setHalignment(tableView, javafx.geometry.HPos.CENTER);  // Centraliza a tabela horizontalmente
        GridPane.setValignment(tableView, javafx.geometry.VPos.CENTER);  // Centraliza a tabela verticalmente

        // Célula (2,1): TextArea
        TextArea textArea = new TextArea();
        textArea.setPromptText("Digite aqui...");
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Permitir que o TextArea cresça com a célula
        gridPane.add(textArea, 2, 1);  // Coluna 2, Linha 1
        GridPane.setFillWidth(textArea, true);
        GridPane.setFillHeight(textArea, true);
        GridPane.setHalignment(textArea, javafx.geometry.HPos.CENTER);  // Centraliza o TextArea horizontalmente
        GridPane.setValignment(textArea, javafx.geometry.VPos.CENTER);  // Centraliza o TextArea verticalmente

        // Configurando a cena
        Scene scene = new Scene(gridPane, 800, 600); // Tamanho inicial da cena
        primaryStage.setTitle("Minha Aplicação JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Abre a janela maximizada
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
