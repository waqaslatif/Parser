package com.stella.ccda.extractor.entry;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public interface CcdaEntryExtractor {
	
	String extractData(Node entry) throws XPathExpressionException;

}
