/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Vinicius Tadeu, Ernesto, Hayato, Helio
 */
public class TelaPrincipal extends JPanel {

    private JFrame mainFrame;
    private JPanel proxyPanel;
    private Thread sockets;
    private JTextField usuario, senha, ipServidorMestre;
    private Checkbox proxyCheckBox;
    private JButton okButton;
    private JLabel ipLabel, userLabel, senhaLabel;

    public void buildGUI() {
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Configurar Proxy");
        mainFrame.setResizable(false);
        proxyPanel = new JPanel(new FlowLayout());
        
        ipServidorMestre = new JTextField(20);        
        usuario = new JTextField(30);
        senha = new JTextField(30);
        proxyCheckBox = new Checkbox("Usar Proxy", false);
        okButton = new JButton("Confirmar");

        /*ipServidorMestre.setBounds(150, 0, 50, 30);
        proxyCheckBox.setBounds(150, 20, 50, 30);
        usuario.setBounds(150, 30, 50, 30);
        senha.setBounds(150, 40, 50, 30);
        okButton.setBounds(150, 80, 30, 40);*/

        //ipServidorMestre.setText("IP do Servidor Mestre");
        //usuario.setText("usuario");
        //senha.setText("senha");
        ipLabel = new JLabel();
        ipLabel.setText("IP do Servidor Mestre: ");
        userLabel = new JLabel();
        userLabel.setText("Usuário: ");
        senhaLabel = new JLabel();
        senhaLabel.setText("Senha: ");
                
        proxyPanel.add(ipLabel);
        proxyPanel.add(ipServidorMestre);        
        proxyPanel.add(userLabel);
        proxyPanel.add(usuario);
        proxyPanel.add(senhaLabel);
        proxyPanel.add(senha);
        proxyPanel.add(proxyCheckBox);
        proxyPanel.add(okButton);

        okButton.addActionListener(new okButtonListener());

        mainFrame.getContentPane().add(BorderLayout.CENTER, proxyPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 200);
        mainFrame.setVisible(true);
    }

    private class okButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if(ipServidorMestre.getText()!=null && !ipServidorMestre.getText().equals("")) {
                sockets = new Thread(new Sockets());
                sockets.start();
                mainFrame.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "Forneça o IP do Servidor Mestre");
            }
        }
    }

    public class Sockets implements Runnable {

        @Override
        public void run() {

            BrowserHandler browserHandler = new BrowserHandler(5558, proxyCheckBox.getState(), usuario.getText(), senha.getText());
            RunClient rc;
            try {
                rc = new RunClient(ipServidorMestre.getText());
            } catch (IOException ex) {
                Logger.getLogger(TelaPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
            browserHandler.run();
        }
    }
}
