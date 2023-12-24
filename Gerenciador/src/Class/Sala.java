package Class;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Aparelhos.ArCondicionado;
import Aparelhos.Computador;
import Aparelhos.Interface.InterAparelho;
// import Client.Microcontrolador;

public class Sala {

    private List<InterAparelho> aparelhos;

    // private Microcontrolador microcontrolador;

    private final int NUMERO_APARELHOS;

    public Sala(int numero_aparelhos){
        this.aparelhos = new ArrayList<>();
        this.NUMERO_APARELHOS = numero_aparelhos;
    }

    public void encherSala(){
        for(int i = 0; i < NUMERO_APARELHOS; i++){
            this.aparelhos.add(new Computador(i));
            this.aparelhos.add(new ArCondicionado(i));
        }
    }

    public String desligarAparelhos(){
        for(InterAparelho i : aparelhos)
            i.Desligar();
        return "Aparelhos desligados!";
    }

    public String ligarAparelhos(){
        for(InterAparelho i : aparelhos)
            i.Ligar();
        return "Aparelhos ligados!";
    }

    public String mostrarAparelhos(){
        String mensagem = "";
        mensagem += "Sala com [ " + (NUMERO_APARELHOS*2) + " ] aparelhos\n";
        Iterator<InterAparelho> iterator = this.aparelhos.iterator();
        while (iterator.hasNext()) {
            InterAparelho i = iterator.next();
            mensagem += i.Descricao();
        }
        return mensagem;
    }

}
