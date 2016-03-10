package com.stella.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class Utils {

    private static final String DB_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String ENABLE_OUTPUT = "yes";
	private static final String DISABLE_OUTPUT = "no";
	private static final String XML_METHOD = "xml";
	private static final String UTF_8_ENCODING = "UTF-8";
	private static final String XSLT_INDENT_AMOUNT_URI = "{http://xml.apache.org/xslt}indent-amount";
	
	private static final String M2HID= "200";

    public static final String NEW_LINE = System.getProperty("line.separator");

    private Utils() {
        throw new UnsupportedOperationException("Utility class. Not intended to be intantiated.");
    }
    
    public static String getStringNode(final Node entry, final String xPath) throws XPathExpressionException {
        final XPathExpression sectionXpathExp = Utils.getXPathExpression(xPath);
        return sectionXpathExp.evaluate(entry, XPathConstants.STRING).toString();
    }
    
    public static XPathExpression getXPathExpression(final String xPath) throws XPathExpressionException {
    	final XPathFactory xPathfactory = XPathFactory.newInstance();
        final XPath xpath = xPathfactory.newXPath();
        final XPathExpression xpathExp = xpath.compile(xPath);
        return xpathExp;
    }

    public static String nodeToString(Node node) {
    	final StringWriter sw = new StringWriter();
    	try {
    	 final Transformer trans = TransformerFactory.newInstance().newTransformer();
    	 trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ENABLE_OUTPUT);
    	 trans.setOutputProperty(OutputKeys.INDENT, ENABLE_OUTPUT);
    	 trans.transform(new DOMSource(node), new StreamResult(sw));
    	} catch (TransformerException te) {
    		System.out.println("nodeToString Transformer Exception");
    	}
    	return sw.toString();
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, DISABLE_OUTPUT);
        transformer.setOutputProperty(OutputKeys.METHOD, XML_METHOD);
        transformer.setOutputProperty(OutputKeys.INDENT, ENABLE_OUTPUT);
        transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8_ENCODING);
        transformer.setOutputProperty(XSLT_INDENT_AMOUNT_URI, "4");

        transformer.transform(new DOMSource(doc), 
             new StreamResult(new OutputStreamWriter(out, UTF_8_ENCODING)));
    }

    public static String getM2hid() {
    	return M2HID;
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
        Date currentDate = Calendar.getInstance().getTime();
        return formatter.format(currentDate);
    }

    public static String formatStringtoDbDate(final String timeStamp, final String srcFormat) throws ParseException {
    	Date objDate = new SimpleDateFormat(DB_DATE_FORMAT, Locale.ENGLISH).parse(timeStamp);
    	SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
    	return formatter.format(objDate);
    }
}
