package SimulacaoV2.serverV2;

import java.util.Arrays;
import java.util.Scanner;

import util.MessageV2;
import util.TableHelper;
import util.HashTable.Table;

public class ServerV2 {

    private MessageV2 socket;

    private Scanner scanner;

    private String login;

    private Table<TableHelper, Integer> table;

    private void serverLoop() {

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

    private void enviarMensagem() {
        while (true) {
            menu();
            String input = this.scanner.nextLine();
            System.out.println("HOST(ex: 127.0.0.1):");
            String host_name = this.scanner.nextLine();
            System.out.println("PORTA(ex: 8080):");
            int porta = this.scanner.nextInt();
            enviar(
                    input,
                    host_name,
                    porta);
        }
    }

    private void enviar(String msg, String host, int port) {
        this.socket.setBuffer_entrada(
                msg.getBytes());
        this.socket.setAddress_destino(
                host);
        this.socket.setPORTA_DESTINO(
                port);
        this.socket.enviar();
    }

    private void menu() {
        System.out.println("SERVIDOR ESTÁ APONTANDO PARA A PORTA: " + this.socket.getPORTA_DESTINO());
        System.out.println(
                "OPÇÕES\n[0] --> DESLIGAR TODOS\n[1] --> LIGAR TODOS\n[2] --> MOSTRAR TODOS\n[3] --> SCANEAR REDE\n[4] --> LIMPAR TELA\n[5] --> MUDAR PORTA DESTINO");
    }

    /**
     * COMANDOS:
     * ping
     * command
     * response
     */

    private void receberMensagens() {
        String[] msg = this.socket.receber().split(" ");
        switch (msg[0]) {
            case "ping": {
                System.out.println("PING");
                System.out.println(
                        Boolean.parseBoolean(msg[1]) == true ? "SERVER - Conectado" : "MICROCONTROLADOR - Conectado");
                this.table.Adicionar(
                        new TableHelper(msg[4], msg[2], Integer.parseInt(msg[3]), Boolean.parseBoolean(msg[1])),
                        Integer.parseInt(msg[4]));
                break;
            }
            case "command": {
                System.out.println("COMMAND");
                command(msg);
                break;
            }
            case "reponse": {
                System.out.println("RESPONSE");
                System.out.println(Arrays.toString(msg));
                break;
            }
            default: {
                System.out.println("Mensagem não seguiu o protocolo !");
                break;
            }
        }
    }

    private void command(String[] args) {
        String host = args[2];
        int port = Integer.parseInt(args[3]);
        switch (args[1]) {
            case "100": {
                String toList = this.table.Print();
                enviar(toList, host, port);
                break;
            }
            case "200": {
                TableHelper toHandle = this.table.BuscarCF(Integer.parseInt(args[4])).getValor();
                enviar("ligar",
                        toHandle.getHost_name(),
                        toHandle.getPort());
                enviar("response ligado",
                        host,
                        port);
                break;
            }
            case "300":
                TableHelper toHandle = this.table.BuscarCF(Integer.parseInt(args[4])).getValor();
                enviar("desligar",
                        toHandle.getHost_name(),
                        toHandle.getPort());
                enviar("response desligado",
                        host,
                        port);
                break;
            default:
                System.out.println("Comando não encontrado !");
                enviar("Comando não encontrado !", host, port);
                break;
        }
    }

}
