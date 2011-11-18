/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class PassThroughProxy {

    private URL url;
    private URLConnection uc;
    private Files gerenciador;
    public void start(String urlFromBrowser) {
        if (urlFromBrowser.length() < 2) {
            return; //string vazia
        }
        gerenciador = new Files();
   //     setProxy();

        try {
            url = new URL(urlFromBrowser);
            try {

                uc = url.openConnection();
  //              String encoded = new String(userPass());
  //              uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
                uc.connect();
            } catch (IOException ex) {
                Logger.getLogger(PassThroughProxy.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(PassThroughProxy.class.getName()).log(Level.SEVERE, null, ex);
        }


        String page = new String();
        String line = new String();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                page += line + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(PassThroughProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
         gerenciador.SaveToFile(urlFromBrowser,page);
    }

    void setProxy() {
        System.setProperty("http.proxyHost", "cache.uel.br");
        System.setProperty("http.proxyPort", "8080");
    }

    public String userPass() {
        return Base64.encode(new String("200905600493:computador").getBytes());
    }
}
