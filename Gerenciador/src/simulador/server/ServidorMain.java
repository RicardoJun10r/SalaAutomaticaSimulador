package simulador.server;

import javafx.application.Application;
import javafx.stage.Stage;

public class ServidorMain extends Application {

    @Override
    public void start(Stage arg0) throws Exception {
        Servidor servidor = new Servidor();
        arg0.setTitle("SIMULADOR");
        arg0.setScene(servidor.getScene());
        arg0.setMaximized(true);
        arg0.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
