package SimulacaoV2.serverV2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import util.ClientSocket;
import util.MessageV2;
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

    private MessageV2 client;

    public ServerV3(String ENDERECO, int PORTA) {
        this.scan = new Scanner(System.in);
        this.ENDERECO = ENDERECO;
        this.PORTA = PORTA;
        // this.table = new Table<>();
        try {
            this.client = new MessageV2(PORTA+1, -1, ENDERECO, null, true);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
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
        //         this.table.Print());
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
                System.out.println(
                        "Mensagem de " + clientSocket.getSocketAddress() + ": " + mensagem);
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
            while (true) {

                mostrarOpcoes();

                opcao = this.scan.next();

                switch (opcao) {
                    case "0": {
                        microcontroladorOpcoes();
                        infoDestino(opcao, endereco, porta);
                        if(validarEntrada(opcao, endereco, porta)){
                            mensagem = opcao;
                            destinatario = "/" + endereco + ":" + porta;
                            sendMessageTo(destinatario, mensagem);
                        }
                        break;
                    }
                    case "1": {
                        controlarServer();
                        infoDestino(opcao, endereco, porta);
                        if(validarEntrada(opcao, endereco, porta)){
                            mensagem = opcao;
                            destinatario = "/" + endereco + ":" + porta;
                            sendMessageTo(destinatario, mensagem);
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

    private void infoDestino(String opcao, String endereco, String porta) {
        System.out.println("Opção (ex: 3)");
        opcao = this.scan.next();
        System.out.println("Endereço (ex: localhost | 127.0.0.1)");
        endereco = this.scan.next();
        System.out.println("Porta (ex: 8080)");
        porta = this.scan.next();
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
