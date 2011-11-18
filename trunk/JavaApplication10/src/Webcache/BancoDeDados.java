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
    private String[] memoriaConexoes;
    
  public void BancoDeDados() {
        inicializaMemoriaConexoes();
    }
  
  private void inicializaMemoriaConexoes() {
        String str = new String();
        int tamMemoria = 0;
        int i = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader("//src//Webcache//conexoes"));
            str = in.readLine();
            tamMemoria = Integer.parseInt(str);
            //System.out.println("INDICE: " + str);
            memoriaConexoes = new String[tamMemoria + 1];
            while ((str = in.readLine()) != null) {
                    memoriaConexoes[i] = str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
  
  
  public String[] getMemoria()
  {
      return memoriaConexoes;
  }
  
      public static void main(String[] args) throws IOException{
    }
}


