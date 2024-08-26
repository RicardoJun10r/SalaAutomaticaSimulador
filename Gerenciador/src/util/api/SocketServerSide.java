package util.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import util.api.Interface.IMySocket;
import util.api.Interface.ISocketListenFunction;
import util.api.Interface.ISocketWriteFunction;

public class SocketServerSide extends IMySocket {

    private ServerSocket server;

    private Map<Integer, SocketClientSide> conexoes;

    private Integer contador_interno;

    private ISocketListenFunction metodo_escutar;

    private ISocketWriteFunction metodo_enviar;

    private ExecutorService executorService;

    public SocketServerSide(String endereco, int porta) {
        super(endereco, porta);
    }

    public void iniciar(){
        try {
            this.server = new ServerSocket(super.getPorta(), 50, InetAddress.getByName(super.getEndereco()));
            System.out.println("INICIADO SERVIDOR NO IP: [" + super.getEndereco() + ":" + super.getPorta() + "]...");
            this.conexoes = Collections.synchronizedMap(new HashMap<>());
            this.contador_interno = 0;
            this.escutar();
            this.enviar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configurarMetodoEscutar(ISocketListenFunction metodo_escutar){ this.metodo_escutar = metodo_escutar; }

    public void configurarMetodoEnviar(ISocketWriteFunction metodo_enviar){ this.metodo_enviar = metodo_enviar; }

    private void enviar(){
        if(this.metodo_enviar != null){
            System.out.println("ESCUTANDO...");
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

    private void escutar(){
        if(this.metodo_escutar != null){
            this.executorService.submit(
                () -> {
                    while (true) {
                        SocketClientSide socketClientSide = new SocketClientSide(this.server.accept());
                        adicionarConexao(socketClientSide);
                        this.metodo_escutar.escutar();
                    }
                }
            );
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
        nova_conexao.configurarEntradaSaida(3);
        this.conexoes.put(contador_interno, nova_conexao);
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
