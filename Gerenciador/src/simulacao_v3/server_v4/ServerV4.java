package simulacao_v3.server_v4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import util.ClientSocket;

public class ServerV4 {

    private final int PORTA;

    private ServerSocket serverSocket;

    private Map<Integer, ClientSocket> USUARIOS = Collections.synchronizedMap(new HashMap<>());

    private final Scanner scan;

    private final Object lock = new Object();

    private Integer CONTADOR;

    private Boolean web_server;

    public ServerV4(int PORTA, Boolean web_server) {
        CONTADOR = 0;
        this.scan = new Scanner(System.in);
        this.PORTA = PORTA;
        this.web_server = web_server;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor na porta [ " + PORTA + " ]");
        serverLoop();
    }

    private void serverLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            addConnection(clientSocket);
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

    private void addConnection(ClientSocket socket) {
        USUARIOS.put(CONTADOR, socket);
        CONTADOR++;
    }

    private String listarUsuarios() {
        String users = "";
        Set<Integer> chaves = this.USUARIOS.keySet();
        Iterator<Integer> iterator = chaves.iterator();
        Integer index;
        while (iterator.hasNext()) {
            index = iterator.next();
            users += index + " -> " + this.USUARIOS.get(index).getSocketAddress().toString() + "*";
        }
        return users;
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
                        "[3] --> ADICIONAR CONEXÃO\n" + //
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
        System.out.println(listarUsuarios().replace('*', '\n'));
        System.out.println(
                "OPCOES DO MICROCONTROLADOR\n" + //
                        "[0] --> DESLIGAR SALA\n" + //
                        "[1] --> LIGAR SALA\n" + //
                        "[2] --> DESCREVER SALA\n" + //
                        "[3] --> LIGAR TODAS AS SALAS\n" + //
                        "[4] --> DESLIGAR TODAS AS SALAS\n" + //
                        "[5] --> DESCREVER TODAS AS SALAS");
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
        System.out.println(listarUsuarios().replace('*', '\n'));
        System.out.println(
                "OPCOES DO SERVER\n" + //
                        "[0] --> DESLIGAR SALA\n" + //
                        "[1] --> LIGAR SALA\n" + //
                        "[2] --> DESCREVER SALA\n" + //
                        "[3] --> LIGAR TODAS AS SALAS\n" + //
                        "[4] --> DESLIGAR TODAS AS SALAS\n" + //
                        "[5] --> DESCREVER TODAS AS SALAS\n" + //
                        "[6] --> LISTAR CONEXOES");
    }

    private void mensagemCliente(ClientSocket clientSocket) throws IOException, InterruptedException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                if (msg[0].equals("req")) {
                    String ip = msg[2], opcao = msg[3], id;
                    switch (opcao) {
                        case "0":
                            id = msg[4];
                            unicast(id, "0;req;PC;" + ip);
                            break;
                        case "1":
                            id = msg[4];
                            unicast(id, "1;req;PC;" + ip);
                            break;
                        case "2":
                            id = msg[4];
                            unicast(id, "2;req;PC;" + ip);
                            break;
                        case "3":
                            broadcast("3;req;PC;" + ip);
                            break;
                        case "4":
                            broadcast("4;req;PC;" + ip);
                            break;
                        case "5":
                            broadcast("5;req;PC;" + ip);
                            break;
                        case "6":
                            String conn = listarUsuarios();
                            __unicast__(ip,
                                    "res;PC;" + this.serverSocket.getLocalSocketAddress().toString() + ";" + conn);
                            break;
                        default:
                            System.out.println("ERRO NA MENSAGEM");
                            break;
                    }
                } else if (msg[0].equals("res")) {
                    System.out.println(
                            msg[1] + " [ " + msg[2] + " ]: " + msg[3].replace('*', '\n'));
                } else if(msg[0].equals("fwd")){
                    __unicast__(msg[1], "res;PC;" + this.serverSocket.getLocalSocketAddress().toString() + ";" + msg[2]);
                } else {
                    System.out.println("ERRO: MENSGAGEM FORA DO PADRÃO!");
                }
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendOrder() throws IOException, InterruptedException {
        synchronized (lock) {
            String opcao, id, sala, endereco, porta;

            while (true) {

                Thread.sleep(300);

                mostrarOpcoes();

                opcao = this.scan.next();

                switch (opcao) {
                    case "0": {
                        microcontroladorOpcoes();
                        System.out.println("Opção (ex: 3)");
                        opcao = this.scan.next();
                        if (Integer.parseInt(opcao) <= 2) {
                            System.out.println("ID da sala (ex: 0)");
                            id = this.scan.next();
                            if (validarEntrada(opcao, id)) {
                                node_server(this.serverSocket.getLocalSocketAddress().toString() + ";0;" + opcao + ";" + USUARIOS.get(Integer.parseInt(id)).getSocketAddress().toString());
                                unicast(id, opcao);
                            }
                        } else {
                            node_server(this.serverSocket.getLocalSocketAddress().toString() + ";0;" + opcao + ";TODOS");
                            broadcast(opcao);
                        }
                        break;
                    }
                    case "1": {
                        controlarServer();
                        System.out.println("Opção (ex: 3)");
                        opcao = this.scan.next();
                        System.out.println("ID do SERVER (ex: 0)");
                        id = this.scan.next();
                        if (Integer.parseInt(opcao) <= 2) {
                            System.out.println("ID da sala (ex: 0)");
                            sala = this.scan.next();
                            if (validarEntrada(opcao, id)) {
                                node_server(this.serverSocket.getLocalSocketAddress().toString() + ";1;" + opcao + ";" + USUARIOS.get(Integer.parseInt(id)).getSocketAddress().toString());
                                unicast(id, "req;PC;" + this.serverSocket.getLocalSocketAddress().toString() + ";"
                                        + opcao + ";" + sala);
                            }
                        } else {
                            node_server(this.serverSocket.getLocalSocketAddress().toString() + ";1;" + opcao + ";TODOS");
                            unicast(id, "req;PC;" + this.serverSocket.getLocalSocketAddress().toString() + ";" + opcao);
                        }
                        break;
                    }
                    case "2": {
                        node_server(this.serverSocket.getLocalSocketAddress().toString() + ";2;listou todos os usuarios;TODOS");
                        System.out.println(listarUsuarios().replace('*', '\n'));
                        break;
                    }
                    case "3": {
                        System.out.println("Digite o endereço destino (ex: 127.0.0.1)");
                        endereco = this.scan.next();
                        System.out.println("Digite a porta de destino (ex: 1111)");
                        porta = this.scan.next();
                        ClientSocket socket = new ClientSocket(
                                new Socket(endereco, Integer.parseInt(porta)));
                        addConnection(socket);
                        node_server(this.serverSocket.getLocalSocketAddress().toString() + ";3;conectou-se a outro server;" + endereco + ":" + porta);
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

    private boolean validarEntrada(String opcao, String id) {
        boolean flag = true;
        if (opcao.isEmpty() || id.isEmpty())
            flag = false;
        return flag;
    }

    private void unicast(String id, String msg) {
        this.USUARIOS.get(
                Integer.parseInt(id)).sendMessage(msg);
    }

    private void node_server(String msg){
        if(web_server){
            try {
                ClientSocket node = new ClientSocket(
                    new Socket("https://main--lucky-speculoos-bed9b6.netlify.app/web_server/index", 443)
                );
                node.sendMessage(msg);
                node.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ClientSocket node = new ClientSocket(
                    new Socket("127.0.0.1", 4000)
                );
                node.sendMessage(msg);
                node.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void __unicast__(String ip, String msg) {
        String[] address = ip.split(":");
        int index = address[0].indexOf("/");
        String host = address[0].substring(0, index);
        int port = Integer.parseInt(address[1]);
        System.out.println(host + ":" + port);
        try {
            ClientSocket socket = new ClientSocket(new Socket(host, port));
            socket.sendMessage(msg);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String msg) {
        Set<Integer> chaves = this.USUARIOS.keySet();
        Iterator<Integer> iterator = chaves.iterator();
        Integer index;
        while (iterator.hasNext()) {
            index = iterator.next();
            this.USUARIOS.get(index).sendMessage(msg);
        }
    }

}
