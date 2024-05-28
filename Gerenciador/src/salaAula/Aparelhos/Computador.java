package salaAula.Aparelhos;

import salaAula.Aparelhos.Interface.InterAparelho;

public class Computador implements InterAparelho {
    
    private boolean status;

    private final int ID;

    public Computador(int id){
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
        return "Computador de ID = [ " + ID + " ] | Status = [ " + this.status + " ]*";
    }
    
}
