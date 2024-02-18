package SimulacaoV2.clientV2;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import salaAula.Sala;
import util.ClientSocket;

public class MicrocontroladorV3 implements Runnable {

    private final String ENDERECO_SERVER;

    private final int PORTA_SERVER;

    private ClientSocket clientSocket;

    private final String ID;

    private final Sala SALA;

    public MicrocontroladorV3(String id, Sala sala, String ENDERECO_SERVER, int PORTA_SERVER) {
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
                executeCommand(mensagem);
            }
        }
    }

    private void executeCommand(String option) {
        switch (option) {
            case "0": {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                break;
            }
            case "1": {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                break;
            }
            case "2": {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
                break;
            }
            default: {
                sendMessage("ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                break;
            }
        }
    }

    private void sendMessage(String mensagem) {
        this.clientSocket.sendMessage(mensagem);
    }

    public void start() throws IOException, UnknownHostException {
        try {
            clientSocket = new ClientSocket(
                    new Socket(ENDERECO_SERVER, PORTA_SERVER));
            System.out.println(
                    "Microcontrolador conectado ao servidor de endereço = " + ENDERECO_SERVER + " na porta = " + PORTA_SERVER);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
