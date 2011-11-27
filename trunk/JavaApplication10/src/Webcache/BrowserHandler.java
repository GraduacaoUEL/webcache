/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import Hash.ArquivoIndice;
import Hash.TabelaGeral;
import Hash.TabelaLocal;
import com.sun.org.apache.xml.internal.security.utils.Base64; //ssaporra buga quando gera jar ç.ç
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Vinicius Tadeu, Ernesto, Hayato, Helio
 */
public class BrowserHandler {

    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final int BUFFER_SIZE = 8192;
    private boolean useProxy;
    private ServerSocket serverSocket;
    private static String usuario, senha;
    private static TabelaLocal tabelaLocal;
    private static TabelaGeral tabelaGeral;
    
    private JFrame frame;
    private JPanel labelPanel, fieldPanel;
    private JLabel localLabel, remoteLabel, internetLabel;
    private JTextArea localArea, remoteArea, internetArea;

    public BrowserHandler(int port, boolean proxy, String user, String password) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.useProxy = proxy;
            this.usuario = user;
            this.senha = password;
            this.tabelaGeral = new TabelaGeral();
            this.tabelaLocal = new TabelaLocal();
        } catch (IOException exception) {
            System.err.println("Browser Reader - constructor error");
            System.exit(-1);
        }
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Browser Handler");
        frame.setResizable(false);
        
        localArea = new JTextArea(20, 25);
        localArea.setLineWrap(true);
        localArea.setWrapStyleWord(true);
        localArea.setEditable(false);
        DefaultCaret caretL = (DefaultCaret) localArea.getCaret();
        caretL.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane localScroller = new JScrollPane(localArea);
        localScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        localScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        localScroller.setAutoscrolls(true);
        localLabel = new JLabel("Requisição Local: ");
        
        remoteArea = new JTextArea(20, 25);        
        remoteArea.setLineWrap(true);
        remoteArea.setWrapStyleWord(true);
        remoteArea.setEditable(false);
        DefaultCaret caretR = (DefaultCaret) remoteArea.getCaret();
        caretR.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane remoteScroller = new JScrollPane(remoteArea);
        remoteScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        remoteScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        remoteScroller.setAutoscrolls(true);
        remoteLabel = new JLabel("Requisição Remota: ");
        
        internetArea = new JTextArea(20, 25);
        internetArea.setLineWrap(true);
        internetArea.setWrapStyleWord(true);
        internetArea.setEditable(false);
        DefaultCaret caretI = (DefaultCaret) internetArea.getCaret();
        caretI.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane internetScroller = new JScrollPane(internetArea);
        internetScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        internetScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        internetScroller.setAutoscrolls(true);
        internetLabel = new JLabel("Requisição Web: ");
        
        labelPanel = new JPanel(new GridLayout(3,1,1,1));
        labelPanel.add(localLabel);
        labelPanel.add(remoteLabel);
        labelPanel.add(internetLabel);
        
        fieldPanel = new JPanel(new GridLayout(3,1,1,1));
        fieldPanel.add(localScroller);
        fieldPanel.add(remoteScroller);
        fieldPanel.add(internetScroller);
        
        frame.getContentPane().add(BorderLayout.WEST, labelPanel);
        frame.getContentPane().add(BorderLayout.CENTER, fieldPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Request(socket, useProxy, tabelaGeral, tabelaLocal).start();
            } catch (IOException exception) {
                System.err.println("Browser Handler - run error" + exception);
            }
        }
    }

    private class Request extends Thread {

        private Socket socket;
        private HttpURLConnection connection;
        private boolean useProxy;
        private String url; //gambi
        Files ar = new Files();
        TabelaLocal tabelaLocal;
        TabelaGeral tabelaGeral;

        private Request(Socket socket, boolean useProxy, TabelaGeral tabelaGeral, TabelaLocal tabelaLocal) {
            this.socket = socket;
            this.useProxy = useProxy;
            this.tabelaGeral = tabelaGeral;
            this.tabelaLocal = tabelaLocal;
        }

        @Override
        public void run() {
            readRequest();
            writeResponse();
        }

        public void readRequest() {
            ArrayList<String> request = new ArrayList<String>();
            try {
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                BufferedReader requestReader = new BufferedReader(in);
                for (String line = requestReader.readLine(); line != null && !line.isEmpty(); line = requestReader.readLine()) {
                    request.add(line);
                    //System.out.println("Linha: " + line);
                }

                String[] requestLine = request.get(0).split(" ");
                String method = requestLine[0];
                URI uri = new URI(requestLine[1]);
                String version = requestLine[2];
                // System.out.println("Request [" + socket.getPort() + "] " + method + " " + uri + " " + version);

                connection = (HttpURLConnection) uri.toURL().openConnection();
                url = uri.toURL().toString();
                ArquivoIndice temp = new ArquivoIndice();
                temp.setIp("127.0.0.1");
                temp.setUrl(url);

                /*Se o arquivo se encontra localmente*/
                if (tabelaLocal.verificar(url)) {

                    //System.out.println("Pegar Localmente. Caminho do Arquivo " + ar.separarNome(url) + " está no diretorio " + ar.caminho(url));
                    localArea.append("LOCAL - Arquivo " + ar.separarNome(url) + " no diretório " + ar.caminho(url) + "\n");

                    File myFile = new File(ar.caminho(url) + ar.separarNome(url));
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);

                    DataOutputStream requestWriter = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    requestWriter.write(mybytearray, 0, mybytearray.length);
                    requestWriter.flush();

                    //System.out.println("Arquivo enviado localmente");
                    localArea.append("Arquivo " +ar.separarNome(url) + " enviado ao browser." + "\n \n");
                }

                /*Se há arquivo no vizinho*/
                else if (tabelaGeral.verificar(url)) {
                    remoteArea.append("REMOTO - Arquivo " + ar.separarNome(url) + "\n");
                    //pegaremoto
                    remoteArea.append("Arquivo " +ar.separarNome(url) + " enviado ao browser." + "\n \n");
                }
                
                else {
                    internetArea.append("WEB - Arquivo " + ar.separarNome(url) + "\n");
                }

                //se chegar aqui continua normal;
                if (useProxy == true) {
                    /* descomentar 1 e 2 usar o cache  da uel*/
                    setProxy("cache.uel.br", "8080");
                    connection.setRequestProperty("Proxy-Authorization", "Basic " + userPass(usuario, senha));
                    /* descomentar 17 para usar proxy sem pass ou senha */
// [17]                   setProxy("103.1.185.31","3128"); //proxy doidão da pqp

                }

                for (int i = 1; i < request.size(); i++) {
                    String[] requestHeader = request.get(i).split(": ");
                    connection.setRequestProperty(requestHeader[0], requestHeader[1]);
                }
                connection.setRequestMethod(method);
                connection.connect();
            } catch (MalformedURLException exception) {
                System.err.println("Request - readRequest error" + exception);
            } catch (URISyntaxException exception) {
                System.err.println("Request - readRequest error" + exception);
            } catch (IOException exception) {
                System.err.println("Request - readRequest error" + exception);
            }

        }

        void setProxy(String proxyName, String proxyPort) {
            System.setProperty("http.proxyHost", proxyName);
            System.setProperty("http.proxyPort", proxyPort);
        }

        public String userPass(String proxyUser, String proxyPass) {
            return Base64.encode((proxyUser + ":" + proxyPass).getBytes());
        }

        public void writeResponse() {
            try {

                DataOutputStream requestWriter = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentEncoding = connection.getContentEncoding();
                int contentLength = connection.getContentLength();
                String transferEncoding = connection.getHeaderField("Transfer-Encoding");
                boolean chunked = (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked"));
                // System.out.println("Response [" + socket.getPort() + "] " + responseCode + " " + responseMessage);

                String key;
                requestWriter.writeUTF(connection.getHeaderField(0));
                for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                    requestWriter.writeUTF(key + ": " + connection.getHeaderField(i));
                    requestWriter.writeByte(CR);
                    requestWriter.writeByte(LF);
                }
                requestWriter.writeUTF("Proxy-Connection: close");
                requestWriter.writeByte(CR);
                requestWriter.writeByte(LF);
                requestWriter.writeByte(CR);
                requestWriter.writeByte(LF);
                requestWriter.flush();

                if (contentLength > 0 || chunked) {
                    ArrayList<byte[]> content = new ArrayList<byte[]>();
                    InputStream responseReader;
                    if (contentEncoding == null) {
                        responseReader = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
                    } else if (contentEncoding.equalsIgnoreCase("gzip")) {
                        responseReader = new GZIPInputStream(new BufferedInputStream(connection.getInputStream()));
                    } else if (contentEncoding.equalsIgnoreCase("deflate")) {
                        responseReader = new InflaterInputStream(new BufferedInputStream(connection.getInputStream()));
                    } else {
                        responseReader = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
                    }

                    int readBytes;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    ar.criarPasta(ar.caminho(url));
                    FileOutputStream fileWriter = new FileOutputStream(ar.caminho(url) + ar.separarNome(url));
                    while ((readBytes = responseReader.read(buffer)) > 0) {
                        fileWriter.write(buffer);
                        requestWriter.write(buffer, 0, readBytes);
                        requestWriter.flush();
                    }

                    tabelaLocal.add(url);
                    fileWriter.close();
                    responseReader.close();
                }

                connection.disconnect();
            } catch (IOException exception) {
                System.err.println("Request - writeResponse error" + exception);
            } finally {
                try {
                    socket.close();
                } catch (IOException exception) {
                    System.err.println("Request - writeResponse error" + exception);
                }
            }
        }
    }
}
