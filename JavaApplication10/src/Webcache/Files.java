/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author Vinicius Tadeu, Ernesto, Hayato, Helio
 */
public class Files {

    public void SaveToFile(String name, String dados) {
        String path = new String();
        String arquivo = new String();
        arquivo = separarNome(name);
        path = caminho(name);
        System.out.println(path);
        criarPasta(path);
        FileOutputStream saida;
        PrintStream fileSaida;
        try {
            saida = new FileOutputStream(path + arquivo);
            fileSaida = new PrintStream(saida);
            fileSaida.print(dados);
            saida.close();
            fileSaida.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String caminho(String str) {
        String temp = str;
        temp = temp.replaceAll("http:", "");
        temp = temp.replaceAll("//", "");
        temp = temp.replace('/', '.');
        temp = temp.replaceAll("\\.", "\\\\");
        return "src\\dados\\" + temp + "\\";
    }

    public void criarPasta(String path) {
        File file = new File(path);
        file.mkdirs();
    }

    public String separarNome(String str) {
        String[] temp = str.split("/");
        str = temp[temp.length - 1];
        return str;
    }
}
