/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;

import java.net.Socket;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class GetURL {
    PassThroughProxy pass;
    
    void start(Socket cSocket) {
        byte b[] = new byte[255];
        String out = new String();
        InputStream clientIn = null;
        OutputStream ServerOut = null;
        HttpParser httpParser;
        pass = new PassThroughProxy();
        try {
            clientIn = cSocket.getInputStream();
            clientIn.read(b);
            httpParser = new HttpParser(clientIn); 
            for (int i = 0; i < b.length; i++) {
                out += (char) b[i];
            }
            pass.start(httpParser.parseGetHost(out));
        } catch (IOException ex) {
            Logger.getLogger(GetURL.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
