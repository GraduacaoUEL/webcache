/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.IOException;

/**
 *
 * @author Vinicius Tadeu, Ernesto, Hayato, Helio
 */
public class WebCache {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        TelaPrincipal tp = new TelaPrincipal();
        tp.buildGUI();
//        Files fl = new Files();
//        System.out.println(fl.separarNome("http://google.com.br/favicon.ico"));
    }
}
