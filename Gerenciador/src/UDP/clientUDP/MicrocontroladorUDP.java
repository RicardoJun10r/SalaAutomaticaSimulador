package UDP.clientUDP;

import java.net.SocketException;
import java.net.UnknownHostException;

import salaAula.Sala;
import util.MessageUDP;

public class MicrocontroladorUDP {
    
    private MessageUDP messageUDP;

    private int ID;

    private final Sala SALA;

    public MicrocontroladorUDP(int PORTA_PROPRIA, int PORTA_DESTINO, String address, int ID, Sala sala) throws SocketException, UnknownHostException{
        this.messageUDP = new MessageUDP(PORTA_PROPRIA, PORTA_DESTINO, address);
        this.ID = ID;
        this.SALA = sala;
    }
    
    public Sala getSALA() {
        return SALA;
    }

    private void clientLoop(){
        while (true) {
            StringBuffer stringBuffer = new StringBuffer();
            String response = this.messageUDP.receber();
            if( (Integer.parseInt(response.split(" ")[0]) != ID) ){
                System.out.println("ENCAMINHANDO: " + ID);
                stringBuffer.append(response).append("|").append(this.messageUDP.getPORTA_PROPRIA());
                this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                this.messageUDP.enviar();
            } else {
                String [] res = response.split(" ");
                stringBuffer.append(-1).append(" ");
                System.out.println("PROCESSANDO: " + ID);
                switch(Integer.parseInt(res[1])){
                    case 0:
                    {
                        stringBuffer.append(this.SALA.desligarAparelhos()).append(":").append(ID).append(" ").append(res[2]).append("|").append(this.messageUDP.getPORTA_PROPRIA());
                        this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                        this.messageUDP.enviar();
                        break;
                    }
                    case 1:
                    {
                        stringBuffer.append(this.SALA.ligarAparelhos()).append(":").append(ID).append(" ").append(res[2]).append("|").append(this.messageUDP.getPORTA_PROPRIA());
                        this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                        this.messageUDP.enviar();
                        break;
                    }
                    case 2:
                    {
                        stringBuffer.append(this.SALA.mostrarAparelhos()).append(":").append(ID).append(" ").append(res[2]).append("|").append(this.messageUDP.getPORTA_PROPRIA());
                        this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                        this.messageUDP.enviar();
                        break;
                    }
                    default:
                    {
                        stringBuffer.append("OPÇÃO INVÁLIDA:").append(ID).append(" ").append(res[2]).append("|").append(this.messageUDP.getPORTA_PROPRIA());
                        this.messageUDP.setBuffer_entrada( stringBuffer.toString().getBytes() );
                        this.messageUDP.enviar();
                        break;
                    }
                }
            }
        }
    }

    public void start(){
        try{
            System.out.println("CLIENTE INICIADO NA PORTA: " + this.messageUDP.getPORTA_PROPRIA());
            clientLoop();
        } finally {
            this.messageUDP.getSocket().close();
        }
    }
}
