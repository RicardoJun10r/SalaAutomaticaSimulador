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
    
    private final int ID;
    
    private final Sala SALA;
    
    public MicrocontroladorV3(int id, Sala sala, String ENDERECO_SERVER, int PORTA_SERVER){
        this.ID = id;
        this.SALA = sala;
        this.ENDERECO_SERVER = ENDERECO_SERVER;
        this.PORTA_SERVER = PORTA_SERVER;
    }
    
    private boolean isNumber(String mensagem){
        for(int i = 0; i < mensagem.length(); i++){
            if(!(mensagem.charAt(i) >= 48 && mensagem.charAt(i) <= 57)) return false;
        }
        return true;
    }
    
    public ClientSocket getClientSocket() {
        return clientSocket;
    }
    
    @Override
    public void run(){
        while (true) {
            String mensagem;
            if((mensagem = this.clientSocket.getMessage()) != null){
                System.out.println(
                    "Mensagem do servidor: " + mensagem
                );
                if(isNumber(mensagem)){
                    executeCommand(
                        Integer.parseInt(mensagem)
                    );
                }
            }
        }
    }

    /**
     * 
     * -----------MENU-----------
     * 
     * 0 --> DESLIGAR TUDO
     * 1 --> LIGAR TUDO
     * 2 --> MOSTRAR APARELHOS COM STATUS
     * 
     * 
     */

    private void executeCommand(int option){
        switch (option) {
            case 0:
            {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.desligarAparelhos());
                break;
            }
            case 1:
            {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.ligarAparelhos());
                break;
            }
            case 2:
            {
                sendMessage("ID = [ " + ID + " ]: " + this.SALA.mostrarAparelhos());
                break;
            }
            default:
            {
                sendMessage("ID = [ " + ID + " ]: " + "ERRO: opção inválida!");
                break;
            }
        }
    }

    private void sendMessage(String mensagem){
        this.clientSocket.sendMessage(mensagem);
    }

    public void start() throws IOException, UnknownHostException{
        try {
            clientSocket = new ClientSocket(
                new Socket(ENDERECO_SERVER, PORTA_SERVER)
            );
            System.out.println("Cliente conectado ao servidor de endereço = " + ENDERECO_SERVER + " na porta = " + PORTA_SERVER);
            new Thread(this).start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
