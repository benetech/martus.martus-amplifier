package com.isogen.lucene.lucene_xml;
/**
 * Title:        Lucene XML Indexing Experiment
 * Description:
 * Copyright:    Copyright (c) 2001, 2002
 * Company:      ISOGEN International, LLC.
 * @author       Eliot Kimber & Brandon Jockman (Professional Services)
 * @version 1.0
 *
 * Permission to reproduce and make available for distribution during and
 * after the XML Europe 2002 Conference is granted.
 */
//----------------------------
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
//----------------------------
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.lucene.store.Directory;

//-------------------------

import java.util.Vector;

import org.w3c.dom.*;
import org.apache.xerces.parsers.DOMParser;

public class XMLIndexer
{

    String sep;
    String pathToIndex;

    public int max_field_length = 1000000; //The maximum length per Lucene field
    public int merge_factor = 10; //Factor determining how documents are merged

    static final String[] nodeTypeStrings =
        {
        "",
            "ELEMENT_NODE",
            "ATTRIBUTE_NODE",
            "TEXT_NODE",
            "CDATA_SECTION_NODE",
            "ENTITY_REFERENCE_NODE",
            "ENTITY_NODE",
            "PROCESSING_INSTRUCTION_NODE",
            "COMMENT_NODE",
            "DOCUMENT_NODE",
            "DOCUMENT_TYPE_NODE",
            "DOCUMENT_FRAGMENT_NODE",
            "NOTATION_NODE"
    };

    public XMLIndexer(String indexPath)
    {
        sep = java.io.File.separator;
        pathToIndex =  indexPath;
        pathToIndex = new java.io.File(pathToIndex).getAbsolutePath();
    }

    /**
     * Sets up the index so all other indexing just does appending.
     */
    public synchronized void initializeIndex(int indexId)
    {
        java.util.Vector docList = new java.util.Vector();
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();


        IndexWriter writer = null;
        try
        {
            writer = new IndexWriter(pathToIndex,
                                     new StemmingAnalyzer(),
                                     true);
            writer.maxFieldLength = this.max_field_length;
            writer.mergeFactor = this.merge_factor;
            writer.close();
        }
        catch (java.io.IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Indexes an XML document. Creates one Lucene Document for each XML element
     * in the input document.
     */
    public synchronized IndexingMetrics indexNewDocument(String docPath) throws IndexError
    {
        IndexWriter writer = null;

        IndexingMetrics metrics = new IndexingMetrics(docPath);
        metrics.startTotalTimer();
        try
        {
            metrics.startLuceneTimer();
            writer = new IndexWriter(pathToIndex,
                                     new StemmingAnalyzer(),
                                     false);
            writer.maxFieldLength = this.max_field_length;
            writer.mergeFactor = this.merge_factor;
            metrics.stopLuceneTimer();
        }
        catch (java.io.IOException e)
        {
            throw new IndexError("Error creating IndexWriter: " + e.getMessage());
        }

        try
        {
            metrics.startDomTimer();
            DOMParser dp = new DOMParser();
            try
            {
                dp.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
            catch (org.xml.sax.SAXException e)
            {
                e.printStackTrace();
            }
            try
            {
                dp.parse(docPath);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                throw new IndexError("Exception parsing document " + docPath +
                                     ": " + e.getMessage());
            }

            org.w3c.dom.Document inDom = dp.getDocument();
            String docType = "{undefined}";
            if (inDom.getDoctype() != null )
            {
                docType = inDom.getDoctype().getName();
            }
            metrics.stopDomTimer();

            metrics.startDomTimer();
            Element elem = inDom.getDocumentElement();
            metrics.stopDomTimer();
            String pathToRootElement = "";
            String docContent = indexNode(elem,
                                          writer,
                                          docPath,
                                          pathToRootElement,
                                          "0",
                                          metrics);

            // Now create the Lucene doc that holds all XML Document's text content:
            metrics.registerText(docContent);
            metrics.startAllContentTimer();
            org.apache.lucene.document.Document contentDoc = new org.apache.lucene.document.Document();
            contentDoc.add(new Field("content", docContent,
                                     false, true, true));
            contentDoc.add(new Field("docid", docPath, true, true, true));
            // NOTE: "ALL_CONTENT" is not a DOM node type--it's our convention for
            //       distinguishing the Lucene document that holds all the content.
            contentDoc.add(new Field("nodetype",
                                     "ALL_CONTENT", true, true, true));
            try
            {
                writer.addDocument(contentDoc);
                metrics.registerLuceneDoc(contentDoc);
            }
            catch (java.io.IOException e)
            {
                throw new IndexError("Error adding doc " + docPath +
                                     " to IndexWriter: " + e.getMessage());
            }
            metrics.stopAllContentTimer();
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (java.io.IOException e)
            {
                throw new IndexError("Error adding doc " + docPath +
                                     " to IndexWriter: " + e.getMessage());
            }
            metrics.stopTotalTimer();
        }
        return metrics;
    }

    /**
     * Indexes a single DOM node and its children (recursively).
     */
    private String indexNode(Node node,
                             IndexWriter writer,
                             String docId,
                             String ancestors,
                             String treelocOfNode,
                             IndexingMetrics metrics) throws IndexError
    {
        metrics.registerNode(node);
        String docContent = "";
        //System.out.println("Node Type=" + nodeTypeStrings[node.getNodeType()] +
        //                   ", treelocOfNode=" + treelocOfNode);
        //if (node.getNodeType() == 1) {
        //    System.out.println("\tTagname=" + ((Element)node).getTagName());
        //}

        metrics.startLuceneTimer();
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

        Field docid = new Field("docid", docId, true, true, true);
        Field ancestorsFld = new Field("ancestors",
                                       ancestors, true, true, true);

        int parentIndex = ancestors.lastIndexOf(" ");
        String parentName = ancestors.substring(parentIndex + 1);

        Field parentFld = new Field("parent",
                                    parentName, true, true, true);
        Field treelocFld = new Field("treeloc",
                                     treelocOfNode, true, false, false);

        String absChildPos = this.getAbsoluteChildPosOfSelf(treelocOfNode);
        Field absoluteChildPosFld = new Field("childnum",
                                                absChildPos, true, true, true);
        Field nodeType = new Field("nodetype",
                                   nodeTypeStrings[node.getNodeType()],
                                   true, true, true);
        doc.add(docid);
        doc.add(ancestorsFld);
        doc.add(parentFld);
        doc.add(absoluteChildPosFld);
        doc.add(treelocFld);
        doc.add(nodeType);
        metrics.stopLuceneTimer();

        String pathToRootForChild = ancestors;

        String contentStr = ""; // Accumulates direct Element content
        String contentFieldName = "content"; // Reset by node types other than Element
        metrics.startLuceneTimer();
        metrics.stopLuceneTimer();
        switch (node.getNodeType())
        {
        case Node.ELEMENT_NODE:
            metrics.startLuceneTimer();
            doc.add(new Field("tagname",
                              ((Element)node).getTagName(), true, true, true));
            doc.add(new Field("attributenames", "", true, true, true));
            metrics.stopLuceneTimer();
            metrics.startDomTimer();
            pathToRootForChild = ancestors + " " + ((Element)node).getTagName();
            metrics.stopDomTimer();
            break;
        case Node.TEXT_NODE:
            break;
        case Node.COMMENT_NODE:
            contentFieldName = "commenttext";
            metrics.startDomTimer();
            if (node.getNodeValue() != null)
            {
                contentStr = node.getNodeValue().trim();
            }
            metrics.stopDomTimer();
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            metrics.startDomTimer();
            String piName = ((ProcessingInstruction)node).getNodeName();
            metrics.stopDomTimer();
            if (piName != null)
            {
                metrics.startLuceneTimer();
                doc.add(new Field("piname",
                                  ((ProcessingInstruction)node).getNodeName(),
                                  true, true, true));
                metrics.stopLuceneTimer();
            }

            contentFieldName = "pitext";
            metrics.startDomTimer();
            if (node.getNodeValue() != null)
            {
                contentStr = node.getNodeValue().trim();
            }
            metrics.stopDomTimer();
            break;
        }

        metrics.startDomTimer();
        NamedNodeMap atts = node.getAttributes();
        metrics.stopDomTimer();
        if (atts != null)
        {
            String attNamesStr = "";
            for (int i = 0; i < atts.getLength(); i++)
            {
                //System.out.println("Indexing attribute " + atts.item(i).getNodeName() +
                //                   " with value '" + atts.item(i).getNodeValue() + "'");
                String attname = atts.item(i).getNodeName();
                //System.out.println("att#" + attname + atts.item(i).getNodeValue());
                attNamesStr = attNamesStr + " " + attname;
                metrics.startLuceneTimer();
                String attNamePrefix = "att#";
                doc.add(new Field(attNamePrefix + attname,
                                  atts.item(i).getNodeValue(),
                                  true, true, true));
                metrics.stopLuceneTimer();
            }
            doc.add(new Field("attributenames", attNamesStr, true, true, true));
        }


        metrics.startDomTimer();
        NodeList childs = node.getChildNodes();
        metrics.stopDomTimer();
        if (childs.getLength() > 0)
        {
            // Note - Other XML node types can be handled as well.
            //System.out.println(elem.getTagName() + ": Recursing over children, treeloc=" + treelocOfElem);
            for (int i = 0; i < childs.getLength(); i++)
            {
                metrics.startDomTimer();
                Node item = childs.item(i);
                metrics.stopDomTimer();
                //System.out.println("i=" + String.valueOf(i) + ", nodeType=" +
                //                   item.getNodeType());
                String tempStr = "";
                switch (item.getNodeType())
                {
                case Node.ELEMENT_NODE:
                case Node.COMMENT_NODE:
                case Node.PROCESSING_INSTRUCTION_NODE:
                    tempStr = this.indexNode(item,
                                             writer,
                                             docId,
                                             pathToRootForChild,
                                             treelocOfNode + " " + String.valueOf(i),
                                             metrics);
                    if (item.getNodeType() == node.ELEMENT_NODE)
                    {
                        if (docContent.equals(""))
                        {
                            docContent = tempStr;
                        }
                        else
                        {
                            docContent = docContent + " " + tempStr;
                        }
                    }
                    break;
                case Node.TEXT_NODE:
                    if (!item.getNodeValue().equals("\n")) {
                      metrics.registerNode(item);
                      metrics.startDomTimer();
                      if (contentStr.equals(""))
                      {
                          contentStr = item.getNodeValue();
                      }
                      else
                      {
                          contentStr = contentStr + " " + item.getNodeValue();
                      }
                      metrics.stopDomTimer();
                    }
                    tempStr = contentStr.trim();
                    if (!tempStr.equals(""))
                    {
                        if (docContent.equals(""))
                        {
                            docContent = tempStr;
                        }
                        else
                        {
                            docContent = docContent + " " + tempStr;
                        }
                    }
                }
            }
        }
        if (!contentStr.equals(""))
        {
            metrics.startLuceneTimer();
            Field content = new Field(contentFieldName,
                                      contentStr, false, true, true);
            doc.add(content);
            metrics.stopLuceneTimer();
        }
        try
        {
            metrics.startLuceneTimer();
            writer.addDocument(doc);
            metrics.stopLuceneTimer();
            metrics.registerLuceneDoc(doc);
        }
        catch (java.io.IOException e)
        {
            throw new IndexError("IndexElement: I/O Error adding document to index: " + e.getMessage());
        }
        return docContent;
    }

    /**
     * Returns the absolute child position of the node whose treeloc is
     * passed in.
     *
     * Eg: if treeloc is 0 1 0 the a.c.p is 1 as it is the first child of its
     * parent.
     *
     * If the position cannot be determined (stringified) 0 is returned.
     */
    public String getAbsoluteChildPosOfSelf(String treelocOfNode) {
        int absChildIndex = treelocOfNode.lastIndexOf(" ");
        String absChildPos = treelocOfNode.substring(absChildIndex + 1);
        if ((absChildPos != null) && (!absChildPos.equals(""))) {
           int tempInt = new Integer(absChildPos).intValue() + 1;
           absChildPos = "" + tempInt;
        } else {
           absChildPos = "" + 0;
        }
        return absChildPos;
    }
}
