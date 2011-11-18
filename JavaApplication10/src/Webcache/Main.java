/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import javax.swing.JFrame;

/**
 *
 * @author Vinicius
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TelaPrincipal t = new TelaPrincipal();
        JFrame principal = new JFrame("Tela Principal");
        t.buildGUI();
    }
}
