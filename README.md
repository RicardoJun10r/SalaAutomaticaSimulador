# SalaAutomaticaSimulador
Uma simulação de salas automáticas, com um servidor conectando-se aos microcontroladores das salas, assim automatizando processos de ligar e desligar aparelhos.

## Projeto
Com a descrição acima, o projeto é divido em quatro classes:
- SalaAula:
Está localizado a sala, com seus aparelhos, nas quais são computadores e ar-condicionados;
- Client:
Está a implementação do microcontrolador, que irá controlar os aparelhos das salas;
- Server:
Está presente o servidor;
- Util:
Está presente uma classe auxiliar, para ajudar a criação dos Sockets do lado do cliente.

### SalaAula:

![Interface de Aparelhos](./Gerenciador/src/assets/Captura%20de%20tela%20de%202023-12-24%2018-30-09.png)

Cada aparelho implementa essas funcionalidades, para ligar, desligar e descreveer cada aparelhos contido na sala.
Os aparelhos:

![Computador](./Gerenciador/src/assets/computador.png)
![Ar-condicionado](./Gerenciador/src/assets/ar-condicionado.png)

A sala:

![Sala](./Gerenciador/src/assets/sala.png)

A sala contém uma lista para armazenar todos os aparelhos contido nela e assim poder fazer as funcionalidades pedidias.
O número de aparelhos é dito no construtor da classe.

### Client:
#### A classe Principal é o microcontrolador, na qual irei descrever abaixo:

![Microcontrolador](./Gerenciador/src/assets/microcontrolador.png)

Na construção desta classe, deve-se passar uma sala de aula, para ele ter acesso a todos os aparelhos contida nela.

As principais partes são:
1. Implementação da interface Runnable, para utilização do seu método "run", na qual permite criar uma Thread separada para "escutar" mensagens enviadas do servidor.
2. 
![Microcontrolador método run](./Gerenciador/src/assets/microcontrolador-run.png)

4. Na função "run" é tratado o pedido do servidor, na qual deve ser um número e então é executado o que se pede, como mostra a função abaixo:
5. 
![Microcontrolador função](./Gerenciador/src/assets/microcontrolador-funcao.png)

7. Por final é enviado a mensagem de volta ao servidor.
8. 
![Microcontrolador Response](./Gerenciador/src/assets/microcontrolador-sendMessage.png)

10. Método start, ele inicializa o Socket do cliente, utilizando a classe utilitária, por fim inicializa uma Thread para executar o método "run".
11. 
![Microcontrolador start](./Gerenciador/src/assets/microcontrolador-start.png)


#### A classe Main:

É possível inicializar de duas formas, uma classe inicializando várias instancias de microcontroladores:
![Microcontrolador Main](./Gerenciador/src/assets/microcontrolador-main.png)
Também é possível inicializar em classes separadas:
![Microcontrolador Main 2](./Gerenciador/src/assets/microcontrolador-main-2.png)

### Server:
#### A classe principal é Server.

As principais partes são:
1. Método start, ele irá subir o SocketServer para receber conexões e executar a função principal.
2. 
![Server start](./Gerenciador/src/assets/server-start.png)

4. Método principal, uma função em loop infinito, é dividio em três partes:
- Receber conexões;

![Server conexões](./Gerenciador/src/assets/server-connections.png)

- Thread separada para receber as respostas dos microcontroladores.
  
![Server mensagens dos clientes](./Gerenciador/src/assets/server-cliente-mensagem.png)

- Thread separada para enviar mensagens aos microcontroladores.
  
![Server enviar mensagens](./Gerenciador/src/assets/server-enviar-mensagens.png)

3. Método receber respostas dos microcontroladores.
   
![Server receber mensagens](./Gerenciador/src/assets/server-receber-mensagem.png)

5. Método enviar mensagens, este método é envolto da função "synchronized()", por fim ele chama a função de envio.
   
![Server enviar mensagem](./Gerenciador/src/assets/server-enviar-mensagem.png)

Função de envio:

![Server função de envio](./Gerenciador/src/assets/server-funao-enviar.png)

### Util:
#### A classe principal é o ClientSocket.

Esta classe vai ser utilizada peloas microcontroladores, ela vai armazenar o Socket do cliente, assim como os métodos de leitura e escrita.

![Classe ClientSocket](./Gerenciador/src/assets/client-socket.png)
