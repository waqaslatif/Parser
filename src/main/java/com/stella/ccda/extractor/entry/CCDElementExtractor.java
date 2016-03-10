package com.stella.ccda.extractor.entry;

import java.text.ParseException;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

public interface CCDElementExtractor {
	
	/**
	 * Extracts the discrete information from CCD document element and 
	 * compose a sql script for the element.
	 * @param document XML Document Object
	 * @return SQL Script composed from extracting CCD Element
	 * @throws XPathExpressionException if invalid XPath is used for parsing the element.
	 * @throws ParseException
	 */
	String extract(Document document)throws ParseException, XPathExpressionException;

}
