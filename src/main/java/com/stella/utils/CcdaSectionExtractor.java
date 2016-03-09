package com.stella.utils;

import java.io.File;

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

public class CcdaSectionExtractor {
	
	private Document doc;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder dBuilder;

    private final String IMMUNIZATION_SECION_ID = "2.16.840.1.113883.10.20.22.2.2.1";

    private final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();

    public void extract(final String filePath) {

        try {

            File xmlFile = new File(filePath);

            System.out.println("----------------------------");

            System.out.println("Reading File : " + xmlFile.getName());

            dBuilder = dbFactory.newDocumentBuilder();

            doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            //M Utils.printDocument(doc, System.out);

            extractImmunizationSection(doc);
            ActiveProblemExtractor.extractActiveProblem(doc);

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
	
	
}
