package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class MessageV2 {
    
    private final int BUFFER_SIZE = 1024;
    
    private DatagramSocket socket;

    private int PORTA_PROPRIA;

    private int PORTA_DESTINO;

    private InetAddress address_proprio;

    private InetAddress address_destino;

    private byte[] buffer_entrada;

    private byte[] buffer_saida;

    private MessageEnum messageEnum;

    public MessageV2(int PORTA_PROPRIA, int PORTA_DESTINO, String address_proprio, String address_destino, MessageEnum messageEnum) throws SocketException, UnknownHostException{
        this.PORTA_PROPRIA = PORTA_PROPRIA;
        this.PORTA_DESTINO = PORTA_DESTINO;
        this.socket = new DatagramSocket(PORTA_PROPRIA);
        this.address_proprio = InetAddress.getByName(address_proprio);
        this.address_destino = InetAddress.getByName(address_destino);
        this.buffer_saida = new byte[this.BUFFER_SIZE];
        this.messageEnum = messageEnum;
    }

    public void enviar(){
        DatagramPacket packet = new DatagramPacket(this.buffer_entrada, this.buffer_entrada.length, this.address_destino, this.PORTA_DESTINO);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receber(){
        DatagramPacket packet = new DatagramPacket(this.buffer_saida, this.buffer_saida.length);
        try {
            this.socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Erro: Cheque o buffer !";
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public int getPORTA_PROPRIA() {
        return PORTA_PROPRIA;
    }

    public void setPORTA_PROPRIA(int pORTA_PROPRIA) {
        this.PORTA_PROPRIA = pORTA_PROPRIA;
    }

    public int getPORTA_DESTINO() {
        return PORTA_DESTINO;
    }

    public void setPORTA_DESTINO(int pORTA_DESTINO) {
        this.PORTA_DESTINO = pORTA_DESTINO;
    }

    public byte[] getBuffer_entrada() {
        return buffer_entrada;
    }

    public void setBuffer_entrada(byte[] buffer_entrada) {
        this.buffer_entrada = buffer_entrada;
    }

    public byte[] getBuffer_saida() {
        return buffer_saida;
    }

    public void setBuffer_saida(byte[] buffer_saida) {
        this.buffer_saida = buffer_saida;
    }

    public InetAddress getAddress_proprio() {
        return address_proprio;
    }

    public void setAddress_proprio(InetAddress address_proprio) {
        this.address_proprio = address_proprio;
    }

    public InetAddress getAddress_destino() {
        return address_destino;
    }

    public void setAddress_destino(InetAddress address_destino) {
        this.address_destino = address_destino;
    }

    public MessageEnum getMessageEnum() {
        return messageEnum;
    }

}
