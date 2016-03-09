package com.stella.ccda.extractor.entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ActiveProblemExtractor implements CcdaEntryExtractor {
	
	private static final String ACTIVE_PROBLEM_ID = "2.16.840.1.113883.10.20.22.4.3";
	
	public String extractData(final Node entry) {
		
		System.out.println("\nCurrent Element :" + entry.getNodeName());
		
		if (entry.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) entry;

			System.out.println("Staff id : " + eElement.getAttribute("classCode"));
			System.out.println("First Name : " + eElement.getAttribute("EVN"));

		}
		return null;
	}
	
	public String getSql(final Node entry) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String extractActiveProblem(final Document doc) throws XPathExpressionException { 
		final CcdaEntryExtractor activeProblemExtractor = new ActiveProblemExtractor();
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("//act[templateId/@root='" + ACTIVE_PROBLEM_ID + "']");
		
		NodeList nList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		System.out.println("NList" + nList.toString() + "|" + nList.getLength());
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			System.out.println(nNode.toString());
			String sql  = activeProblemExtractor.extractData(nNode);
		}
		
		return "";
	}

}
