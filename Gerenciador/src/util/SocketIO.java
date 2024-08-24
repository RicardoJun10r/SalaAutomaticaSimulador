package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.io.InputStreamReader;

public class SocketIO {

    private BufferedReader leitor_texto;

    private PrintWriter escritor_texto;

    private ObjectOutputStream leitor_objeto;

    private ObjectInputStream escritor_objeto;

    public SocketIO(){}

    public boolean enviar(String mensagem){
        this.escritor_texto.println(mensagem);
        return !this.escritor_texto.checkError();
    }

    public String receber(){
        try {
            return this.leitor_texto.readLine();
        } catch (IOException e) {
            return "\nERRO: " + e.getMessage();
        }
    }

    public void configurarTexto(Socket socket){
        try {
            this.leitor_texto = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.escritor_texto = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void configurarObjeto(Socket socket){
        try {
            this.leitor_objeto = new ObjectOutputStream(socket.getOutputStream());
            this.escritor_objeto = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getLeitor_texto() {
        return leitor_texto;
    }

    public PrintWriter getEscritor_texto() {
        return escritor_texto;
    }

    public ObjectOutputStream getLeitor_objeto() {
        return leitor_objeto;
    }

    public ObjectInputStream getEscritor_objeto() {
        return escritor_objeto;
    }
    
}
