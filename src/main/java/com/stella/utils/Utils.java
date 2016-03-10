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
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class Utils {

    private static final String DB_DATE_FORMAT = "yyyy-MM-ddThh:mm:ss";
    // For portability on different systems

    public static final String NEW_LINE = System.getProperty("line.separator");

    private Utils() {
        throw new UnsupportedOperationException("Utility class. Not intended to be intantiated.");
    }

    public static XPathExpression getXPathExpression(final String xPath) throws XPathExpressionException {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        XPathExpression xpathExp = xpath.compile(xPath);

        return xpathExp;
    }

    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    public static String getM2hid() {
        return "200";
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
        Date currentDate = Calendar.getInstance().getTime();
        return formatter.format(currentDate);
    }

    public static String formatStringtoDbDate(final String timeStamp, final String srcFormat) throws ParseException {
        Date objDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(timeStamp);
        SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
        return formatter.format(objDate);
    }
}
