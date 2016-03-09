package apps.stella.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import apps.stella.ccda.extractor.entry.CcdaEntryExtractor;
import apps.stella.ccda.extractor.entry.ImmunizationEntryExtractor;

public class CcdaSectionExtractor {
	
	final String IMMUNIZATION_SECION_ID = "";
	
	final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();
	
	
	
	public void extract(final String filePath) {
		
		try {
		
			File fXmlFile = new File(filePath);
			
			Document doc;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			
		
			doc = dBuilder.parse(fXmlFile);		
				
			doc.getDocumentElement().normalize();
			
			System.out.println("----------------------------");
			
			System.out.println("Reading Immunization section");
			
			extractImmunizationSection(doc);
		
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private String extractImmunizationSection(final Document doc) {
		
		NodeList nList = doc.getElementsByTagName(IMMUNIZATION_SECION_ID);

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			String sql  = immunizationExtractor.extractData(nNode);
			
		}
		
		return "";		
	}
}
