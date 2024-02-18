package SimulacaoV2.serverV2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import util.ClientSocket;

public class ServerV3 {

    private final int PORTA;

    private ServerSocket serverSocket;

    private String login;

    private final Set<ClientSocket> USUARIOS = Collections.synchronizedSet(new HashSet<>());

    private final Scanner scan;

    private final Object lock = new Object();

    public ServerV3(int PORTA) {
        this.scan = new Scanner(System.in);
        this.PORTA = PORTA;
    }

    public void start() throws IOException {
        System.out.println("Qual o seu login ?");
        this.login = this.scan.nextLine();
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor [ " + login + " ] na porta [ " + PORTA + " ]");
        serverLoop();
    }

    private void serverLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            new Thread(() -> {
                try {
                    mensagemCliente(clientSocket);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    sendOrder();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void listarUsuarios() {
        Iterator<ClientSocket> iterator = this.USUARIOS.iterator();
        while (iterator.hasNext()) {
            ClientSocket i = iterator.next();
            System.out.println(
                    i.getId() + " --> " + i.getSocketAddress());
        }
    }

    private void mostrarOpcoes() {
        System.out.println(" _ __ ___   ___ _ __  _   _ \n" + //
                "| '_ ` _ \\ / _ \\ '_ \\| | | |\n" + //
                "| | | | | |  __/ | | | |_| |\n" + //
                "|_| |_| |_|\\___|_| |_|\\__,_|");
        System.out.println(
                "OPCOES\n" + //
                        "[0] --> USAR MICROCONTROLADOR\n" + //
                        "[1] --> CONECTAR-SE A OUTRO SERVIDOR\n" + //
                        "[2] --> LISTAR CONEXOES\n" + //
                        "OPCAO:");
    }

    private void microcontroladorOpcoes() {
        System.out.println("\n" + //
                "  __  __  _                                       _                _             _              \n" + //
                " |  \\/  |(_)                                     | |              | |           | |             \n" + //
                " | \\  / | _   ___  _ __  ___    ___  ___   _ __  | |_  _ __  ___  | |  __ _   __| |  ___   _ __ \n" + //
                " | |\\/| || | / __|| '__|/ _ \\  / __|/ _ \\ | '_ \\ | __|| '__|/ _ \\ | | / _` | / _` | / _ \\ | '__|\n"
                + //
                " | |  | || || (__ | |  | (_) || (__| (_) || | | || |_ | |  | (_) || || (_| || (_| || (_) || |   \n" + //
                " |_|  |_||_| \\___||_|   \\___/  \\___|\\___/ |_| |_| \\__||_|   \\___/ |_| \\__,_| \\__,_| \\___/ |_|   \n"
                + //
                "                                                                                                \n" + //
                "                                                                                                \n" + //
                "");
        System.out.println(
                "OPCOES DO MICROCONTROLADOR\n" + //
                        "[0] --> DESLIGAR SALA\n" + //
                        "[1] --> LIGAR SALA\n" + //
                        "[2] --> DESCRICAO DA SALA");
    }

    private void controlarServer() {
        System.out.println("\n" + //
                "   _____                               \n" + //
                "  / ____|                              \n" + //
                " | (___    ___  _ __ __   __ ___  _ __ \n" + //
                "  \\___ \\  / _ \\| '__|\\ \\ / // _ \\| '__|\n" + //
                "  ____) ||  __/| |    \\ V /|  __/| |   \n" + //
                " |_____/  \\___||_|     \\_/  \\___||_|   \n" + //
                "                                       \n" + //
                "                                       \n" + //
                "");
        System.out.println(
                "OPCOES DO SERVER\n" + //
                        "[0] --> DESLIGAR SALA\n" + //
                        "[1] --> LIGAR SALA\n" + //
                        "[2] --> DESCRICAO DA SALA\n" + //
                        "[3] --> LISTAR CONEXOES");
    }

    private void mensagemCliente(ClientSocket clientSocket) throws IOException, InterruptedException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                if (mensagem.contains("req")) {
                    String[] req = mensagem.split(" ");
                    String dest;
                    switch (req[1]) {
                        case "0":
                            dest = "/" + req[2] + ":" + req[3];
                            sendMessageTo(dest, "0");
                            System.out.println("deslistando");
                            sendMessageTo(clientSocket.getSocketAddress().toString(), "Desligado!");
                            break;
                        case "1":
                            dest = "/" + req[2] + ":" + req[3];
                            sendMessageTo(dest, "1");
                            System.out.println("ligando " + clientSocket.getSocketAddress().toString());
                            sendMessageTo(clientSocket.getSocketAddress().toString(), "Ligado!");
                            break;
                        case "2":
                            System.out.println("listando");
                            String res = this.USUARIOS.toString();
                            sendMessageTo(clientSocket.getSocketAddress().toString(), res);
                            break;
                        default:
                            System.out.println("Erro!");
                            sendMessageTo(clientSocket.getSocketAddress().toString(), "Erro!");
                            break;
                    }
                } else {
                    System.out.println(
                            "Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendOrder() throws IOException, InterruptedException {
        synchronized (lock) {
            String opcao;
            String mensagem;
            String endereco = "";
            String porta = "";
            String destinatario;
            String[] res;

            while (true) {

                Thread.sleep(300);

                mostrarOpcoes();

                opcao = this.scan.next();

                switch (opcao) {
                    case "0": {
                        microcontroladorOpcoes();
                        res = infoDestino();
                        opcao = res[0];
                        endereco = res[1];
                        porta = res[2];
                        if (validarEntrada(opcao, endereco, porta)) {
                            mensagem = opcao;
                            destinatario = "/" + endereco + ":" + porta;
                            sendMessageTo(destinatario, mensagem);
                        }
                        break;
                    }
                    case "1": {
                        controlarServer();
                        res = infoDestino();
                        opcao = res[0];
                        endereco = res[1];
                        porta = res[2];
                        if (validarEntrada(opcao, endereco, porta)) {
                            System.out.println("Endereço da sala (ex: 127.0.0.1)");
                            String endereco_destino = this.scan.next();
                            System.out.println("Porta da sala (ex: 8008)");
                            String porta_destino = this.scan.next();
                            mensagem = "req" + " " + opcao + " " + endereco_destino + " " + porta_destino;
                            ClientSocket socket = new ClientSocket(
                                    new Socket(endereco, Integer.parseInt(porta)));
                            this.USUARIOS.add(socket);
                            socket.sendMessage(mensagem);
                            Thread.sleep(300);
                            System.out.println(socket.getMessage());
                            socket.close();
                        }
                        break;
                    }
                    case "2": {
                        listarUsuarios();
                        break;
                    }
                    default: {
                        System.out.println("Opção inválida!");
                        break;
                    }
                }
            }
        }
    }

    private String[] infoDestino() {
        String[] res = new String[3];
        System.out.println("Opção (ex: 3)");
        res[0] = this.scan.next();
        System.out.println("Endereço (ex: 127.0.0.1)");
        res[1] = this.scan.next();
        System.out.println("Porta (ex: 8080)");
        res[2] = this.scan.next();
        return res;
    }

    private boolean validarEntrada(String opcao, String endereco, String porta) {
        boolean flag = true;
        if (opcao.isEmpty() || endereco.isEmpty() || porta.isEmpty())
            flag = false;
        return flag;
    }

    private void sendMessageTo(String destinataio, String mensagem) {
        Iterator<ClientSocket> iterator = this.USUARIOS.iterator();
        while (iterator.hasNext()) {
            ClientSocket i = iterator.next();
            if (i.getSocketAddress().toString().equals(destinataio)) {
                if (!i.sendMessage(mensagem))
                    iterator.remove();
            }
        }
    }

}
