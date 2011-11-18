/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class BancoDeDados {

    private String[] memoriaConexoes;
    private int tamanhoMemoria;

    public void BancoDeDados() {

        inicializaMemoriaConexoes();
    }

    private void inicializaMemoriaConexoes() {
        String str = new String();

        int i = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader("src\\Webcache\\conexoes"));
            str = in.readLine();
            tamanhoMemoria = Integer.parseInt(str);
            //System.out.println("INDICE: " + str);
            memoriaConexoes = new String[tamanhoMemoria + 1];
            while ((str = in.readLine()) != null) {
                if (str != "null") {
                    memoriaConexoes[i] = str;
                    i++;
                }
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String[] getMemoria() {
        return memoriaConexoes;
    }

    public void novoIP(String novoIP) {
        escreveNoArquivo(novoIP);
    }

    private void escreveNoArquivo(String novoIP) {
        /*Limpa o arquivo antes de escrever*/
        RandomAccessFile file;
        try {
            file = new RandomAccessFile("src\\Webcache\\conexoes", "rw");
            file.setLength(0);
            file.close();

            /*Escreve no arquivo*/
            String buffer;
            BufferedWriter out = new BufferedWriter(new FileWriter("src\\Webcache\\conexoes"));
            //buffer = preparaString();

            tamanhoMemoria++;
            buffer = Integer.toString(tamanhoMemoria);
            out.write(buffer);
            out.newLine();
            for (int i = 0; i < tamanhoMemoria; i++) {
                buffer = "";
                buffer += memoriaConexoes[i];
                out.write(buffer);
                out.newLine();
            }
            out.write(novoIP);
            out.newLine();


            //out.write(buffer);
            out.close();
        } catch (IOException e) {
            e.getMessage();

        }

    }

    public void ResetarBanco() {
        BufferedWriter out;
        RandomAccessFile file;
        try {
             file = new RandomAccessFile("src\\Webcache\\conexoes", "rw");
            file.setLength(0);
            file.close();
            
            out = new BufferedWriter(new FileWriter("src\\Webcache\\conexoes"));
        out.write("4");
        out.newLine();
        out.write("127.0.0.1");
        out.newLine();
        out.write("127.0.0.1");
        out.newLine();
        out.write("127.0.0.1");
        out.newLine();
        out.write("127.0.0.1");
        out.newLine();
        out.close();
        } catch (IOException ex) {
           
        }

    }

    public static void main(String[] args) throws IOException {
        BancoDeDados bd = new BancoDeDados();
        bd.inicializaMemoriaConexoes();
        bd.ResetarBanco();
        
    }
}
