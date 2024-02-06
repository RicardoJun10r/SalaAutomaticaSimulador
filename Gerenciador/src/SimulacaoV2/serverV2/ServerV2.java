package SimulacaoV2.serverV2;

import java.util.Arrays;
import java.util.Scanner;

import util.MessageV2;

public class ServerV2 {
    
    private MessageV2 socket;

    private Scanner scanner;

    private String login;

    private void serverLoop(){

        System.out.println("Seu login");
        this.login = this.scanner.nextLine();

        while (true) {
            new Thread(() -> {
                receberMensagens();
            });
            new Thread(() -> {
                enviarMensagem();
            });
        }
    }

    private void enviarMensagem(){
        while (true) {
            
        }
    }

    private void menu(){
        System.out.println("SERVIDOR ESTÁ APONTANDO PARA A PORTA: " + this.socket.getPORTA_DESTINO());
        System.out.println(
            "OPÇÕES\n[0] --> DESLIGAR TODOS\n[1] --> LIGAR TODOS\n[2] --> MOSTRAR TODOS\n[3] --> SCANEAR REDE\n[4] --> LIMPAR TELA\n[5] --> MUDAR PORTA DESTINO"
        );
    }

    private void receberMensagens(){
        String[] msg = this.socket.receber().split(" ");
        System.out.println(Arrays.toString(msg));
    }

}
