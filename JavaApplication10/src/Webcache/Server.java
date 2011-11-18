/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author Vinicius
 */
public class Server {
    
    private int clientCount;
    private Thread c;
        public static synchronized void println(String s) {
        System.out.println(s);
    }
        
        
    public void run() {
        try {
            ServerSocket sSocket = new ServerSocket(5000);
            while (true) {
                Socket cSocket = null;
                try {
                    println("listening...");
                    cSocket = sSocket.accept();
                    if (cSocket != null) {
                        clientCount++;
                        println("accepted as #" + clientCount + ":" + cSocket);
                        GetURL c = new GetURL();
                        c.start(cSocket);
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                try {
                    cSocket.close();
                } catch (Exception e) {
                    //fall thru
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
