package com.stella.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stella.ccda.extractor.entry.ActiveProblemExtractor;
import com.stella.ccda.extractor.entry.CcdaEntryExtractor;
import com.stella.ccda.extractor.entry.ImmunizationEntryExtractor;

/**
 * @author ali
 *
 */
public class CcdaSectionExtractor {
	
	private Document doc;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder dBuilder;

    private static final String IMMUNIZATION_SECION_ID = "2.16.840.1.113883.10.20.22.2.2.1";
    private static final String ACTIVE_PROBLEM_ID = "2.16.840.1.113883.10.20.22.4.3";
    private static final String LINE_BREAK = "\n";
    
    private final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();

    public void extract(final String filePath) {

        try {

            File ccdDatasetDir = new File(filePath);
            if(ccdDatasetDir.isDirectory()) {
            	List<Document> ccdXmlDocuments = new ArrayList<Document>();
            	for(File ccdFile: ccdDatasetDir.listFiles()) { 
	            	System.out.println("----------------------------");
	
	                System.out.println("Reading File : " + ccdFile.getName());
	
	                dBuilder = dbFactory.newDocumentBuilder();
	
	                doc = dBuilder.parse(ccdFile);
	
	                doc.getDocumentElement().normalize();
	
	                //M Utils.printDocument(doc, System.out);
	
	                extractImmunizationSection(doc);
	                ccdXmlDocuments.add(doc);
            	}
                System.out.println(extractActiveProblem(ccdXmlDocuments));
            }
            

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String extractImmunizationSection(final Document doc) throws XPathExpressionException {

        System.out.println("----------------------------");

        System.out.println("Reading Immunization section");

        XPathExpression sectionXpathExp = Utils.getXPathExpression("//section[templateId/@root='" + IMMUNIZATION_SECION_ID + "']");
        Node sectionNode = (Node) sectionXpathExp.evaluate(doc, XPathConstants.NODE);
        
        //System.out.println(Utils.nodeToString(sectionNode));
        
        XPathExpression entryXpathExp = Utils.getXPathExpression("entry");
		NodeList nList = (NodeList) entryXpathExp.evaluate(sectionNode, XPathConstants.NODESET);
        
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node entryNode = nList.item(temp);
            
            //System.out.println(Utils.nodeToString(entryNode));
            
            String sql = immunizationExtractor.extractData(entryNode);
        }

        return "";
    }
    
    /**
     * Extracts the content from list of ccd xml documents and converts into list of insert queries.
     * @param ccdXMLDocumentsList
     * @return
     * @throws XPathExpressionException
     */
    public static String extractActiveProblem(final List<Document> ccdXMLDocumentsList) throws XPathExpressionException { 
		final StringBuffer sqlScriptBuffer = new StringBuffer();
		for(Document ccdXMLDocument: ccdXMLDocumentsList) {
			sqlScriptBuffer.append(extractActiveProblem(ccdXMLDocument));
		}
		return sqlScriptBuffer.toString();
		
	}
	
    /**
     * Extracts the active problems CCD document and build SQL insert queries for ActiveProblem table.
     * @param doc
     * @return
     * @throws XPathExpressionException
     */
    public static String extractActiveProblem(final Document doc) throws XPathExpressionException { 
		final ActiveProblemExtractor activeProblemExtractor = new ActiveProblemExtractor();
		final XPathExpression problemExp = Utils.getXPathExpression("//act[templateId"
				+ "/@root='" + ACTIVE_PROBLEM_ID + "']");
		final NodeList nList = (NodeList) problemExp.evaluate(doc, XPathConstants.NODESET);
		final StringBuffer queryBuffer = new StringBuffer();
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			final Node nNode = nList.item(temp);
			final String query = activeProblemExtractor.extractData(nNode);
			queryBuffer.append(query);
			if (temp > 0) {
				queryBuffer.append("\n");
			}
		}
		return queryBuffer.toString();
	}
	
}
