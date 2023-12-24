package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import util.ClientSocket;

public class MicrocontroladorServer {
    
    public final static int PORTA = 1025;

    private ServerSocket serverSocket;

    private final Set<ClientSocket> USUARIOS = Collections.synchronizedSet(new HashSet<>());

    private final Scanner scan;

    private final Object lock = new Object();

    public MicrocontroladorServer(){
        this.scan = new Scanner(System.in);
    }

    public void start() throws IOException{
        serverSocket = new ServerSocket(PORTA);
        System.out.println("Iniciando servidor na porta " + PORTA);
        clientConnectionLoop();
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(this.serverSocket.accept());
            USUARIOS.add(clientSocket);
            new Thread(() -> {
                try {
                    clientMessageLoop(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    sendOrder();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void menu(){
        Iterator<ClientSocket> iterator = this.USUARIOS.iterator();
        while (iterator.hasNext()){
            ClientSocket i = iterator.next();
            System.out.println(
                i.getId() + " --> " + i.getSocketAddress()
            );
        }
        System.out.println(
            "------OPÇÕES------\n[0] --> DESLIGAR TUDO\n[1] --> LIGAR TUDO\n[2] --> DESCRIÇÃO\nOpção e porta( ex: 9 8080 ):"
        );
    }

    private void clientMessageLoop(ClientSocket clientSocket) throws IOException, InterruptedException{
        String mensagem;
        try {
            while ((mensagem = clientSocket.getMessage()) != null) {
                System.out.println(
                    "Mensagem de "  + clientSocket.getSocketAddress() + ": " + mensagem
                );
            }
        } finally {
            clientSocket.close();
        }
    }
    
    private void sendOrder() throws IOException, InterruptedException {
        synchronized(lock){
            String mensagem;
            String destinatario;
            while(true){
                Thread.sleep(300);
                menu();
    
                String input = this.scan.nextLine();
                String[] parts = input.split(" ");
    
                // System.out.println("ENTRDAS = " + parts.length);
    
                // System.out.println(Arrays.toString(parts));
    
                if (parts.length >= 2) {
                    mensagem = parts[0];
                    destinatario = parts[1];
                    if(validarEntrada(mensagem, destinatario)){
                    String endereco = "/127.0.0.1:".concat(destinatario);
                    // System.out.println(
                    //     "SERVIDOR:\nMensagem: " + mensagem + "\nDestino: " + destinatario
                    // );
                    sendMessageTo(endereco, mensagem);
                    } else {
                        System.out.println(
                            "ERRO: Entradas incorretas!"
                        );
                    }
                } else {
                    System.out.println("Erro!");
                }
            }
        }
    }

    private boolean validarEntrada(String mensagem, String destinatario){
        boolean flag = true;
        if(mensagem.isEmpty() || destinatario.isEmpty()){
            flag = false;
        }
        return flag;
    }

    private void sendMessageTo(String destinataio, String mensagem){
        Iterator<ClientSocket> iterator = this.USUARIOS.iterator();
        while (iterator.hasNext()){
            ClientSocket i = iterator.next();
            if(i.getSocketAddress().toString().equals(destinataio)){
                if(!i.sendMessage(mensagem))
                    iterator.remove();
            }
        }
    }

}
