package SimulacaoV2.serverV2;

import java.net.SocketException;
import java.net.UnknownHostException;
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

    public ServerV2(int porta, String address) {
        try {
            this.socket = new MessageV2(porta, porta, address, address, true);
            this.scanner = new Scanner(System.in);
            this.table = new Table<>();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        serverLoop();
    }

    private void serverLoop() {

        System.out.println("Seu login");
        this.login = this.scanner.nextLine();
        System.out.println("INICIANDO");
        while (true) {
            new Thread(() -> {
                enviarMensagem();
            }).start();
            new Thread(() -> {
                receberMensagens();
            }).start();
        }
    }

    private synchronized void enviarMensagem() {
        while (true) {
            menu();
            String input = this.scanner.nextLine();
            if (input.equals("3")) {
                menuServer();
                input = this.scanner.nextLine();
                if (input.equals("ping")) {
                    buildSend(input);
                } else if (input.equals("command")) {
                    System.out.println(
                            "OPÇÕES\n[100] --> MOSTRAR TODOS\n[200] --> LIGAR TODOS\n[300] --> DESLIGAR TODOS\n");
                    input = this.scanner.nextLine();
                    buildSend(input);
                }
            } else {
                buildSend(input);
            }
        }
    }

    private void buildSend(String input) {
        System.out.println("HOST(ex: 127.0.0.1):");
        String host_name = this.scanner.nextLine();
        System.out.println("PORTA(ex: 8080):");
        int porta = this.scanner.nextInt();
        enviar(
                input,
                host_name,
                porta);
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
                "OPÇÕES\ndesligar --> DESLIGAR TODOS\nligar --> LIGAR TODOS\nmostrar --> MOSTRAR TODOS\n[3] --> CONECTAR-SE A OUTRO SERVER\n");
    }

    private void menuServer() {
        System.out.println("COMANDOS\nping --> CONECTAR\ncommand --> EXECUTAR COMANDO NO SERVER\n");
    }

    /**
     * COMANDOS:
     * ping
     * command
     * response
     */

    private void receberMensagens() {
        String[] msg = this.socket.receber().split(" ");
        if(msg.length > 0){
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
