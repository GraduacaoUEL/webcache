/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Vinicius
 */
public class TelaPrincipal extends JPanel {

    private JFrame mainFrame;
    private JPanel proxyPanel;
    private Thread sockets;
    private JTextField usuario, senha;
    private Checkbox proxyCheckBox;
    private JButton okButton;

    public void buildGUI() {
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Configurar Proxy");
        mainFrame.setResizable(false);
        proxyPanel = new JPanel();

        usuario = new JTextField(20);
        senha = new JTextField(20);
        proxyCheckBox = new Checkbox("Usar Proxy", false);
        okButton = new JButton("Confirmar");
        
        proxyCheckBox.setBounds(150, 10, 50, 30);
        usuario.setBounds(150, 30, 50, 30);
        senha.setBounds(150, 40, 50, 30);
        okButton.setBounds(150, 80, 30, 40);

        usuario.setText("usuario");
        senha.setText("senha");

        proxyPanel.add(proxyCheckBox);
        proxyPanel.add(usuario);
        proxyPanel.add(senha);
        proxyPanel.add(okButton);
        
        okButton.addActionListener(new okButtonListener());

        mainFrame.getContentPane().add(BorderLayout.CENTER, proxyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 200);
        mainFrame.setVisible(true);
    }
    
    private class okButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            sockets = new Thread(new Sockets());
            sockets.start();
            mainFrame.setVisible(false);
        }
    }

    public class Sockets implements Runnable {
        

        @Override
        public void run() {
            
            BrowserHandler browserHandler = new BrowserHandler(5558, proxyCheckBox.getState(), usuario.getText(), senha.getText());
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
