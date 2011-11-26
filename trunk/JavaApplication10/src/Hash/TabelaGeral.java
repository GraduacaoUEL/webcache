/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Hash;

import java.util.ArrayList;

/**
 *
 * @author helioalb
 */
public class TabelaGeral {
        private ArrayList tabela;
    
    void TabelaGeral()
    {
       tabela = new ArrayList();
    }
    
    public void add(ArquivoRemoto ar)
    {
        tabela.add(ar);
    }
    
    public boolean verificar(ArquivoRemoto ar)
    {
        return tabela.contains(ar);
    }
}
