package apps.stella.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML Utilities.
 *
 * @author Ravi Luthra
 */
public final class XMLUtilities {

    // For portability on different systems
    public static final String NEW_LINE = System.getProperty("line.separator");

    private XMLUtilities() {}

    /**
     * Perform an XSLT Transformation on the given document using the given stylesheet and URI resolver
     *
     * @param document The document to transform
     * @param stylesheet The stylesheet that will perform the transformation
     * @param uriResolver The URI Resolver to locate xslt includes/import elements. This may be null if there are no
     * includes.
     *
     * @return The output (transformed) document.
     */
    public static Document xslt1(final Node document, final Node stylesheet, final URIResolver uriResolver) {

        try {
            // Use a Transformer for output
            final TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
            if (uriResolver != null) {
                tFactory.setURIResolver(uriResolver);
            }
            final Source stylesource = new DOMSource(stylesheet);
            final Transformer transformer = tFactory.newTransformer(stylesource);
            if (uriResolver != null) {
                transformer.setURIResolver(uriResolver);
            }
            final DOMSource source = new DOMSource(document);
            final DOMResult result = new DOMResult();

            transformer.transform(source, result);
            return (Document) result.getNode();
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalArgumentException("Failed to load XSLT. " + ex.getMessageAndLocation(), ex);
        } catch (final TransformerException ex) {
            throw new IllegalArgumentException("Failed to transform document. " + ex.getMessageAndLocation(), ex);
        }
    }

    /**
     * Creates a DocumentBuilder that is namespace aware.
     *
     * @return A namespace aware document builder.
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder();
    }

    /**
     * Load a classpath resource that is an XML document and return the DOM Document.
     *
     * @param classPathResource The path (within the classpath) of the resource to load.
     * @return A DOM Document of the classpath resource.
     */
    public static Document parseResource1(final String classPathResource) {
        try {
            final DocumentBuilder builder = createDocumentBuilder();
            try (final InputStream is = XMLUtilities.class.getResourceAsStream(classPathResource)) {
                return builder.parse(is);
            }
        } catch (final RuntimeException | ParserConfigurationException | IOException | SAXException ex) {
            throw new IllegalStateException("Failed to parse classpath resource " + classPathResource, ex);
        }
    }

    /**
     * Parses the given File as XML and returns the XML Document DOM
     *
     * @param file The XML file to load
     * @return The DOM representation of the File
     */
    public static Document parseFile(final File file) {
        try {
            final DocumentBuilder builder = createDocumentBuilder();
            return builder.parse(file);
        } catch (final ParserConfigurationException | SAXException | IOException ex) {
            throw new IllegalStateException("Failed to parse file " + file, ex);
        }
    }

    /**
     * Returns the String representation of the given XML DOM document (the XML string)
     *
     * @param document The document to convert to a string
     * @return A String of XML
     */
    public static String toString(final Document document) {
        try {
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (final IllegalArgumentException | TransformerException ex) {
            throw new IllegalArgumentException("Failed to convert document to XML text", ex);
        }
    }

    /**
     * Parse a given String as an XML document.
     *
     * @param xmlString
     * @return
     */
    public static Document parseString(final String xmlString) {
        if (xmlString == null) {
            throw new IllegalArgumentException("XML String to parse is null.");
        }
        try {
            final DocumentBuilder builder = createDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString.trim())));
        } catch (final ParserConfigurationException | SAXException | IOException ex) {
            throw new IllegalStateException("Failed to parse XML: " + xmlString, ex);
        }
    }
    
    /**
     * Utility method to convert a String to an XML document (DOM) which is parsed using the standard Java
     * DocumentBuildFactory.netInstance() method with namespace awareness enabled. (Note that overriding XML
     * DocumentBuilderFactory instances may be in the classpath.)
     *
     * @param xmlStr The XML string to parse.
     * @return The DOM document object that results or an Exception if the string is not parseable for any reason.
     * @throws org.xml.sax.SAXException For errors parsing the XML document.
     */
    public static Document convertStringToDocument(final String xmlStr) throws SAXException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (final IOException | ParserConfigurationException ex) {
            throw new IllegalArgumentException("Failed to convert to XML document " + xmlStr, ex);
        }
    }

    /**
     * Convert a Node to a Document
     *
     * @param node Node
     * @return Document with node
     */
    public static Document nodeToDocument(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("Node is null.");
        }
        final DocumentBuilder builder;
        try {
            builder = createDocumentBuilder();
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException("Failed to create DocumentBuilder to convert node to document.", ex);
        }
        final Document document = builder.newDocument();
        final Node importedNode = document.importNode(node, true);
        document.appendChild(importedNode);
        return document;
    }

    /**
     * Get the XML, as a String, from a String that is formatted with HTTP (or other) headers before the XML
     * part. This method assumes that the headers are separated from the XML body by two line breaks.
     * @param withXml String containing XML separated from headers by two line breaks. 
     * @return XML portion of the String, starting with "<" after two line breaks. If two line breaks followed
     * by "<" are not present, returns the original String.
     */
    public static String getXmlPart(final String withXml) {
        if (withXml == null || withXml.startsWith("<")) {
            return withXml;
        }
        //Format of logged XML message with HTTP header info is two newlines before XML message
        final String breakString = NEW_LINE + NEW_LINE + "<";
        final int breakIndex = withXml.indexOf(breakString);
        if (breakIndex < breakString.length()) {
            return withXml;
        }
        return withXml.substring(breakIndex + breakString.length() - 1);
    }

    /**
     * Converts the list of provided nodes to String
     * @param nodes
     * @return xml String of nodes
     */
    public static String nodeToString(final List<Node> nodes) throws TransformerException {
        final StringBuilder xmlString  = new StringBuilder();
        final DOMSource source= new DOMSource();
        final StringWriter sw = new StringWriter();
        final StreamResult result = new StreamResult(sw);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        for (final Node node : nodes) {
            source.setNode(node);
            transformer.transform(source, result);
        }
        xmlString.append(sw.toString());
        return xmlString.toString();
    }
    
    /**
     * Converts the provided node to String
     * @param node
     * @return xml String of node
     */
    public static String nodeToString(final Node node) throws TransformerException {
    	if(node == null) {
    		throw new IllegalArgumentException("Node is null.");
    	}
    	
        final DOMSource source= new DOMSource();
        final StringWriter sw = new StringWriter();
        final StreamResult result = new StreamResult(sw);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        source.setNode(node);
        transformer.transform(source, result);
        return sw.toString();
    }

}
