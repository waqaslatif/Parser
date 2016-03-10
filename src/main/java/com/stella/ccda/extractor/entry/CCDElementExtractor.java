package com.stella.ccda.extractor.entry;

import java.text.ParseException;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

public interface CCDElementExtractor {
	
	String extract(Document document)throws XPathExpressionException, ParseException;

}
