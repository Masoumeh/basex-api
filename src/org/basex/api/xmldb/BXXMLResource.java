package org.basex.api.xmldb;

import org.basex.BaseX;
import org.basex.data.XMLSerializer;
import org.basex.io.CachedOutput;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.basex.data.Nodes;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Implementation of the XMLResource Interface for the XMLDB:API
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Andreas Weiler
 */
public class BXXMLResource implements XMLResource {
  /** Current node context. */
  Nodes nodes;
  /** String content. */
  Object content;

  /**
   * Standard constructor.
   * @param n nodes
   */
  public BXXMLResource(Nodes n) {
    nodes = n;
  }

  /**
   * @see org.xmldb.api.base.Resource#getContent()
   */
  public Object getContent() {
    if(content == null) {
      try {
        final CachedOutput out = new CachedOutput();
        final boolean chop = nodes.data.meta.chop;
        nodes.serialize(new XMLSerializer(out, false, chop));
        content = out.toString();
      } catch(final Exception ex) {
        BaseX.debug(ex);
      }
    }
    return content;
  }

  /**
   * @see org.xmldb.api.modules.XMLResource#getContentAsDOM()
   */
  public Node getContentAsDOM() {
    if(content != null) getContent();
    // <AW> ...process content (see eXist)
    
    try {
      // Create a builder factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);

      // Create the builder and parse the file
      Document doc = factory.newDocumentBuilder().parse(
          new File(nodes.data.meta.file.toString()));
      return doc;
    } catch(SAXException e) {
      // A parsing error occurred; the xml input is not valid
    } catch(ParserConfigurationException e) {} catch(IOException e) {}
    return null;
  }

  /**
   * @see org.xmldb.api.modules.XMLResource#getContentAsSAX(org.xml.sax.ContentHandler)
   */
  public void getContentAsSAX(ContentHandler handler) throws XMLDBException {
    if(content != null) getContent();
    // <AW> ...process content (see eXist)

    XMLReader reader = null;
    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    saxFactory.setNamespaceAware(true);
    saxFactory.setValidating(false);
    try {
      SAXParser sax = saxFactory.newSAXParser();
      reader = sax.getXMLReader();
    } catch(ParserConfigurationException pce) {
      throw new XMLDBException(1, pce.getMessage());
    } catch(SAXException saxe) {
      saxe.printStackTrace();
      throw new XMLDBException(1, saxe.getMessage());
    }
    try {
      reader.setContentHandler(handler);
      reader.parse(new InputSource(new FileInputStream(new File(
          nodes.data.meta.file.toString()))));
    } catch(SAXException saxe) {
      saxe.printStackTrace();
      throw new XMLDBException(1, saxe.getMessage());
    } catch(IOException ioe) {
      throw new XMLDBException(1, ioe.getMessage());
    }
  }

  /**
   * @see org.xmldb.api.modules.XMLResource#getDocumentId()
   */
  public String getDocumentId() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.xmldb.api.base.Resource#getId()
   */
  public String getId() {
    return nodes.data.meta.dbname;
  }

  /**
   * @see org.xmldb.api.base.Resource#getParentCollection()
   */
  public Collection getParentCollection() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.xmldb.api.base.Resource#getResourceType()
   */
  public String getResourceType() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @see org.xmldb.api.base.Resource#setContent(java.lang.Object)
   */
  public void setContent(Object value) {
    content = value;
  } 

  /**
   * @see org.xmldb.api.modules.XMLResource#setContentAsDOM(org.w3c.dom.Node)
   */
  public void setContentAsDOM(Node content) {
  // TODO Auto-generated method stub

  }

  /**
   * @see org.xmldb.api.modules.XMLResource#setContentAsSAX()
   */
  public ContentHandler setContentAsSAX() {
    // TODO Auto-generated method stub
    return null;
  }
}