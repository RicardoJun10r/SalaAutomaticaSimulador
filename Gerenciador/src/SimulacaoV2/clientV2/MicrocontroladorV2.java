package SimulacaoV2.clientV2;

import java.net.SocketException;
import java.net.UnknownHostException;

import salaAula.Sala;
import util.MessageV2;

public class MicrocontroladorV2 {

    private String ID;

    private MessageV2 socket;

    private final Sala sala;

    public Sala getSala(){ return sala; }

    public MicrocontroladorV2(Sala sala, int PORTA_PROPRIA, int PORTA_DESTINO, String address_proprio,
            String address_destino, String ID) {
        this.sala = sala;
        this.ID = ID;
        try {
            this.socket = new MessageV2(PORTA_PROPRIA, PORTA_DESTINO, address_proprio, address_destino, false);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void clientLoop() {
        ping();
        while (true) {
            StringBuffer stringBuffer = new StringBuffer();
            String[] response = this.socket.receber().split(" ");
            switch (response[0]) {
                case "ligar": {
                    stringBuffer.append("response").append(" ");
                    stringBuffer.append(this.sala.ligarAparelhos()).append(" ").append(ID);
                    enviar(stringBuffer.toString(), this.socket.getAddress_destino().getHostAddress(),
                            this.socket.getPORTA_DESTINO());
                    break;
                }
                case "desligar": {
                    stringBuffer.append("response").append(" ");
                    stringBuffer.append(this.sala.desligarAparelhos()).append(" ").append(ID);
                    enviar(stringBuffer.toString(), this.socket.getAddress_destino().getHostAddress(),
                            this.socket.getPORTA_DESTINO());
                    break;
                }
                case "mostrar": {
                    stringBuffer.append("response").append(" ");
                    stringBuffer.append(this.sala.mostrarAparelhos()).append(" ").append(ID);
                    enviar(stringBuffer.toString(), this.socket.getAddress_destino().getHostAddress(),
                            this.socket.getPORTA_DESTINO());
                    break;
                }
                default: {
                    stringBuffer.append("response").append(" ");
                    stringBuffer.append("COMANDO NÃO CONHECIDO!");
                    enviar(stringBuffer.toString(), this.socket.getAddress_destino().getHostAddress(),
                            this.socket.getPORTA_DESTINO());
                    break;
                }
            }
        }
    }

    private void ping() {
        String res = "ping " + this.socket.getIsServer() + " " + socket.getAddress_proprio() + " "
                + socket.getPORTA_PROPRIA() + " " + ID;
        enviar(res,
                this.socket.getAddress_destino().getHostAddress(),
                this.socket.getPORTA_DESTINO());
    }

    private void enviar(String msg, String host, int port) {
        this.socket.setBuffer_entrada(
                msg.getBytes());
        this.socket.setAddress_destino(
                host);
        this.socket.setPORTA_DESTINO(
                port);
        this.socket.enviar();
    }

    public void start() {
        try {
            System.out.println("CLIENTE INICIADO NA PORTA: " + this.socket.getPORTA_PROPRIA());
            clientLoop();
        } finally {
            this.socket.getSocket().close();
        }
    }

}
