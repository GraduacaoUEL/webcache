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
    
    public TabelaGeral()
    {
       tabelaGeral = new ArrayList();
       tabelaIndice = new ArrayList();
    }
    
    public void add(ArquivoRemoto ar)
    {
        tabelaGeral.add(ar);
        tabelaIndice.add(ar.getUrl());
    }
    
    public boolean verificar(String ar)
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
