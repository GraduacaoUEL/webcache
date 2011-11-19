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
    private Map hashTable;
    private int countClient;

    public static void main(String[] args) {
        
        Thread sm = new Thread(new ServidorMestre(5555));
        sm.start();
    }

    public ServidorMestre(int port) {
        hashTable = new Hashtable();
        countClient = 0;
        try {
            serverSocket = new ServerSocket(port);
            
            String ipText = String.valueOf(InetAddress.getLocalHost().getHostAddress());
            ipText = ipText.replace("/", "");
            System.out.println("IP server: " + ipText);
            hashTable.put(countClient, ipText);
            countClient++;

            System.out.println("Socket do Servidor ativado");
        } catch (IOException ioE) {
            System.out.println("FALHA AO CRIAR O SERVER SOCKET");
            ioE.printStackTrace();
        }
    }

    @Override
    public void run() {
        clientOutputStreams = new ArrayList<String>();
        try {
            Socket clientSocket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            clientOutputStreams.add(writer);
            t = new Thread(new ClientHandler(clientSocket));
            t.start();
            
            tellEveryone("Atualizando Lista de Clientes");

        } catch (Exception ex) {
            System.out.println("FALHA AO ESTABELECER CONEXÃO COM O CLIENTE");
            ex.printStackTrace();
        }
    }

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
                while ((message = reader.readLine()) != null) {
                    mensagem = message.split(";");
                    
                    if(mensagem[0].equals("IP")) {
                        System.out.println("Mensagem do Cliente " + countClient + ": " + mensagem[1]);
                        hashTable.put(countClient, mensagem[1]);
                        System.out.println("Tabela HASH: " + hashTable);                        
                    }
                }
            } catch (Exception ex) {
                System.out.println("CONEXÃO COM O CLIENTE ENCERRADA");
                ex.printStackTrace();
            } finally {
            }
        }
    }

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