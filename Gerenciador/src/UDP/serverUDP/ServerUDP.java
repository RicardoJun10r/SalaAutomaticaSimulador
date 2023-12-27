package UDP.serverUDP;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

import util.MessageUDP;

public class ServerUDP {

    private MessageUDP messageUDP;

    private Scanner scanner;

    public ServerUDP(int PORTA_PROPRIA, int PORTA_DESTINO, String address) throws SocketException, UnknownHostException{
        this.messageUDP = new MessageUDP(PORTA_PROPRIA, PORTA_DESTINO, address);
        this.scanner = new Scanner(System.in);
    }

    /*
     *
     *  VETOR PACOTE
     * TAMANHO: 3
     * POSIÇÕES
     *  1. ID DA SALA
     *  2. OPCAO
     *  3. PORTA
     * 
     */

    private void serverLoop(){
        while (true) {
            StringBuffer stringBuffer = new StringBuffer();
            menu();
            int opcao = scanner.nextInt();
            if(opcao == 5){
                System.out.println("Onde você deseja se conectar ?");
                this.messageUDP.setPORTA_DESTINO( scanner.nextInt() );
            } else if(opcao == 4){
                limparTela();
            } else if(opcao == 3){
                stringBuffer.append(-1).append(" ").append(opcao).append(" ").append(this.messageUDP.getPORTA_PROPRIA());
                this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                this.messageUDP.enviar();
                String[] response = this.messageUDP.receber().split(" ");
                System.out.println("RESPOSTA: " + Arrays.toString(response));
            } else {
                System.out.println("Qual ID da sala ?");
                int ID_SALA = scanner.nextInt();
                stringBuffer.append(ID_SALA).append(" ").append(opcao).append(" ").append(this.messageUDP.getPORTA_PROPRIA());
                this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                this.messageUDP.enviar();
                String[] response = this.messageUDP.receber().split(" ");
                System.out.println("RESPOSTA: " + Arrays.toString(response));
            }
        }
    }

    private void limparTela(){
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }
    
    public void start(){
        try {
            System.out.println("SERVIDOR INICIADO NA PORTA: " + this.messageUDP.getPORTA_PROPRIA());
            serverLoop();
        } finally {
            this.messageUDP.getSocket().close();
        }
    }

    private void menu(){
        System.out.println("SERVIDOR ESTÁ APONTANDO PARA A PORTA: " + this.messageUDP.getPORTA_DESTINO());
        System.out.println(
            "OPÇÕES\n[0] --> DESLIGAR TODOS\n[1] --> LIGAR TODOS\n[2] --> MOSTRAR TODOS\n[3] --> SCANEAR REDE\n[4] --> LIMPAR TELA\n[5] --> MUDAR PORTA DESTINO"
        );
    }

}
