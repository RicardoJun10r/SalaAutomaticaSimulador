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
import util.TableHelper;
import util.HashTable.Table;

public class ServerV3 {

    private final int PORTA;

    private final String ENDERECO;

    private ServerSocket serverSocket;

    // private Table<ClientSocket, String> table;

    private String login;

    private final Set<ClientSocket> USUARIOS = Collections.synchronizedSet(new HashSet<>());

    private final Scanner scan;

    private final Object lock = new Object();

    public ServerV3(String ENDERECO, int PORTA) {
        this.scan = new Scanner(System.in);
        this.ENDERECO = ENDERECO;
        this.PORTA = PORTA;
        // this.table = new Table<>();
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
            // table.Adicionar(clientSocket, clientSocket.getId());
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

        // System.out.println(
        // this.table.Print());
    }

    private void mostrarOpcoes() {
        System.out.println(
                "------OPÇÕES------\n" +
                        "[0] --> USAR MICROCONTROLADOR\n" +
                        "[1] --> CONECTAR-SE A OUTRO SERVIDOR\n" +
                        "[2] --> LISTAR CONEXÕES\n" +
                        "Opção:");
    }

    private void microcontroladorOpcoes() {
        System.out.println(
                "------OPÇÕES DO MICROCONTROLADOR------\n" +
                        "[0] --> DESLIGAR SALA\n" +
                        "[1] --> LIGAR SALA\n" +
                        "[2] --> DESCRIÇÃO DA SALA");
    }

    private void controlarServer() {
        System.out.println(
                "------OPÇÕES DO SERVER------\n" +
                        "[0] --> DESLIGAR SALA\n" +
                        "[1] --> LIGAR SALA\n" +
                        "[2] --> DESCRIÇÃO DA SALA\n" +
                        "[3] --> LISTAR CONEXÕES");
    }

    private void mensagemCliente(ClientSocket clientSocket) throws IOException, InterruptedException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                if (mensagem.contains("req")) {
                    String[] req = mensagem.split(" ");
                    switch (req[1]) {
                        case "0":
                        System.out.println("deslistando");

                            sendMessageTo(clientSocket.getSocketAddress().toString(), "Desligado!");
                            break;
                        case "1":
                        System.out.println("ligando " + clientSocket.getSocketAddress().toString());

                            sendMessageTo(clientSocket.getSocketAddress().toString(), "Ligado!");
                            break;
                        case "2":
                        System.out.println("listando");
                            String res = this.USUARIOS.toString();
                            sendMessageTo(clientSocket.getSocketAddress().toString(), res);
                            break;
                        default:
                        System.out.println("erro");
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
                            mensagem = "req" + " " + opcao;
                            ClientSocket socket = new ClientSocket(
                                    new Socket(endereco, Integer.parseInt(porta)));
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
