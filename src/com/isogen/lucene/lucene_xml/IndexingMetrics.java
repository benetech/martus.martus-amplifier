package com.isogen.lucene.lucene_xml;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.Hashtable;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.apache.lucene.document.Document;

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

/**
 * Holds metrics from an indexing request.
 */
public class IndexingMetrics {

  public String docid = null;          // The document ID these metrics are for
  public long totalTime = 0;           // Total time needed to index the document.
  public long luceneTime = 0;          // Total time taken by Lucene processing
  public long allContentTime = 0;      // Time take to index the all-content document
  public long domTime = 0;             // Total time taken by DOM processing
  public long totalDocsCreated = 0;    // Total number of Lucene documents created
  public long totalNodes = 0;          // Total number of DOM nodes processed
  public long numElements = 0;         // Number of element nodes processed
  public long numComments = 0;         // Number of comment nodes
  public long numPIs = 0;              // Number of PI nodes processed
  public long textLength = 0;          // Length of ALL_CONTENT text
  public Hashtable elemTypes = new Hashtable(); // { tagname : Dictionary of attributes }
  public Hashtable elemTypeCounts = new Hashtable(); // { tagname : Long }

  private Date totalStartTime = null;
  private Date domStartTime = null;
  private Date luceneStartTime = null;
  private Date allContentStartTime = null;

  public IndexingMetrics(String theDocid) {
    docid = theDocid;
  }

  public void startTotalTimer() {
    totalStartTime = new Date();
  }
  public void stopTotalTimer() {
    Date stopTime = new Date();
    if (totalStartTime != null) {
      totalTime = totalTime + getElapsedTime(totalStartTime, stopTime);
    } else {
      System.err.println("stopTotalTimer() called before startTotalTimer()");
    }
  }
  public void startLuceneTimer() {
    luceneStartTime = new Date();
  }
  public void stopLuceneTimer() {
    Date stopTime = new Date();
    if (luceneStartTime != null) {
      luceneTime = luceneTime + getElapsedTime(luceneStartTime, stopTime);
      luceneStartTime = null;
    } else {
      System.err.println("stopLuceneTimer() called before startLuceneTimer()");
    }
  }

  public void startDomTimer() {
    domStartTime = new Date();
  }
  public void stopDomTimer() {
    Date stopTime = new Date();
    if (domStartTime != null) {
      domTime = domTime + getElapsedTime(domStartTime, stopTime);
      domStartTime = null;
    } else {
      System.err.println("stopDomTimer() called before startDomTimer()");
    }
  }

  public void startAllContentTimer() {
    allContentStartTime = new Date();
  }
  public void stopAllContentTimer() {
    Date stopTime = new Date();
    if (allContentStartTime != null) {
      allContentTime = allContentTime + getElapsedTime(allContentStartTime, stopTime);
      allContentStartTime = null;
    } else {
      System.err.println("stopAllContentTimer() called before startAllContentTimer()");
    }
  }


  public long getElapsedTime(Date startTime, Date endTime) {
    long startMS = startTime.getTime();
    long endMS = endTime.getTime();
    if (endMS < startMS) {
      System.err.println("endTime " + endMS + " < startTime " + startMS);
    }
    long elapsed = endMS - startMS;
    return elapsed;
  }

  public void registerNode(Node theNode) {
    totalNodes++;
    switch (theNode.getNodeType()) {
    case Node.ELEMENT_NODE:
      numElements++;
      String tagname = ((Element)theNode).getNodeName();
      if (!elemTypes.containsKey(tagname)) {
        Hashtable atts = new Hashtable();
        elemTypes.put(tagname, atts);
      }
      if (!elemTypeCounts.containsKey(tagname)) {
        elemTypeCounts.put(tagname, new Long(1));
      } else {
        Long count = (Long)elemTypeCounts.get(tagname);
        elemTypeCounts.put(tagname, new Long((count.longValue()) + 1));
      }
      break;
    case Node.COMMENT_NODE:
      this.numComments++;
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      this.numPIs++;
      break;
    }
  }

  /**
   * Registers a text chunk added to the all_content doc
   *
   */
  public void registerText(String text) {
    textLength = textLength + text.length();
  }

  public void registerLuceneDoc(Document doc) {
    totalDocsCreated++;
  }

  public String formatTime(long milis) {
    String resultStr = "";
    if (milis < 1000) {
      resultStr = "0." + milis + " Seconds";
    } else {
      double seconds = milis/1000.0;
      if (seconds > 60) {
        int minutes = new Double(seconds/60).intValue();
        seconds = seconds - (minutes * 60);
        java.util.StringTokenizer tokens = new java.util.StringTokenizer(Double.toString(seconds), ".");
        String base = tokens.nextToken();
        String fract = tokens.nextToken();
        if ((fract != null) && (fract.length() > 3)) {
          fract = fract.substring(0,3);
        }
        resultStr = minutes + " Minutes, " + base + "." + fract + " Seconds";
      } else {
        resultStr = seconds + " Seconds";
      }
    }
    return resultStr;
  }

  public String toString() {
    String outstr = "Metrics for document ID '" + this.docid + "':";
    outstr = outstr + "\n\nTimes:";
    outstr = outstr + "\n\tTotal Time:\t\t" + formatTime(this.totalTime);
    outstr = outstr + "\n\tElement Indexing Time:\t" + formatTime(this.luceneTime);
    outstr = outstr + "\n\tContent Indexing Time:\t" + formatTime(this.allContentTime);
    outstr = outstr + "\n\tDOM processing Time:\t" + formatTime(this.domTime);
    outstr = outstr + "\n\nStatistics:";
    outstr = outstr + "\n\tTotal Nodes Indexed:\t" + this.totalNodes;
    outstr = outstr + "\n\tTotal Element Nodes:\t" + this.numElements;
    outstr = outstr + "\n\tTotal Comment Nodes:\t" + this.numComments;
    outstr = outstr + "\n\tTotal PI Nodes:\t" + this.numPIs;
    outstr = outstr + "\n\tTotal Lucene Docs:\t" + this.totalDocsCreated;
    outstr = outstr + "\n\tContent Text Size:\t" + this.textLength;

    return outstr;
  }

  /**
   * Returns Metric results in a Vector.
   * The order should be the same as the toString() output.
   */
  public Vector toVector() {
    Vector results = new Vector();
    results.addElement(new Long(this.totalTime));
    results.addElement(new Long(this.luceneTime));
    results.addElement(new Long(this.allContentTime));
    results.addElement(new Long(this.domTime));
    results.addElement(new Long(this.totalNodes));
    results.addElement(new Long(this.numElements));
    results.addElement(new Long(this.numComments));
    results.addElement(new Long(this.numPIs));
    results.addElement(new Long(this.totalDocsCreated));
    results.addElement(new Long(this.textLength));
    return results;
  }

  /**
   * Formats string output for the results Vector. (ie. toVector)
   * Result string is same as toString().
   */
  public String toString(Vector results) {
    String outstr = "Metrics:"; //docid not certain, may be 2+
    outstr = outstr + "\n\nTimes:";
    outstr = outstr + "\n\tTotal Time:\t\t" + formatTime(((Long)results.get(0)).longValue());
    outstr = outstr + "\n\tElement Indexing Time:\t" + formatTime(((Long)results.get(1)).longValue());
    outstr = outstr + "\n\tContent Indexing Time:\t" + formatTime(((Long)results.get(2)).longValue());
    outstr = outstr + "\n\tDOM processing Time:\t" + formatTime(((Long)results.get(3)).longValue());
    outstr = outstr + "\n\nStatistics:";
    outstr = outstr + "\n\tTotal Nodes Indexed:\t" + ((Long)results.get(4)).longValue();
    outstr = outstr + "\n\tTotal Element Nodes:\t" + ((Long)results.get(5)).longValue();
    outstr = outstr + "\n\tTotal Comment Nodes:\t" + ((Long)results.get(6)).longValue();
    outstr = outstr + "\n\tTotal PI Nodes:\t" + ((Long)results.get(7)).longValue();
    outstr = outstr + "\n\tTotal Lucene Docs:\t" + ((Long)results.get(8)).longValue();
    outstr = outstr + "\n\tContent Text Size:\t" + ((Long)results.get(9)).longValue();
    return outstr;
  }

  /**
   * Used to add a set of metrics from a toVector() call
   * to the current metrics object.
   *
   * Can be used to aid in totaling metrics information.
   */
  public void addMetrics(Vector metrics_v) {
    this.totalTime += ((Long)metrics_v.get(0)).longValue();
    this.luceneTime += ((Long)metrics_v.get(1)).longValue();
    this.allContentTime += ((Long)metrics_v.get(2)).longValue();
    this.domTime += ((Long)metrics_v.get(3)).longValue();
    this.totalNodes += ((Long)metrics_v.get(4)).longValue();
    this.numElements += ((Long)metrics_v.get(5)).longValue();
    this.numComments += ((Long)metrics_v.get(6)).longValue();
    this.numPIs += ((Long)metrics_v.get(7)).longValue();
    this.totalDocsCreated += ((Long)metrics_v.get(8)).longValue();
    this.textLength += ((Long)metrics_v.get(9)).longValue();
  }

}