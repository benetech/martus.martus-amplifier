package com.isogen.lucene.lucene_xml.test;

import junit.framework.TestCase;
import com.isogen.lucene.lucene_xml.IndexingMetrics;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

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

public class IndexMetricsTest extends TestCase {

  public IndexMetricsTest(String name) {
      super(name);
  }

  public void testNodeCounters() {
      String docDir = "src/com/isogen/lucene/lucene_xml/test/testdocs/";
      String docid_1 = "doc-one";
      IndexingMetrics metrics = new IndexingMetrics(docid_1);
      DOMParser dp = new DOMParser();
      try {
        dp.parse(docDir + "doc1.xml");
      } catch (Throwable exc) {
        this.assertTrue("Failed to parse doc1.xml: " + exc.getMessage(), false);
      }
      Document dom = dp.getDocument();
      Element docElem = dom.getDocumentElement();
      long totalNodes = 0;
      long numElems = 0;
      metrics.registerNode(docElem);
      totalNodes++;
      numElems++;
      assertEquals(numElems, metrics.numElements);
      assertEquals(totalNodes, metrics.totalNodes);
      NodeList nl = docElem.getChildNodes();
      java.util.Vector gis = new java.util.Vector();
      for (int i = 0; i < nl.getLength(); i++) {
        Node candNode = nl.item(i);
        if (candNode.getNodeType() == Node.ELEMENT_NODE) {
          numElems++;
          gis.addElement(candNode.getNodeName());
        }
        metrics.registerNode(candNode);
        totalNodes++;
      }
      assertEquals(numElems,metrics.numElements);
      assertEquals(totalNodes,metrics.totalNodes);
      assertEquals(1,metrics.numComments);

      for (int i = 0; i < gis.size(); i++) {
        String gi = (String)gis.get(i);
        assertTrue("Failed to find gi " + gi,
                   metrics.elemTypes.containsKey(gi));
      }

      assertEquals(2, ((Long)metrics.elemTypeCounts.get("chap")).longValue());

  }

  public void testTimers() {
      String docid_1 = "doc-one";
      IndexingMetrics metrics = new IndexingMetrics(docid_1);

      metrics.startTotalTimer();
      metrics.startDomTimer();
      try {
        new java.lang.Thread().sleep(100);
      } catch (java.lang.InterruptedException exc) {
        // nothing to do
      } catch (Throwable exc) {
        exc.printStackTrace();
        assertTrue("Unexpected exception: " + exc.getClass() + ": " + exc.getMessage(), false);
      }
      metrics.stopDomTimer();
      assertTrue("Expected >= 100, got " + metrics.domTime, metrics.domTime >= 100);

      metrics.startLuceneTimer();
      try {
        new java.lang.Thread().sleep(150);
      } catch (java.lang.InterruptedException exc) {
        // nothing to do
      } catch (Throwable exc) {
        exc.printStackTrace();
        assertTrue("Unexpected exception: " + exc.getClass() + ": " + exc.getMessage(), false);
      }
      metrics.stopLuceneTimer();
      assertTrue("Expected >= 150, got " + metrics.luceneTime, metrics.luceneTime >= 150);

      metrics.startLuceneTimer();
      try {
        new java.lang.Thread().sleep(50);
      } catch (java.lang.InterruptedException exc) {
        // nothing to do
      } catch (Throwable exc) {
        exc.printStackTrace();
        assertTrue("Unexpected exception: " + exc.getClass() + ": " + exc.getMessage(), false);
      }
      metrics.stopLuceneTimer();
      assertTrue("Expected >= 200, got " + metrics.luceneTime, metrics.luceneTime >= 200);

      metrics.startAllContentTimer();
      try {
        new java.lang.Thread().sleep(170);
      } catch (java.lang.InterruptedException exc) {
        // nothing to do
      } catch (Throwable exc) {
        exc.printStackTrace();
        assertTrue("Unexpected exception: " + exc.getClass() + ": " + exc.getMessage(), false);
      }
      metrics.stopAllContentTimer();
      assertTrue("Expected >= 170, got " + metrics.allContentTime,
                 metrics.allContentTime >= 170);
      metrics.stopTotalTimer();
      long timeSum = metrics.domTime + metrics.luceneTime + metrics.allContentTime;
      assertTrue("Expected total time > " + timeSum + ", got " + metrics.totalTime,
                 metrics.totalTime >= timeSum);
      //String temp = metrics.toString();
      //System.out.println(temp);
  }

  public void testTextLength() {
      String docid_1 = "doc-one";
      IndexingMetrics metrics = new IndexingMetrics(docid_1);
      assertEquals(0, metrics.textLength);
      metrics.registerText("123456789");
      assertEquals(9, metrics.textLength);
      metrics.registerText("12345");
      assertEquals(14, metrics.textLength);
  }

  public void testDocCount() {
      String docid_1 = "doc-one";
      IndexingMetrics metrics = new IndexingMetrics(docid_1);
      assertEquals(0, metrics.totalDocsCreated);
      metrics.registerLuceneDoc(new org.apache.lucene.document.Document());
      assertEquals(1, metrics.totalDocsCreated);
      metrics.registerLuceneDoc(new org.apache.lucene.document.Document());
      assertEquals(2, metrics.totalDocsCreated);

  }


  public void testFormatTime() {
      IndexingMetrics metrics = new IndexingMetrics("foo");
      String timeStr = metrics.formatTime(734);
      this.assertEquals("0.734 Seconds", timeStr);
      timeStr = metrics.formatTime(1734);
      this.assertEquals("1.734 Seconds", timeStr);
      timeStr = metrics.formatTime(61734);
      this.assertEquals("1 Minutes, 1.734 Seconds", timeStr);

  }

}