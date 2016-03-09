package apps.stella.utils;

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

import apps.stella.ccda.extractor.entry.CcdaEntryExtractor;
import apps.stella.ccda.extractor.entry.ImmunizationEntryExtractor;

public class CcdaSectionExtractor {
	
	final String IMMUNIZATION_SECION_ID = "2.16.840.1.113883.10.20.22.2.2"; ///
	
	final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();
	
	
	
	public void extract(final String filePath) {
		
		try {
			
			File xmlFile = new File(filePath);
			
			System.out.println("----------------------------");
			
			System.out.println("Reading File : " + xmlFile.getName());		
			
			
			Document doc;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			
		
			doc = dBuilder.parse(xmlFile);		
				
			doc.getDocumentElement().normalize();
			
			extractImmunizationSection(doc);			
		
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}	
	}
	
	private String extractImmunizationSection(final Document doc) throws XPathExpressionException {
		
		System.out.println("----------------------------");
		
		System.out.println("Reading Immunization section");
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("/cda:section[cda:templateId/@root='" + IMMUNIZATION_SECION_ID + "']");

		NodeList nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			String sql  = immunizationExtractor.extractData(nNode);
		}
		
		return "";		
	}
	
	public static List<Node> extractValues(final Map<String, String> namespaceToUriMap, final Node node, final String xPathString) {
        final XPath xPath = newXPath(getNamespaceContext(namespaceToUriMap));
        final NodeList nodeList;
        try {
            final XPathExpression expr = xPath.compile(xPathString);
            nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        } catch (final XPathExpressionException ex) {
            throw ex;
        }
        final int nodeListLength = nodeList.getLength();
        final List<Node> nodes = new ArrayList<Node>();
        for (int i = 0; i < nodeListLength; i++) {
            final Node nodeItem  = nodeList.item(i);
            if (nodeItem != null) {
                nodes.add(nodeList.item(i));
            }
        }
        return nodes;
    }
	
	private Map<String, String> getNamespaceUriMapForSchematron(final String schematron) {
        final Map<String, String> namespaceToUriMap = new HashMap<String, String>();
        final Document document = XMLUtilities.parseString(schematron);
        final Element documentElement = document.getDocumentElement();
        final NodeList nsNodes = documentElement.getElementsByTagNameNS("http://purl.oclc.org/dsdl/schematron", "ns");
        for (int i = 0; i < nsNodes.getLength(); i++) {
            final Node node = nsNodes.item(i);
            if (node instanceof Element) {
                final Element nsElement = (Element) node;
                namespaceToUriMap.put(nsElement.getAttribute("prefix"), nsElement.getAttribute("uri"));
            }
        }
        return namespaceToUriMap;
    }
}
