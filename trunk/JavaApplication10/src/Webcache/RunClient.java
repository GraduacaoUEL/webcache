/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static int portaServidorMestre = 5555;
    
    //Atributos para ativa o Socket de comunicação com outros clientes
    private ServerSocket connectionClientClient_Socket;
    private Thread connectionClientClient_Thread, connectionWithClient_Thread, newClient_Thread;
    private ArrayList clientOutputStreams;
    private Socket sockWithClient;
    private BufferedReader readerWithClient;
    private PrintWriter writerWithClient;
    private ArrayList listaClientes;
    private static int portaComunicacaoClienteCliente = 5556;
    
    //Atributos para Transferência de Arquivo
    private Thread connectionFileClientClient_Thread;
    private String fileName;
    private static String diretorio = "C:\\Users\\Ernesto\\Documents\\NetBeansProjects\\trunk\\JavaApplication10\\src\\Arquivo\\";
    private static int portaFileTransfer = 5557;

    public static void main(String[] args) throws IOException {
        RunClient rc = new RunClient();
    }

    public RunClient() throws IOException{
        //Ativa a parte de conexão com o Servidor Mestre
        connectionToMasterServer_Thread = new Thread(new connectionToMasterServer("127.0.0.1", portaServidorMestre));
        connectionToMasterServer_Thread.start();
        
        
        
        
        //Ativa a parte que comunicação direta com outros clientes        
        connectionClientClient_Thread = new Thread(new connectionClientClient(portaComunicacaoClienteCliente));
        connectionClientClient_Thread.start();
        
        //Ativa a conexão de Transferência de Arquivo
        connectionFileClientClient_Thread = new Thread(new connectionFileClientClient(portaFileTransfer));
        connectionFileClientClient_Thread.start();
        
        //inicia a arraylist
        listaClientes = new ArrayList();
    
        
        
        /*EXECUTAR SEPARADO PARA TESTAR - COMENTAR A LINHA 58 A 63*/
        String[] listaArquivos = {"reg_alloc.pdf", "Web_Cache_Distribuido.pdf", "Gramatica C.docx", "WebCacheDistribuido.rar"};
        
        System.out.println("Quantidade de Arquivos a ser resgatados: " + listaArquivos.length);
        connectionWithClient_Thread = new Thread(new startConnectionWithClient("127.0.0.1", portaComunicacaoClienteCliente, listaArquivos));
        connectionWithClient_Thread.start();       
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
                String[] dados;
                //Mensagem que vem do Servidor Mestre
                while ((message = readerMasterServer.readLine()) != null) {
                    dados = message.split(";");

                    if (dados[0].equals("IP")) {
                        int i = 1;
                        while (i < dados.length - 1) {
                            listaClientes.add(dados[i]);
                            i++;
                        }
                    }
                    System.out.println("Lista de Clientes atualizado");
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
                String[] mensagem = new String[5000];
                String message;
                try {

                    //Se o cliente enviar alguma mensagem
                    while ((message = readerClientClient.readLine()) != null) {
                        mensagem = message.split(";");
                        
                        if (mensagem[0].equals("NomeDoArquivo")) {
                            fileName = mensagem[1];
                            File myFile;
                            if( (myFile = new File(diretorio + fileName))!=null ){
                                tellTheNeighbor("TenhoOArquivo");
                                System.out.println("Tenho o Arquivo: " + fileName);
                            } else {
                                tellTheNeighbor("NaoTenhoOArquivo");
                                System.out.println("Não tenho o Arquivo: " + fileName);
                            }                            
                        }
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

        private Thread temp;
        private String IPneighbor, NomeArq;
        private String[] arqNome;
        private boolean endProcess;

        public startConnectionWithClient(String ipNeighbor, int portNeighbor, String[] nomeArquivo) {
            IPneighbor = ipNeighbor;
            arqNome = nomeArquivo;
            endProcess = false;
            try {
                sockWithClient = new Socket(ipNeighbor, portNeighbor);
                InputStreamReader streamReader = new InputStreamReader(sockWithClient.getInputStream());
                readerWithClient = new BufferedReader(streamReader);
                writerWithClient = new PrintWriter(sockWithClient.getOutputStream());
                System.out.println("Conexão com o Cliente estabelecida. Procurar arquivo nos vizinhos...");

                temp = new Thread(new listen());
                temp.start();

            } catch (IOException ex) {
                System.out.println("FALHA AO TENTAR CONECTAR COM O CLIENTE");
            }
        }

        private class listen implements Runnable {

            public void run() {
                String message;
                Thread tempListen;

                try {
                    //Resposta do Cliente/Vizinho
                    while ((message = readerWithClient.readLine()) != null) {
                        System.out.println(message);
                        if (message.equals("TenhoOArquivo")) {
                            System.out.println("O Viznho tem o arquivo");
                            tempListen = new Thread(new startGetTransfer(IPneighbor, portaFileTransfer, NomeArq));
                            tempListen.start();
                            System.out.println("Transferencia terminada");
                            endProcess = true;
                        } else if (message.equals("NaoTenhoOArquivo")) {
                            System.out.println("O Vizinho não tem o arquivo");
                            endProcess = true;
                        }
                    }

                } catch (Exception ex) {
                    System.out.println("CONEXÃO COM O CLIENTE ENCERRADA");
                    try {
                        temp.interrupt();
                        connectionToMasterServer_Thread.interrupt();
                        readerWithClient.close();
                        writerWithClient.close();
                        sockWithClient.close();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                } finally {
                    try {
                        temp.interrupt();
                        connectionToMasterServer_Thread.interrupt();
                        readerWithClient.close();
                        writerWithClient.close();
                        sockWithClient.close();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            }
        }

        public void run() {

            for (int i = 0; i < arqNome.length; i++) {
                System.out.println("Leitura do nome do arquivo: " + arqNome[i]);
                NomeArq = arqNome[i];

                String sTemp = "NomeDoArquivo;" + NomeArq;

                writerWithClient.println(sTemp);
                writerWithClient.flush();

                System.out.println(sTemp);

                while (endProcess == false) {
                    //System.out.print(".");
                }
                endProcess = false;
                System.out.println("Busca encerrada");
            }
        }
    }//Fim do startConnectionWithClient
    
    //Faz a transferência do Arquivo para o Vizinho
    private class connectionFileClientClient implements Runnable {
        
        ServerSocket servsock;
        
        public connectionFileClientClient(int Port) throws IOException {
            servsock = new ServerSocket(Port);            
        }
        
        public void runTransfer() throws IOException {
            while (true) {
                System.out.println("Waiting...");

                Socket clientsock = servsock.accept();
                System.out.println("Accepted connection : " + clientsock);

                // sendfile
                System.out.println("Diretorio: " +diretorio+fileName);
                System.out.println("Nome do Arquivo: " + fileName);
                File myFile = new File(diretorio + fileName);
                byte[] mybytearray = new byte[(int) myFile.length()];
                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                bis.read(mybytearray, 0, mybytearray.length);
                OutputStream os = clientsock.getOutputStream();
                System.out.println("Sending...");
                os.write(mybytearray, 0, mybytearray.length);
                os.flush();
                clientsock.close();
            }
        }
        
        public void run() {
            try {
                runTransfer();
            } catch (IOException ex) {
                Logger.getLogger(RunClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
   
    //Método para pegar um arquivo do vizinho
    private class startGetTransfer implements Runnable {

        private String ip, nomeArquivo;
        private int port;
        public startGetTransfer(String ipTransfer, int portTransfer, String nomeArquivoTransfer) {
            ip = ipTransfer;
            port = portTransfer;
            nomeArquivo = nomeArquivoTransfer;
        }

        public void run() {
            try {
                int filesize = 6022386; // filesize temporary hardcoded

                long start = System.currentTimeMillis();
                int bytesRead;
                int current = 0;
                // localhost for testing
                Socket getsock = new Socket(ip, port);
                System.out.println("Connecting...");

                // receive file
                byte[] mybytearray = new byte[filesize];
                InputStream is = getsock.getInputStream();
                FileOutputStream fos = new FileOutputStream(diretorio + "Copy-" + nomeArquivo);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(mybytearray, 0, mybytearray.length);
                current = bytesRead;

                // thanks to A. Cádiz for the bug fix
                do {
                    bytesRead =
                            is.read(mybytearray, current, (mybytearray.length - current));
                    if (bytesRead >= 0) {
                        current += bytesRead;
                    }
                } while (bytesRead > -1);

                bos.write(mybytearray, 0, current);
                bos.flush();
                long end = System.currentTimeMillis();
                System.out.println("Arquivo resgatado");
                System.out.println(end - start);
                bos.close();
                getsock.close();
            } catch (IOException ioe) {
            }
        }
    }
}
