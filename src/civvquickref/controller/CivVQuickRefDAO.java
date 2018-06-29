/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.controller;

import civvquickref.CivilizationList;
import java.util.List;

/**
 *
 * @author gabag
 */
public interface CivVQuickRefDAO {
    
    public String loadModName() throws Exception;
    public List<CivilizationList.Civ> loadCivs() throws Exception;
    
    public String getSource();
}
