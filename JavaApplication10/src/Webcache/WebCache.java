/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;

import java.io.IOException;

/**
 *
 * @author Vinicius
 */
public class WebCache {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        Server sr = new Server();
        sr.run();
        
//       Ping p = new Ping();
//       p.start();
    }
}
