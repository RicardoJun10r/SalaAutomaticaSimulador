package simulador.server;

public class Conexao {
    private Integer id;
    private String endereco;
    private int porta;

    public Conexao(Integer id, String endereco, int porta) {
        this.id = id;
        this.endereco = endereco;
        this.porta = porta;
    }

    public Integer getId() {
        return id;
    }

    public String getEndereco() {
        return endereco;
    }

    public int getPorta() {
        return porta;
    }
}
