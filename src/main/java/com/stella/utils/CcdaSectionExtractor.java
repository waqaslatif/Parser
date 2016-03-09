package com.stella.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stella.ccda.extractor.entry.ActiveProblemExtractor;
import com.stella.ccda.extractor.entry.CcdaEntryExtractor;
import com.stella.ccda.extractor.entry.ImmunizationEntryExtractor;
import com.stella.ccda.extractor.entry.ProgressNoteEntryExtractor;

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
    private static final String PROGRESS_NOTE_SECION_ID = "1.3.6.1.4.1.19376.1.5.3.1.3.4";

    private final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();
    private final CcdaEntryExtractor progressNoteEntryExtractor = new ProgressNoteEntryExtractor();

    public void extract(final String filePath) {
    	
    	StringBuilder sbCcdaSQL = new StringBuilder();

        try {

            File ccdDatasetDir = new File(filePath);
            if(ccdDatasetDir.isDirectory()) {
            	for(File ccdFile: ccdDatasetDir.listFiles()) { 
	            	
            		System.out.println("----------------------------");	
	                System.out.println("Reading File : " + ccdFile.getName());
	
	                dBuilder = dbFactory.newDocumentBuilder();
	
	                doc = dBuilder.parse(ccdFile);
	
	                doc.getDocumentElement().normalize();
	
	                sbCcdaSQL.append(extractImmunizationSection(doc));
	                
	                //extractProgressNoteSection(doc);
	                
	                //ccdXmlDocuments.add(doc);
            	}
                //System.out.println(extractActiveProblem(ccdXmlDocuments));
            	
            	//TBD:Write this all script into .txt or .sql file
            	
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String extractProgressNoteSection(final Document doc) throws XPathExpressionException {

        System.out.println("----------------------------");
        System.out.println("Reading Progress Note Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + PROGRESS_NOTE_SECION_ID
                + "']");

        if (sectionNode != null) {
            return progressNoteEntryExtractor.extractData(sectionNode);
        }

        return "";
    }

    private StringBuilder extractImmunizationSection(final Document doc) throws XPathExpressionException {

    	StringBuilder sbSql = new StringBuilder();
    	
        System.out.println("----------------------------");        
        System.out.println("Reading Immunization Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + IMMUNIZATION_SECION_ID + "']");

        // System.out.println(Utils.nodeToString(sectionNode));
        
        final String immGroupId = UUID.randomUUID().toString();
        
        immunizationExtractor.setGroupId(immGroupId);
		
		String sqlImmunGroup = "INSERT INTO records.ImmunizationGroup(id, m2hid) "
								+ "VALUES('%s' , '%s');";
		
		sbSql.append(sqlImmunGroup);
		
		System.out.println("----------------------------");
		
		System.out.println("Creating Immunization Group");
		
		sqlImmunGroup = String.format(sqlImmunGroup, immGroupId, Utils.getM2hid());
		
		final NodeList entryList = getSectionEntries(sectionNode, "entry");

        for (int temp = 0; temp < entryList.getLength(); temp++) {

            Node entryNode = entryList.item(temp);

            // System.out.println("----------------------------");
            // System.out.println(Utils.nodeToString(entryNode));

            sbSql.append(immunizationExtractor.extractData(entryNode));
        }

        return sbSql;
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
	

    private Node extractSectionByID(final Document doc, String sectionXpath) throws XPathExpressionException {
        XPathExpression sectionXpathExp = Utils.getXPathExpression(sectionXpath);
        return (Node) sectionXpathExp.evaluate(doc, XPathConstants.NODE);
    }

    private NodeList getSectionEntries(final Node sectionNode, String entryXpath) throws XPathExpressionException {
        XPathExpression entryXpathExp = Utils.getXPathExpression(entryXpath);
        return (NodeList) entryXpathExp.evaluate(sectionNode, XPathConstants.NODESET);
    }

}
