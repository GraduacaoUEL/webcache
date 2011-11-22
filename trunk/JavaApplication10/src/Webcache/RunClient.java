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
    private PrintWriter writer;
    private Socket sock;
    private BufferedReader reader;
    
    //Atributos para ativa o Socket de comunicação com outros clientes
    private ServerSocket connectionClientClient_Socket;
    private Thread connectionClientClient_Thread, newClient;
    private ArrayList clientOutputStreams;

    public static void main(String[] args) {
        RunClient rc = new RunClient();
    }

    public RunClient() {
        //Ativa a parte de conexão com o Servidor Mestre
        connectionToMasterServer_Thread = new Thread(new connectionToMasterServer("127.0.0.1", 5555));
        connectionToMasterServer_Thread.start();

        //Ativa a parte que comunicação direta com outros clientes        
        connectionClientClient_Thread = new Thread(new connectionClientClient(5556));
        connectionClientClient_Thread.start();
        //connectionClientClient_Thread = new Thread(new connectionToMasterServer("127.0.0.1", 5556));
        //connectionClientClient_Thread.start();
    }

    //Realiza conexão com o Servidor Mestre
    private class connectionToMasterServer implements Runnable {

        //Construtor
        public connectionToMasterServer(String ipServer, int portServer) {
            try {
                sock = new Socket(ipServer, portServer);
                InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(streamReader);
                writer = new PrintWriter(sock.getOutputStream());
                System.out.println("Conexão com o Servidor estabelecida...");

                try {
                    String ipText = String.valueOf(InetAddress.getLocalHost().getHostAddress());
                    ipText = ipText.replace("/", "");
                    writer.println("IP;" + ipText);
                    writer.flush();
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
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }

                try {
                    connectionToMasterServer_Thread.interrupt();
                    reader.close();
                    writer.close();
                    sock.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println("CONEXÃO COM O SERVIDOR ENCERRADA");
                ex.printStackTrace();
                try {
                    connectionToMasterServer_Thread.interrupt();
                    reader.close();
                    writer.close();
                    sock.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } finally {
                try {
                    connectionToMasterServer_Thread.interrupt();
                    reader.close();
                    writer.close();
                    sock.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
        }
    }//Fim do connectionToMasterServer

    //Conexão entre Cliente-Cliente
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
                newClient = new Thread(new ClientHandler(clientSocket));
                newClient.start();

                tellTheNeighbor("Olá vizinho!");

            } catch (Exception ex) {
                System.out.println("FALHA AO ESTABELECER CONEXÃO COM O CLIENTE");
                ex.printStackTrace();
            }
        }

        private class ClientHandler implements Runnable {

            BufferedReader reader;
            Socket sock;

            public ClientHandler(Socket clientSocket) {
                try {
                    sock = clientSocket;
                    InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                    reader = new BufferedReader(isReader);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void run() {
                String message;
                try {

                    //Se o cliente enviar alguma mensagem
                    while ((message = reader.readLine()) != null) {
                    }

                } catch (Exception ex) {
                    System.out.println("CONEXÃO COM O CLIENTE ENCERRADA");
                    try {
                        reader.close();
                        sock.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    ex.printStackTrace();
                } finally {
                    try {
                        reader.close();
                        sock.close();
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
}
