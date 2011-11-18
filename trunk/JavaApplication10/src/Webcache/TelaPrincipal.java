/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import javax.swing.*;

/**
 *
 * @author Vinicius
 */
public class TelaPrincipal extends JPanel {
    
    JFrame mainFrame;
    
    JPanel proxyPanel; 

    public void buildGUI()
    {
        mainFrame = new JFrame();
        //mainFrame.setLayout(null);
        
        
        
        proxyPanel = new JPanel();
        //proxyPanel.setLayout(null);
        
        JTextField usuario = new JTextField(30);
        JTextField senha = new JTextField(30);
        Checkbox proxyCheckBox = new Checkbox("Usar Proxy",false);
        
        proxyCheckBox.setBounds(0, 0, 100, 30);
        usuario.setText("usuario");
        senha.setText("senha");
        
        proxyPanel.add(proxyCheckBox);
        proxyPanel.add(usuario);
        proxyPanel.add(senha);
        
      
 
        mainFrame.getContentPane().add(BorderLayout.CENTER, proxyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        mainFrame.setSize(800,600);
        mainFrame.setVisible(true);
    }






}


