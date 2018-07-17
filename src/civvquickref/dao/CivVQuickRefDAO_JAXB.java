/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.dao;

import civvquickref.CivilizationList;
import civvquickref.CivilizationVGame;
import civvquickref.ObjectFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author gabag
 */
public class CivVQuickRefDAO_JAXB implements CivVQuickRefDAO {
    
    private static final String PACKAGE = "civvquickref";
    
    private static JAXBContext jaxbContext;
    private String xmlFile;
    
    /**
     * Performance tip: use an unique JAXBContext instead of creating one for 
     * each operation.
     * 
     * https://stackoverflow.com/questions/7400422/jaxb-creating-context-and-marshallers-cost
     * https://stackoverflow.com/questions/6043956/how-do-i-improve-performance-of-application-that-uses-the-jaxbcontext-newinstanc
     */
    static {
        try {
            jaxbContext = JAXBContext.newInstance(PACKAGE);
        } catch (JAXBException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
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
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement jaxbElement = (JAXBElement)unmarshaller.unmarshal(new FileInputStream(xmlFile));
        
        // Now we extract the object tree using the generated JAXB link.
        CivilizationVGame civVGame = (CivilizationVGame)jaxbElement.getValue();
        
        return civVGame.getCivlist().getCiv();
    }
    
    /**
     * This method receives the entire data of a CivilizationVGame: the list of 
     * civilizations and the name of the mod. It puts those objects into their 
     * corresponding JAXB objects and saves the resulting CivilizationVGame object 
     * into the XML.
     * 
     * Structure:
     * 
     * CivilizationVGame
     * |-- String mod
     * |-- CivilizationList civilizationList
     *     |
     *     |-- List<CivilizationList.Civ> civList
     * 
     * @param civList The list of civilizations.
     * @param modName The name of the Civilization V mod.
     * @throws javax.xml.bind.JAXBException
     * @throws java.io.FileNotFoundException
     */
    @Override
    public void saveCivs(List<CivilizationList.Civ> civList, String modName) 
            throws JAXBException, FileNotFoundException {
        
        ObjectFactory factory = new ObjectFactory();
        
        CivilizationVGame civVGame = factory.createCivilizationVGame();
        
        CivilizationList civilizationList = factory.createCivilizationList();
        List<CivilizationList.Civ> civ = civilizationList.getCiv();
        civ.addAll(civList);
        
        System.out.println(civ.get(civ.size()-1).getCivname());
        
        civVGame.setCivlist(civilizationList);
        civVGame.setMod(modName);
        
        // Marshall the CivilizationVGame
        
        Marshaller m = jaxbContext.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(factory.createCivilizationv(civVGame), new File(xmlFile));
    }
    
    @Override
    public String getSource() {
        
        String[] splitFile = xmlFile.split("\\\\");
        
        return splitFile[splitFile.length - 1];
    }
}
