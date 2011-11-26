/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hash;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author helioalb
 */
public class TabelaGeral {
        private ArrayList tabelaIndice;
        private ArrayList tabelaGeral;
    
    void TabelaGeral()
    {
       tabelaGeral = new ArrayList();
       tabelaIndice = new ArrayList();
    }
    
    public void add(ArquivoRemoto ar)
    {
        ArquivoIndice ai = new ArquivoIndice();
        ai.setIp(ar.getIp());
        ai.setUrl(ar.getUrl());
        tabelaGeral.add(ar);
        tabelaIndice.add(ai);
    }
    
    public boolean verificar(ArquivoIndice ar)
    {
        return tabelaIndice.contains(ar);
    }
    
    public void adicionarTabela(ArrayList tab)
    {
        Iterator it = tab.iterator();
        while(it.hasNext())
        {
            add((ArquivoRemoto) it.next());
        }
    }
}
