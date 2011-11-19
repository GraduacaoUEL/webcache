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
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Ernesto
 */
public class RunClient {

    private Thread readerThread;
    private PrintWriter writer;
    private Socket sock;
    private BufferedReader reader;

    public static void main(String[] args) {
        RunClient rc = new RunClient();
    }

    public RunClient() {
        setUpNetworking("127.0.0.1", 5555);
        readerThread = new Thread(new IncomingReader());
        readerThread.start();
    }

    private void setUpNetworking(String ipServer, int portServer) {
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
    }//Fim setUpNetworking

    public class IncomingReader implements Runnable {

        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
                try {
                    readerThread.interrupt();
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
                    readerThread.interrupt();
                    reader.close();
                    writer.close();
                    sock.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } finally {
                try {
                    readerThread.interrupt();
                    reader.close();
                    writer.close();
                    sock.close();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            }
        }
    }//Fim do IncomingReader
}
