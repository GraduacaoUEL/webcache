/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Webcache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Vinicius
 */
public class Files {
    
    
    public void SaveToFile(String name,String dados)
    {
        String path = new String();
        path = caminho(name);
        System.out.println(path);
        File file = new File(path +"\\test.html");

        try {
            file.mkdirs();
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(dados);
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public String caminho(String str)
    {
        String temp = str;
        temp = temp.replaceAll("http:", "");
        temp = temp.replaceAll("/","");
        temp = temp.replaceAll("\\.","\\\\");
        return "\\" + temp;
    }
}
