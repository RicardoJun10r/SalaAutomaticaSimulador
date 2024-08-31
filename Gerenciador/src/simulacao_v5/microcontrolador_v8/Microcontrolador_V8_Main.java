package simulacao_v5.microcontrolador_v8;

public class Microcontrolador_V8_Main {

    public static void main(String[] args) {
        Microcontrolador_V8 microcontrolador_V8 = new Microcontrolador_V8("127.0.0.1", 5000);

        microcontrolador_V8.start();
    }
    
}
