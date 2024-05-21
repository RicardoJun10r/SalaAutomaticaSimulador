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

    public ServerV4(int PORTA) {
        CONTADOR = 0;
        this.scan = new Scanner(System.in);
        this.PORTA = PORTA;
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
            users += index + " -> " + this.USUARIOS.get(index).getSocketAddress().toString() + "\n";
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
        System.out.println(
                "OPCOES DO SERVER\n" + //
                        "[0] --> DESLIGAR SALA\n" + //
                        "[1] --> LIGAR SALA\n" + //
                        "[2] --> LISTAR CONEXOES\n" + //
                        "[3] --> DESLIGAR TODAS AS SALAS\n" + //
                        "[4] --> LIGAR TODAS AS SALAS\n" + //
                        "[5] --> DESCREVER TODAS AS SALAS\n" + //
                        "[6] --> DESCREVER SALA");
    }

    private void mensagemCliente(ClientSocket clientSocket) throws IOException, InterruptedException {
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                String[] msg = mensagem.split(";");
                if (msg[0].equals("req")) {
                    String[] req = mensagem.split(" ");
                    String dest;
                    String res;
                    switch (req[1]) {
                        case "0":
                            dest = "/" + req[2] + ":" + req[3];
                            unicast(dest, "0");
                            System.out.println("deslistando");
                            unicast(clientSocket.getSocketAddress().toString(), "Desligado!");
                            break;
                        case "1":
                            dest = "/" + req[2] + ":" + req[3];
                            unicast(dest, "1");
                            System.out.println("ligando " + clientSocket.getSocketAddress().toString());
                            unicast(clientSocket.getSocketAddress().toString(), "Ligado!");
                            break;
                        case "2":
                            System.out.println("listando");
                            res = listarUsuarios();
                            unicast(clientSocket.getSocketAddress().toString(), res);
                            break;
                        default:
                            System.out.println("Erro!");
                            unicast(clientSocket.getSocketAddress().toString(), "Erro!");
                            break;
                    }
                } else if (msg[0].equals("res")) {
                    System.out.println(
                            msg[1] + " [ " + msg[2] + " ]: " + msg[3]);
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
            String opcao, mensagem, id, endereco, porta;
            String[] res;

            while (true) {

                Thread.sleep(300);

                mostrarOpcoes();

                opcao = this.scan.next();

                switch (opcao) {
                    case "0": {
                        microcontroladorOpcoes();
                        System.out.println("Opção (ex: 3)");
                        opcao = this.scan.next();
                        if(Integer.parseInt(opcao) <= 2){
                            System.out.println("ID da sala (ex: 0)");
                            id = this.scan.next();
                            if(validarEntrada(opcao, id)){
                                unicast(id, opcao);
                            }
                        } else{
                            broadcast(opcao);
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
                            System.out.println("Quer controlar uma sala especifíca (S | N) ?");
                            String y_n = this.scan.next();
                            mensagem = "req" + " " + opcao;
                            if (y_n.equalsIgnoreCase("s")) {
                                System.out.println("Endereço da sala (ex: 127.0.0.1)");
                                String endereco_destino = this.scan.next();
                                System.out.println("Porta da sala (ex: 8008)");
                                String porta_destino = this.scan.next();
                                mensagem += " " + endereco_destino + " " + porta_destino;
                            }
                            ClientSocket socket = new ClientSocket(
                                    new Socket(endereco, Integer.parseInt(porta)));
                            addConnection(socket);
                            socket.sendMessage(mensagem);
                            Thread.sleep(300);
                            System.out.println(socket.getMessage());
                            socket.close();
                        }
                        break;
                    }
                    case "2": {
                        System.out.println(listarUsuarios());
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
        System.out.println("ID (ex: 0)");
        res[1] = this.scan.next();
        return res;
    }

    private boolean validarEntrada(String opcao, String endereco, String porta) {
        boolean flag = true;
        if (opcao.isEmpty() || endereco.isEmpty() || porta.isEmpty())
            flag = false;
        return flag;
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

    private void broadcast(String msg){
        Set<Integer> chaves = this.USUARIOS.keySet();
        Iterator<Integer> iterator = chaves.iterator();
        Integer index;
        while (iterator.hasNext()) {
            index = iterator.next();
            this.USUARIOS.get(index).sendMessage(msg);
        }
    }

}
