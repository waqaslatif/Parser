package com.stella.utils;

import java.io.File;
import java.text.ParseException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FilenameUtils;
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

    private static final String IMMUNIZATION_SECION_ID = "2.16.840.1.113883.10.20.22.2.2.1";
    private static final String ACTIVE_PROBLEM_ID = "2.16.840.1.113883.10.20.22.4.3";
    private static final String PROGRESS_NOTE_SECION_ID = "1.3.6.1.4.1.19376.1.5.3.1.3.4";
    
    private static final String XML_EXTENSION = "xml";
    private static final String LINE_BREAK = "\n";

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder dBuilder;
    private Document document;

    private final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();
    private final CcdaEntryExtractor progressNoteEntryExtractor = new ProgressNoteEntryExtractor();

    public void extract(final String directoryPath) {

        StringBuilder sbCcdaSQL = new StringBuilder();
        try {

            File ccdDatasetDir = new File(directoryPath);
            if(ccdDatasetDir.isDirectory()) {
            	for(File ccdFile: ccdDatasetDir.listFiles()) { 
            		//extractProgressNoteSection(doc);
	                if (FilenameUtils.getExtension(ccdFile.getName()).equals(XML_EXTENSION)) {
	            		System.out.println("----------------------------");	
		                System.out.println("Reading File : " + ccdFile.getName());
		 
		                dBuilder = dbFactory.newDocumentBuilder();
		                document = dBuilder.parse(ccdFile);		
		                document.getDocumentElement().normalize();
		
		                sbCcdaSQL.append(extractImmunizationSection(document));
		                sbCcdaSQL.append(extractProgressNoteSection(document));
		                sbCcdaSQL.append(extractActiveProblem(document));
	                }
            	}
            	
            	//TBD:Write this all script into .txt or .sql file
            	
            }
            System.out.println(sbCcdaSQL.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String extractProgressNoteSection(final Document doc) throws XPathExpressionException, ParseException {

        System.out.println("----------------------------");
        System.out.println("Reading Progress Note Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + PROGRESS_NOTE_SECION_ID
                + "']");

        if (sectionNode != null) {
            return progressNoteEntryExtractor.extractData(sectionNode);
        }

        return "";
    }

    private StringBuilder extractImmunizationSection(final Document doc) throws XPathExpressionException,
            ParseException {

        StringBuilder sbSql = new StringBuilder();

        System.out.println("----------------------------");
        System.out.println("Reading Immunization Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + IMMUNIZATION_SECION_ID + "']");

        // System.out.println(Utils.nodeToString(sectionNode));

        final String immGroupId = UUID.randomUUID().toString();

        immunizationExtractor.setGroupId(immGroupId);

        String sqlImmunGroup = "INSERT INTO records.ImmunizationGroup(id, m2hid) " + "VALUES('%s' , '%s');";

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
     * Extracts the active problems CCD document and build SQL insert queries for ActiveProblem table.
     * 
     * @param doc
     * @return
     * @throws XPathExpressionException
     */
    public String extractActiveProblem(final Document doc) throws XPathExpressionException { 
		final ActiveProblemExtractor activeProblemExtractor = new ActiveProblemExtractor();
		final XPathExpression problemExp = Utils.getXPathExpression("//act[templateId"
				+ "/@root='" + ACTIVE_PROBLEM_ID + "']");
		final NodeList nList = (NodeList) problemExp.evaluate(doc, XPathConstants.NODESET);
		final StringBuilder querybuilder = new StringBuilder();
		final String problemGroupId = UUID.randomUUID().toString();
		
		if (nList.getLength() > 0) {
	        querybuilder.append(buildActiveProblemGroupSQL(problemGroupId));
	        activeProblemExtractor.setGroupId(problemGroupId);
		}
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			final Node nNode = nList.item(temp);
			final String query = activeProblemExtractor.extractData(nNode);
			querybuilder.append("\n");
			querybuilder.append(query);
		}
		return querybuilder.toString();
	}
    
    private String buildActiveProblemGroupSQL(final String problemGroupId){
    	String sqlProblemGroup = "INSERT INTO records.\"ActiveProblemGroup\"(id, m2hid, timestamp)"
				+ " VALUES ('%s', '%s', '%s');";
        return String.format(sqlProblemGroup, problemGroupId, Utils.getM2hid(), Utils.getCurrentDate());
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
