package simulador.server;

import java.util.Map;
import java.util.Queue;

import javafx.scene.control.TextArea;

import java.util.LinkedList;

import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketConnectionsFunction;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class ServidorSocket {

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private ISocketConnectionsFunction atualizarConexoes;

    private SocketServerSide server;

    private final int PORTA;

    private final String HOST;

    private final Boolean DEBUG;

    private Queue<String> comandos;

    private TextArea responses;

    public ServidorSocket(String host, int porta, boolean debug, TextArea responses) {
        this.PORTA = porta;
        this.HOST = host;
        this.DEBUG = debug;
        this.responses = responses;
        this.comandos = new LinkedList<>();
    }

    public void addCommand(String comando) {
        synchronized (this.comandos) {
            this.comandos.add(comando);
            this.comandos.notify();
        }
    }

    public void configurarUpdateConnections(ISocketConnectionsFunction atualizar_conexoes) {
        this.atualizarConexoes = atualizar_conexoes;
        if (this.server != null) {
            this.server.configurarUpdateConnections(atualizar_conexoes);
        }
    }

    public void start() {
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.TEXTO);

        if (this.atualizarConexoes != null) {
            this.server.configurarUpdateConnections(this.atualizarConexoes);
        }

        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaRequisicoes();
                if (cliente != null) {
                    String line;
                    while ((line = cliente.receberMensagem()) != null) {
                        if (DEBUG) {
                            System.out.println(
                                    "DEBUG [" + cliente.getEndereco() + ":" + cliente.getPorta() + "]: " + line);
                        }
                        if(line.contains("*")){
                            line = line.replace('*', '\n');
                        }
                        final String receivedLine = responses.getText() + "\n" + line;
                        javafx.application.Platform.runLater(() -> responses.setText(receivedLine));
                    }
                }
            }
        };

        this.metodo_enviar = () -> {
            String comando;
            String[] res;

            synchronized(this.comandos){
                while (true) {
                    if (this.comandos.isEmpty()) {
                        try {
                            System.out.println("Esperando comandos...");
                            this.comandos.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        comando = this.comandos.poll();
                        System.out.println("Comando capturado!");
                        if (comando != null) {
                            res = comando.split(";");
                            switch (res[0]) {
                                case "0": {
                                    if(res[1].equals("-1")){
                                        this.server.broadcast(res[2]);
                                    } else {
                                        System.out.println("Enviando: " + comando);
                                        this.server.unicast(Integer.parseInt(res[1]), res[2]);
                                    }
                                    break;
                                }
                                case "1": {
                                    break;
                                }
                                case "2": {
                                    break;
                                }
                                default:
                                    System.out.println("ERRO, SEM COMANDO ESPECIFICADO");
                                    break;
                            }
                        }
                    }
                }
            }
        };

        this.server.configurarMetodoEscutar(metodo_escutar);

        this.server.configurarMetodoEnviar(metodo_enviar);

        this.server.enviar();

        this.server.escutar();
    }

    public void adicionarConexao(String endereco, int porta) {
        SocketClientSide nova_conexao = new SocketClientSide(endereco, porta);
        nova_conexao.conectar();
        nova_conexao.configurarEntradaSaida(SocketType.TEXTO);
        this.server.adicionar(nova_conexao);
        if (this.atualizarConexoes != null) {
            this.server.configurarUpdateConnections(this.atualizarConexoes);
        }
    }

    public Map<Integer, SocketClientSide> listarConexoes() {
        return this.server.getConexoes();
    }

}
