/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webcache;

/**
 *
 * @author Vinicius Tadeu, Ernesto, Hayato, Helio
 */
import java.io.*;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java ping class.
 * Created by Alvin Alexander, devdaily.com.
 */
public class Ping {

    public long pingEmMS(String ip) {
        long data = System.currentTimeMillis();
        try {
            InetAddress.getByName(ip).isReachable(10);
        } catch (IOException ex) {
            Logger.getLogger(Ping.class.getName()).log(Level.SEVERE, null, ex);
        }
        long data2 = System.currentTimeMillis();
        return data2 - data;
    }

    public static void main(String[] args) {
        Ping p = new Ping();
        System.out.println(p.pingEmMS("127.0.0.1"));
        System.out.println(p.pingEmMS("www.google.com.br"));

    }
}