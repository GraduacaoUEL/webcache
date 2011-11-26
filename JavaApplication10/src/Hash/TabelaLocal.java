/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hash;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author helioalb
 */
public class TabelaLocal {
    private ArrayList tabelaIndice;
    private ArrayList tabela;
    
    void TabelaLocal()
    {
       tabela = new ArrayList();
       tabelaIndice = new ArrayList();
    }
    
    public void add(ArquivoIndice ar)
    {
        ArquivoRemoto br =  new ArquivoRemoto();
        br.setIp(ar.getIp());
        br.setUrl(ar.getUrl());
        br.setPing(0);
        br.setVida(System.currentTimeMillis());
        tabelaIndice.add(ar);
        tabela.add(br);
    }
    
    public boolean verificar(ArquivoIndice ar)
    {
        return tabelaIndice.contains(ar);
    }
    
    public ArrayList getTabela()
    {
        return tabela;
    }
}
