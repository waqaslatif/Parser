package com.stella.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
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
import javax.xml.xpath.XPathVariableResolver;

import org.apache.xpath.jaxp.XPathFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XPath utilities creates XPath objects with sensible defaults. There are some methods that simplify the common
 * namespaces for convenience.
 *
 * @author Ravi Luthra
 * @author Corrina Burnley
 */
public final class Utils {
    
    private static final String EVALUATION_ERROR_MESSAGE = "Unable evaluate XPath expression.";
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

    /**
     * Creates an XPathFactory using the default instance type (DOM tree).
     *
     * @return An XPath Factory for
     */
    public static XPathFactory newInstance() {
        return new XPathFactoryImpl();
    }

    /**
     * Returns a new XPath instance with the given namespace context. 
     *
     * @param namespaceContext The namespace context to apply to the XPath object.
     * @return An XPath object with the given namespace context applied.
     */
    public static XPath newXPath(final NamespaceContext namespaceContext) {
        final XPathFactory xPathFactory = newInstance();
        final XPath xpath = xPathFactory.newXPath();
        xpath.setNamespaceContext(namespaceContext);
        xpath.setXPathVariableResolver(getXmlSanitizingVariableResolver());
        return xpath;
    }

    /**
     * @return Return an XPath instance with the default DOM model.
     */
    public static XPath newXPath() {
        final XPathFactory xPathFactory = newInstance();
        final XPath xpath = xPathFactory.newXPath();
	    xpath.setXPathVariableResolver(getXmlSanitizingVariableResolver());
        return xpath;
    }

    /**
     * Get the value at a specified XPath location in the document.
     * @param namespaceToUriMap Map of namespace prefixes to namespace URIs for namespaces used in the XPath
     * @param documentString XML document as a String. If the document does not start with "<", the method will
     * attempt to find "\n\n<".
     * @param xPathString String of XPath to use
     * @return Result of the XPath expression as a String
     */
    public static String extractValue(final Map<String, String> namespaceToUriMap, final String documentString,
            final String xPathString) {
        final Document document = XMLUtilities.parseString(XMLUtilities.getXmlPart(documentString));
        return extractValue(namespaceToUriMap, document, xPathString);
    }   

    /**
     * Get the value at a specified XPath location in the document.
     * @param namespaceToUriMap Map of namespace prefixes to namespace URIs for namespaces used in the XPath
     * @param node XML node (including Document)
     * @param xPathString String of XPath to use
     * @return Result of the XPath expression as a String
     */
    public static String extractValue(final Map<String, String> namespaceToUriMap, final Node node,
            final String xPathString) {
        final XPath xPath = newXPath(getNamespaceContext(namespaceToUriMap));
        try {
            final XPathExpression expr = xPath.compile(xPathString);
            return (String) expr.evaluate(node, XPathConstants.STRING);
        } catch (final XPathExpressionException ex) {
            throw new IllegalArgumentException(EVALUATION_ERROR_MESSAGE, ex);
        }
        
    }

    /**
     * Get the values at a specified XPath location in the document.
     * @param namespaceToUriMap Map of namespace prefixes to namespace URIs for namespaces used in the XPath
     * @param documentString XML document as a String. If the document does not start with "<", the method will
     * attempt to find "\n\n<".
     * @param xPathString String of XPath to use
     * @return Result of the XPath expression as a List of Nodes
     */
    public static List<Node> extractValues(final Map<String, String> namespaceToUriMap, final String documentString,
            final String xPathString) {
        final Document document = XMLUtilities.parseString(XMLUtilities.getXmlPart(documentString));
        return extractValues(namespaceToUriMap, document, xPathString);
    }   
    
    /**
     * Get the values at a specified XPath location in the document.
     * @param namespaceToUriMap Map of namespace prefixes to namespace URIs for namespaces used in the XPath
     * @param node XML node (including Document)
     * @param xPathString String of XPath to use
     * @return Result of the XPath expression as a List of Nodes
     */
    public static List<Node> extractValues(final Map<String, String> namespaceToUriMap, final Node node,
            final String xPathString) {
        final XPath xPath = newXPath(getNamespaceContext(namespaceToUriMap));
        final NodeList nodeList;
        try {
            final XPathExpression expr = xPath.compile(xPathString);
            nodeList = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
        } catch (final XPathExpressionException ex) {
            throw new IllegalArgumentException(EVALUATION_ERROR_MESSAGE, ex);
        }
        final int nodeListLength = nodeList.getLength();
        final List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeListLength; i++) {
            final Node nodeItem  = nodeList.item(i);
            if (nodeItem != null) {
                nodes.add(nodeList.item(i));
            }
        }
        return nodes;
    }   
    
    /**
     * Get the values at a specified XPath location in a list of nodes.
     * @param namespaceToUriMap Map of namespace prefixes to namespace URIs for namespaces used in the XPath
     * @param nodeList XML NodeList
     * @param xPathString String of XPath to use
     * @return Result of the XPath expression as a List of Nodes
     */
    public static List<Node> extractValuesFromNodeList(
            final Map<String, String> namespaceToUriMap, final NodeList nodeList, final String xPathString) {
        final XPath xPath = newXPath(getNamespaceContext(namespaceToUriMap));
        final List<Node> nodes = new ArrayList<>();

        final int nodeListLength = nodeList.getLength();
        for (int i = 0; i < nodeListLength; i++) {
            final Node node = nodeList.item(i);
            final NodeList nodeListResult;
            try {
                final XPathExpression expr = xPath.compile(xPathString);
                nodeListResult = (NodeList) expr.evaluate(node, XPathConstants.NODESET);
            } catch (final XPathExpressionException ex) {
                throw new IllegalArgumentException(EVALUATION_ERROR_MESSAGE, ex);
            }
            final int nodeListResultLength = nodeListResult.getLength();
            for (int j = 0; j < nodeListResultLength; j++) {
                nodes.add(nodeListResult.item(j));
            }            
        }
        
        
        return nodes;
    }   

    /**
     * Get a NamespaceContext object based on a map of namespace prefixes to namespace URIs.
     * @param namespaceToUriContext Map of namespace prefixes to namespace URIs
     * @return NamespaceContext containing the mapped namespace prefixes and URIs
     */
    public static NamespaceContext getNamespaceContext(final Map<String, String> namespaceToUriContext) {
        return new NamespaceContext() {

            /**Get the namespace URI for the specified prefix.*/
            @Override
            public String getNamespaceURI(final String prefix) {
                return namespaceToUriContext.get(prefix);
            }
            /**Get the prefix for a specified namespace URI.*/
            @Override
            public String getPrefix(final String namespaceURI) {
                return null;
            }

            /**Get the prefixes as an Iterator for a specified namespace URI. */
            @Override
            public Iterator<?> getPrefixes(final String namespaceURI) {return null;} };
    }
    
    public static XPathVariableResolver getXmlSanitizingVariableResolver() {
    	final XmlSanitizingVariableResolver resolver = new XmlSanitizingVariableResolver("[a-zA-Z0-9]{1,15}");
	    return resolver;
    }
}
