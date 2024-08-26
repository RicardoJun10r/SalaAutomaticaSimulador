package util.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import util.api.Interface.IMySocket;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class SocketServerSide extends IMySocket {

    private ServerSocket server;

    private Map<Integer, SocketClientSide> conexoes;

    private Queue<SocketClientSide> fila_escuta;

    // private Queue<SocketClientSide> fila_envio;

    private Integer contador_interno;

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private ExecutorService executorService;

    public SocketServerSide(String endereco, int porta) {
        super(endereco, porta);
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.conexoes = Collections.synchronizedMap(new HashMap<>());
        this.fila_escuta = new ConcurrentLinkedQueue<>();
        this.contador_interno = 0;
    }

    public Queue<SocketClientSide> Fila(){ return this.fila_escuta; }

    public void iniciar(){
        try {
            this.server = new ServerSocket(super.getPorta(), 50, InetAddress.getByName(super.getEndereco()));
            System.out.println("INICIADO SERVIDOR NO IP: [" + super.getEndereco() + ":" + super.getPorta() + "]...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configurarMetodoEscutar(ISocketListenFunction metodo_escutar){ this.metodo_escutar = metodo_escutar; }

    public void configurarMetodoEnviar(ISocketWriteFunction metodo_enviar){ this.metodo_enviar = metodo_enviar; }

    public void enviar(){
        if(this.metodo_enviar != null){
            this.executorService.submit(
                () -> {
                    while (true) {
                        this.metodo_enviar.enviar();
                    }
                }
            );
        } else {
            System.out.println("ERRO: MÉTODO DO TIPO [ISocketWriteFunction] NÃO CONFIGURADO!");
        }
    }

    public void escutar(){
        if(this.metodo_escutar != null){
            System.out.println("ESCUTANDO...");
            new Thread(
                () -> {
                    while (true) {
                        try {
                            SocketClientSide socketClientSide = new SocketClientSide(this.server.accept());
                            adicionarConexao(socketClientSide);
                            this.metodo_escutar.escutar();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            ).start();
            // this.executorService.submit(
            //     () -> {
            //         while (true) {
            //             try {
            //                 SocketClientSide socketClientSide = new SocketClientSide(this.server.accept());
            //                 adicionarConexao(socketClientSide);
            //                 this.metodo_escutar.escutar();
            //             } catch (IOException e) {
            //                 e.printStackTrace();
            //             }
            //         }
            //     }
            // );
        } else {
            System.out.println("ERRO: MÉTODO DO TIPO [ISocketListenFunction] NÃO CONFIGURADO!");
        }
    }

    public void fechar(){
        try {
            this.executorService.close();
            this.server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adicionarConexao(SocketClientSide nova_conexao){
        nova_conexao.configurarEntradaSaida(SocketType.TEXTO);
        this.conexoes.put(contador_interno, nova_conexao);
        this.fila_escuta.add(nova_conexao);
        this.contador_interno++;
    }

    public void unicast(Integer id, String msg){
        this.conexoes.get(id).enviarMensagem(msg);
    }

    public void multicast(Integer[] id, String msg){
        for(int i = 0; i < id.length; i++)
            if(this.conexoes.get(id[i]) != null) this.conexoes.get(id[i]).enviarMensagem(msg);
    }

    public void broadcast(String msg){
        this.conexoes.forEach(
            (id, conexao) -> conexao.enviarMensagem(msg)
        );
    }

    public void listarConexoes(){
        this.conexoes.forEach(
            (id, conexao) -> System.out.println(id + " --> [" + conexao.getEndereco() + ":" + conexao.getPorta() + "]")
        );
    } 

}