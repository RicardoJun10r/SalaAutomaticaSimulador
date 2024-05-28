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
                executeCommand(mensagem);
            }
        }
    }

    private void executeCommand(String option) {
        switch (option) {
            case "0", "4": {
                unicast("ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                break;
            }
            case "1", "3": {
                unicast("ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                break;
            }
            case "2", "5": {
                unicast("ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
                break;
            }
            default: {
                unicast("ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                break;
            }
        }
    }

    private void unicast(String mensagem) {
        String headers = "res;MC;" + clientSocket.getSocket().getLocalSocketAddress().toString() + ";";
        this.clientSocket.sendMessage(headers + mensagem);
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
