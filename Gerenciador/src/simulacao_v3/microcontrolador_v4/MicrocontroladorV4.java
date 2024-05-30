package simulacao_v3.microcontrolador_v4;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import salaAula.Sala;
import util.ClientSocket;

public class MicrocontroladorV4 implements Runnable {

    private final String ENDERECO_SERVER;

    private final int PORTA_SERVER;

    private ClientSocket clientSocket;

    private final String ID;

    private final Sala SALA;

    private String HEADERS;

    public MicrocontroladorV4(String id, Sala sala, String ENDERECO_SERVER, int PORTA_SERVER) {
        this.ID = id;
        this.SALA = sala;
        this.ENDERECO_SERVER = ENDERECO_SERVER;
        this.PORTA_SERVER = PORTA_SERVER;
    }

    public ClientSocket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        while (true) {
            String mensagem;
            if ((mensagem = this.clientSocket.getMessage()) != null) {
                System.out.println(
                        "Mensagem do servidor: " + mensagem);
                if (mensagem.contains("req")) {
                    forward(mensagem);
                } else {
                    executeCommand(mensagem);
                }
            }
        }
    }

    private void forward(String mensagem) {
        String[] msg = mensagem.split(";");
        switch (msg[0]) {
            case "0", "4": {
                unicast("fwd;" + msg[3] + ";ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                break;
            }
            case "1", "3": {
                unicast("fwd;" + msg[3] + ";ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                break;
            }
            case "2", "5": {
                unicast("fwd;" + msg[3] + ";ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
                break;
            }
            default: {
                unicast("fwd;" + msg[3] + ";ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                break;
            }
        }
    }

    private void executeCommand(String option) {
        switch (option) {
            case "0", "4": {
                unicast(HEADERS + "ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                break;
            }
            case "1", "3": {
                unicast(HEADERS + "ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                break;
            }
            case "2", "5": {
                unicast(HEADERS + "ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
                break;
            }
            default: {
                unicast(HEADERS + "ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                break;
            }
        }
    }

    private void unicast(String mensagem) {
        this.clientSocket.sendMessage(mensagem);
    }

    public void start() throws IOException, UnknownHostException {
        try {
            clientSocket = new ClientSocket(
                    new Socket(ENDERECO_SERVER, PORTA_SERVER));
            System.out.println(
                    "Microcontrolador conectado ao servidor de endereço = " + ENDERECO_SERVER + " na porta = "
                            + PORTA_SERVER);
            HEADERS = "res;MC;" + clientSocket.getSocket().getLocalSocketAddress().toString() + ";";
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
