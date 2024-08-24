package util;

import java.net.Socket;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SocketClientSide extends IMySocket {

    private Socket socket;

    private final int TIME_OUT = 5*1000;

    public SocketClientSide(String endereco, int porta) {
        super(endereco, porta);
    }

    public void connect(){
        System.out.println("TENTANDO CONEXÃO NO IP: [" + super.getEndereco() + ":" + super.getPorta() + "]...");
        try {
            this.socket.connect(new InetSocketAddress(super.getEndereco(), super.getPorta()), TIME_OUT);
            System.out.println("CONEXÃO ESTABELECIDA!");
        } catch (IOException e) {
            System.out.println("ERRO NA CONEXÃO: " + e.getMessage());
        }
    }
    
}
