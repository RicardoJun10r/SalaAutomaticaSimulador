package salaAula.Aparelhos;

import salaAula.Aparelhos.Interface.InterAparelho;

public class ArCondicionado implements InterAparelho {

    private boolean status;

    private final int ID;
    
    public ArCondicionado(int id){
        this.ID = id;
        this.status = false;
    }
    
    public boolean isStatus() {
        return status;
    }

    public int getID() {
        return ID;
    }
    
    @Override
    public void Ligar() {
        this.status = true;
    }

    @Override
    public void Desligar() {
        this.status = false;
    }

    @Override
    public String Descricao() {
        return "Ar-condicionado de ID = [ " + ID + " ] | Status = [ " + this.status + " ]*";
    }
    
}
