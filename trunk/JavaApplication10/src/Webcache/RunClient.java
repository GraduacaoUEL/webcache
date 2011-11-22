/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Ernesto
 */
public class RunClient {

    //Atributos para conexão com Servidor Mestre
    private Thread connectionToMasterServer_Thread;
    private PrintWriter writerMasterServer;
    private Socket sockMasterServer;
    private BufferedReader readerMasterServer;
    
    //Atributos para ativa o Socket de comunicação com outros clientes
    private ServerSocket connectionClientClient_Socket;
    private Thread connectionClientClient_Thread, connectionWithClient_Thread, newClient_Thread;
    private ArrayList clientOutputStreams;
    private Socket sockWithClient;
    private BufferedReader readerWithClient;
    private PrintWriter writerWithClient;
    private ArrayList listaClientes; 

    public static void main(String[] args) {
        RunClient rc = new RunClient();
        BancoDeDados bd = new BancoDeDados();
        bd.inicializaMemoriaConexoes();
        rc.listaClientes = bd.getMemoria();
        Iterator it = rc.listaClientes.iterator();
        rc.conectarServidor(it.next().toString());
        //rc.criarSocketCliente();
        //rc.conectarCliente("127.0.0.1");

    }

    public void criarSocketCliente()
    {
        //Ativa a parte que comunicação direta com outros clientes        
        connectionClientClient_Thread = new Thread(new connectionClientClient(5556));
        connectionClientClient_Thread.start();
    }
    
    public boolean conectarServidor(String ip)
    {
        //Ativa a parte de conexão com o Servidor Mestre
        connectionToMasterServer_Thread = new Thread(new connectionToMasterServer("127.0.0.1", 5555));
        connectionToMasterServer_Thread.start();
        return true;
    }
    
    public boolean conectarCliente(String ip)
    {
        connectionWithClient_Thread = new Thread(new startConnectionWithClient(ip, 5556));
        connectionWithClient_Thread.start();
        return true;
    }
    //Realiza conexão com o Servidor Mestre
    private class connectionToMasterServer implements Runnable {

        //Construtor
        public connectionToMasterServer(String ipServer, int portServer) {
            try {
                sockMasterServer = new Socket(ipServer, portServer);
                InputStreamReader streamReader = new InputStreamReader(sockMasterServer.getInputStream());
                readerMasterServer = new BufferedReader(streamReader);
                writerMasterServer = new PrintWriter(sockMasterServer.getOutputStream());
                System.out.println("Conexão com o Servidor estabelecida...");

                try {
                    String ipText = String.valueOf(InetAddress.getLocalHost().getHostAddress());
                    ipText = ipText.replace("/", "");
                    writerMasterServer.println("IP;" + ipText);
                    writerMasterServer.flush();
                } catch (Exception ex) {
                    System.out.println("FALHA AO COMUNICAR COM O SERVIDOR");
                    ex.printStackTrace();
                }
            } catch (IOException ex) {
                System.out.println("FALHA AO TENTAR CONECTAR COM O SERVIDOR");
                ex.printStackTrace();
            }
        }

        //O que a Thread irá executar
        public void run() {
            String message;
            try {

                //Mensagem que vem do Servidor Mestre
                while ((message = readerMasterServer.readLine()) != null) {
                    System.out.println(message);
                }

                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerMasterServer.close();
                    writerMasterServer.close();
                    sockMasterServer.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println("CONEXÃO COM O SERVIDOR ENCERRADA");
                ex.printStackTrace();
                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerMasterServer.close();
                    writerMasterServer.close();
                    sockMasterServer.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } finally {
                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerMasterServer.close();
                    writerMasterServer.close();
                    sockMasterServer.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
        }
    }//Fim do connectionToMasterServer

    //Conexão entre Cliente-Cliente (Esculta)
    private class connectionClientClient implements Runnable {

        //Construtor
        public connectionClientClient(int port) {
            try {
                connectionClientClient_Socket = new ServerSocket(port);
                System.out.println("Socket de comunicação Cliente-Cliente ativado");
            } catch (IOException ioE) {
                System.out.println("FALHA AO CRIAR O CLIENTE-CLIENTE SOCKET");
                ioE.printStackTrace();
            }
        }

        public void run() {
            clientOutputStreams = new ArrayList<String>();
            try {
                Socket clientSocket = connectionClientClient_Socket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                newClient_Thread = new Thread(new ClientHandler(clientSocket));
                newClient_Thread.start();

                tellTheNeighbor("Olá vizinho!!!");
                System.out.println("Olá vizinho de novo!");

            } catch (Exception ex) {
                System.out.println("FALHA AO ESTABELECER CONEXÃO COM O CLIENTE");
                ex.printStackTrace();
            }
        }

        private class ClientHandler implements Runnable {

            BufferedReader readerClientClient;
            Socket sockClientClient;

            public ClientHandler(Socket clientSocket) {
                try {
                    sockClientClient = clientSocket;
                    InputStreamReader isReader = new InputStreamReader(sockClientClient.getInputStream());
                    readerClientClient = new BufferedReader(isReader);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void run() {
                String message;
                try {

                    //Se o cliente enviar alguma mensagem
                    while ((message = readerClientClient.readLine()) != null) {
                    }

                } catch (Exception ex) {
                    System.out.println("CONEXÃO COM O CLIENTE ENCERRADA");
                    try {
                        readerClientClient.close();
                        sockClientClient.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    ex.printStackTrace();
                } finally {
                    try {
                        readerClientClient.close();
                        sockClientClient.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

        private void tellTheNeighbor(String message) {
            Iterator it = clientOutputStreams.iterator();
            while (it.hasNext()) {
                try {
                    PrintWriter writer = (PrintWriter) it.next();
                    writer.println(message);
                    writer.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }//Fim do connectionClientClient
    
    //Conexão entre Cliente-Cliente (Procura)
    private class startConnectionWithClient implements Runnable {

        //Construtor
        public startConnectionWithClient(String ipServer, int portServer) {
            try {
                sockWithClient = new Socket(ipServer, portServer);
                InputStreamReader streamReader = new InputStreamReader(sockWithClient.getInputStream());
                readerWithClient = new BufferedReader(streamReader);
                writerWithClient = new PrintWriter(sockWithClient.getOutputStream());
                System.out.println("Conexão com o Cliente estabelecida...");

                /*try {
                    String ipText = String.valueOf(InetAddress.getLocalHost().getHostAddress());
                    ipText = ipText.replace("/", "");
                    writerWithClient.println("IP;" + ipText);
                    writerWithClient.flush();
                } catch (Exception ex) {
                    System.out.println("FALHA AO COMUNICAR COM O CLIENTE");
                    ex.printStackTrace();
                }*/
                System.out.println("Um vizinho conectou-se");
            } catch (IOException ex) {
                System.out.println("FALHA AO TENTAR CONECTAR COM O CLIENTE");
                ex.printStackTrace();
            }
        }

        //O que a Thread irá executar
        public void run() {
            String message;
            try {

                //Mensagem que vem do Servidor Mestre
                while ((message = readerWithClient.readLine()) != null) {
                    System.out.println(message);
                }

                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerWithClient.close();
                    writerWithClient.close();
                    sockWithClient.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println("CONEXÃO COM O CLIENTE ENCERRADA");
                ex.printStackTrace();
                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerWithClient.close();
                    writerWithClient.close();
                    sockWithClient.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } finally {
                try {
                    connectionToMasterServer_Thread.interrupt();
                    readerWithClient.close();
                    writerWithClient.close();
                    sockWithClient.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
        }
    }//Fim do startConnectionWithClient
}
