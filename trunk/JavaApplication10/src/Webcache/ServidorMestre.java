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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Ernesto
 */
public class ServidorMestre implements Runnable {

    private ArrayList clientOutputStreams;
    private ServerSocket serverSocket;
    private Thread t;
    //private Map hashTable;
    private ArrayList listIP;
    private int countClient;

    public static void main(String[] args) {

        Thread sm = new Thread(new ServidorMestre(5555));
        sm.start();
    }

    //Construtor - Ativa o Socket para a comunicação com os clientes
    public ServidorMestre(int port) {
        //hashTable = new Hashtable();
        listIP = new ArrayList();
        countClient = 0;
        try {
            serverSocket = new ServerSocket(port);

            //IP do Servidor Mestre
            String ipText = String.valueOf(InetAddress.getLocalHost().getHostAddress());
            ipText = ipText.replace("/", "");
            System.out.println("IP server: " + ipText);
            //hashTable.put(countClient, ipText);
            listIP.add(ipText);
            countClient++;

            System.out.println("Socket do Servidor ativado");
        } catch (IOException ioE) {
            System.out.println("FALHA AO CRIAR O SERVER SOCKET");
            ioE.printStackTrace();
        }
    }

    //Estabelece conexão a cada nova requisição
    @Override
    public void run() {
        clientOutputStreams = new ArrayList<String>();
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                t = new Thread(new ClientHandler(clientSocket));
                t.start();

                tellEveryone("Atualizando Lista de Clientes");
            }

        } catch (Exception ex) {
            System.out.println("FALHA AO ESTABELECER CONEXÃO COM O CLIENTE");
            ex.printStackTrace();
        }
    }

    //Mantém escuta com cada cliente
    public class ClientHandler implements Runnable {

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

        @Override
        public void run() {
            String[] mensagem = new String[20];
            String message;
            try {

                //Se o cliente enviar alguma mensagem
                while ((message = reader.readLine()) != null) {
                    mensagem = message.split(";");

                    if (mensagem[0].equals("IP")) {
                        System.out.println("Mensagem do Cliente " + countClient + ": " + mensagem[1]);
                        //hashTable.put(countClient, mensagem[1]);
                        listIP.add(mensagem[1]);
                        countClient++;
                        //System.out.println("Tabela HASH: " + hashTable);
                        System.out.println("Lista de IP's: " + listIP);
                    }
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

    //Envia mensagem para todos os clientes conectados ao Servidor Mestre
    private void tellEveryone(String message) {
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
}
