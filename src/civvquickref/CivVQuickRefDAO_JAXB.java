/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author gabag
 */
public class CivVQuickRefDAO_JAXB implements CivVQuickRefDAO {
    
    private static final String PACKAGE = "civvquickref";
    
    private String xmlFile;
    
    public CivVQuickRefDAO_JAXB() {
        
    }
    
    public CivVQuickRefDAO_JAXB(String xmlFile) {
        this.xmlFile = xmlFile;
    }
    
    /**
     * 
     * Method to load the name of the game mod.
     * 
     * @return
     * @throws JAXBException
     * @throws FileNotFoundException 
     */
    @Override
    public String loadModName() throws JAXBException, FileNotFoundException {
        // First, we fiddle with the JAXB part of the already mapped schema
        JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(new FileInputStream(xmlFile));
        
        // Now we extract the object tree using the generated JAXB link.
        CivilizationVGame civVGame = (CivilizationVGame)jaxbElement.getValue();
        
        return civVGame.getMod();
    }
    
    /**
     * 
     * Method to load the entire list of civilizations from a certain XML document.
     * 
     * @return 
     * @throws JAXBException
     * @throws FileNotFoundException 
     */
    @Override
    public List<CivilizationList.Civ> loadCivs() throws JAXBException, FileNotFoundException {
        
        // First, we fiddle with the JAXB part of the already mapped schema
        JAXBContext jaxbContext = JAXBContext.newInstance(PACKAGE);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(new FileInputStream(xmlFile));
        
        // Now we extract the object tree using the generated JAXB link.
        CivilizationVGame civVGame = (CivilizationVGame)jaxbElement.getValue();
        
        return civVGame.getCivlist().getCiv();
    }
    
    @Override
    public String getSource() {
        
        String[] splitFile = xmlFile.split("\\\\");
        
        return splitFile[splitFile.length - 1];
    }
}
