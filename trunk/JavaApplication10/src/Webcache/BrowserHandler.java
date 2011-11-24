/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;

/**
 *
 * @author okamura
 */
public class BrowserHandler{
    private static final byte CR = 0x0d;
    private static final byte LF = 0x0a;
    private static final int BUFFER_SIZE = 8192;
    
    private ServerSocket serverSocket;
    
    public BrowserHandler(int port){
        try{
            this.serverSocket = new ServerSocket(port);
        } catch(IOException exception){
            System.err.println("Browser Reader - constructor error");
            System.exit(-1);
        }
    }
    
    public void run(){        
        while(true){          
            try {
                Socket socket = serverSocket.accept();
                new Request(socket).start();
            } catch (IOException exception) {
                System.err.println("Browser Handler - run error" + exception);
            }
        } 
    }
        
    private class Request extends Thread {
        private Socket socket;
        private HttpURLConnection connection;
        
        public Request(Socket socket){
            this.socket = socket;            
        }
        
        @Override
        public void run() {
            readRequest();
            writeResponse();
        }
        
        public void readRequest(){
            ArrayList<String> request = new ArrayList<String>();
            try{ 
                BufferedReader requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                for (String line = requestReader.readLine(); line != null && !line.isEmpty(); line = requestReader.readLine()) {
                    request.add(line);
                }
                
                String[] requestLine = request.get(0).split(" ");
                String method = requestLine[0];
                URI uri = new URI(requestLine[1]);
                String version = requestLine[2];
                System.out.println("Request [" + socket.getPort() + "] " + method + " " + uri + " " + version);

                connection = (HttpURLConnection) uri.toURL().openConnection();
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
        
        public void writeResponse(){
            try {
                             
                DataOutputStream requestWriter = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                String contentEncoding = connection.getContentEncoding();
                int contentLength = connection.getContentLength();
                String transferEncoding = connection.getHeaderField("Transfer-Encoding");
                boolean chunked = (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked"));               
                System.out.println("Response [" + socket.getPort() + "] " + responseCode + " " + responseMessage);
                
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
                    while ((readBytes = responseReader.read(buffer)) > 0) {
                        requestWriter.write(buffer, 0, readBytes);
                        requestWriter.flush();
                    }
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
