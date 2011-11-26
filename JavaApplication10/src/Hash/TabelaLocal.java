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
    private ArrayList tabela;
    
    void TabelaLocal()
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
