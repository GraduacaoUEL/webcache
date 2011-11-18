/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.*;

/**
 *
 * @author Vinicius
 */
public class BancoDeDados {
    private String[][] memoriaConexoes;
    private final int ITEMS_CONEXAO = 3; //numero de colunas
    
  public void BancoDeDado() {
        inicializaMemoriaConexoes();
    }
  
  private void inicializaMemoriaConexoes() {
        String str = new String();
        int tamMemoria = 0;
        int i = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader("//src//tabs//conexoes.txt"));
            str = in.readLine();
            tamMemoria = Integer.parseInt(str);
            //System.out.println("INDICE: " + str);
            memoriaConexoes = new String[tamMemoria + 1][ITEMS_CONEXAO];
            while ((str = in.readLine()) != null) {
                if (str.length() > 12) {

                    memoriaConexoes[i] = str.split(";");
                    i++;
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
  
}


