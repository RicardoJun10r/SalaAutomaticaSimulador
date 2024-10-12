package simulador_v2.server;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import simulador_v2.Main.Appliance;
import util.ServerReq;
import util.api.SocketClientSide;
import util.api.SocketServerSide;
import util.api.SocketType;
import util.api.Interface.ISocketConnectionsFunction;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.function.Consumer;

public class Servidor {
    private ISocketListenFunction metodo_escutar;
    private ISocketWriteFunction metodo_enviar;
    private ISocketConnectionsFunction atualizarConexoes;
    private SocketServerSide server;
    private final int PORTA;
    private final String HOST;
    private final Boolean DEBUG;
    private BlockingQueue<ServerReq> comandos;
    private TextArea responses;

    private Consumer<Appliance> atualizarTabelaDispositivos;

    public Servidor(String host, int porta, boolean debug, TextArea responses) {
        this.PORTA = porta;
        this.HOST = host;
        this.DEBUG = debug;
        this.responses = responses;
        this.comandos = new LinkedBlockingQueue<>();
    }

    public void addCommand(ServerReq comando) {
        this.comandos.add(comando);
    }

    public void configurarUpdateConnections(ISocketConnectionsFunction atualizar_conexoes) {
        this.atualizarConexoes = atualizar_conexoes;
        if (this.server != null) {
            this.server.configurarUpdateConnections(atualizar_conexoes);
        }
    }

    public void configurarAtualizarTabelaDispositivos(Consumer<Appliance> atualizarTabelaDispositivos) {
        this.atualizarTabelaDispositivos = atualizarTabelaDispositivos;
    }

    public void start() {
        this.server = new SocketServerSide(this.HOST, this.PORTA, SocketType.OBJETO);

        if (this.atualizarConexoes != null) {
            this.server.configurarUpdateConnections(this.atualizarConexoes);
        }

        this.server.iniciar();

        this.metodo_escutar = () -> {
            while (true) {
                SocketClientSide cliente = this.server.filaClientes();
                if (cliente != null) {
                    ServerReq line;
                    while ((line = (ServerReq) cliente.receberObjeto()) != null) {
                        if (DEBUG) {
                            System.out.println(
                                    "DEBUG [" + cliente.getEndereco() + ":" + cliente.getPorta() + "]: " + line);
                        }
                        final String receivedLine = responses.getText() + "\n" + line.getMensagem();
                        Platform.runLater(() -> responses.setText(receivedLine));

                        if (line.getOpcao() == 2) { // 2 representa a operação "Descrever"
                            String resposta = line.getMensagem();

                            processarDescricaoSala(line.getMicrocontrolador_id(), resposta);
                        }
                    }
                }
            }
        };

        this.metodo_enviar = () -> {
            ServerReq comando;
            while (true) {
                try {
                    comando = this.comandos.take();
                    System.out.println("Comando capturado!");
                    if (comando != null) {
                        if (comando.getMensagem().equals("unicast")) {
                            if (this.server.getConexoes().containsKey(comando.getMicrocontrolador_id())) {
                                this.server.unicast(comando.getMicrocontrolador_id(), comando);
                            } else {
                                System.out.println("ID não encontrado nas conexões ativas.");
                            }
                        } else {
                            this.server.broadcast(comando);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        this.server.configurarMetodoEscutar(metodo_escutar);
        this.server.configurarMetodoEnviar(metodo_enviar);

        this.server.enviar();
        this.server.escutar();
    }

    private void processarDescricaoSala(int idSala, String resposta) {
        int index = resposta.indexOf("Sala com");
        if (index != -1) {
            resposta = resposta.substring(index);
        }

        int totalLigados = 0;
        int totalDesligados = 0;

        String[] linhas = resposta.split("\\*");
        for (String linha : linhas) {
            linha = linha.trim();
            if (!linha.isEmpty()) {
                if (linha.startsWith("Ligados:")) {
                    totalLigados = Integer.parseInt(linha.substring(8).trim());
                } else if (linha.startsWith("Desligados:")) {
                    totalDesligados = Integer.parseInt(linha.substring(11).trim());
                }
            }
        }

        Appliance appliance = new Appliance(idSala, totalLigados, totalDesligados);

        if (atualizarTabelaDispositivos != null) {
            Platform.runLater(() -> atualizarTabelaDispositivos.accept(appliance));
        }
    }

    public Map<Integer, SocketClientSide> listarConexoes() {
        return this.server.getConexoes();
    }
}
