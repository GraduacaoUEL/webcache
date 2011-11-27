/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author helioalb
 */
public class TabelaLocal {
    private ArrayList<String> tabelaIndice;
    private ArrayList<ArquivoRemoto> tabela;
    
    public TabelaLocal()
    {
       tabela = new ArrayList();
       tabelaIndice = new ArrayList();
       
    }
    
    public void add(String ar)
    {
        ArquivoRemoto br =  new ArquivoRemoto();
        br.setIp("127.0.0.1");
        br.setUrl(ar);
        br.setPing(0);
        br.setVida(System.currentTimeMillis());
        tabelaIndice.add(ar);
        tabela.add(br);
    }
    
    public boolean verificar(String ar)
    {
       return tabelaIndice.contains(ar);
    }
    
    public ArrayList getTabela()
    {
        return tabela;
    }
}
