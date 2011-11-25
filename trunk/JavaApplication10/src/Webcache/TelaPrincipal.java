/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Vinicius
 */
public class TelaPrincipal extends JPanel {
    
    JFrame mainFrame;
    
    JPanel proxyPanel; 
    Thread sockets;

    public void buildGUI()
    {
        mainFrame = new JFrame();
        //mainFrame.setLayout(null);
        
        
        proxyPanel = new JPanel();
        //proxyPanel.setLayout(null);
        
        JTextField usuario = new JTextField(30);
        JTextField senha = new JTextField(30);
        Checkbox proxyCheckBox = new Checkbox("Usar Proxy",false);     
        JButton okButton =  new JButton("Confirmar");
        
        okButton.setBounds(0, 0, 30, 40);
        proxyCheckBox.setBounds(0, 0, 100, 30);
        
        usuario.setText("usuario");
        senha.setText("senha");
        

        proxyPanel.add(proxyCheckBox);
        proxyPanel.add(usuario);
        proxyPanel.add(senha);
        proxyPanel.add(okButton);
        
        sockets = new Thread(new Sockets());
        sockets.start();
                
        
        mainFrame.getContentPane().add(BorderLayout.CENTER, proxyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        mainFrame.setSize(800,600);
        mainFrame.setVisible(true);
       

    }

    public class Sockets implements Runnable{

        @Override
        public void run() {
        BrowserHandler browserHandler = new BrowserHandler(5557,false);
        RunClient rc;
            try {
                rc = new RunClient();
            } catch (IOException ex) {
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
                


        browserHandler.run();
        }
        
    }




}


