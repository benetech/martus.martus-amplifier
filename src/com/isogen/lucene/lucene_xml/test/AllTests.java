package com.isogen.lucene.lucene_xml.test;

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

import com.isogen.lucene.lucene_xml.test.IndexXmlTest;
import com.isogen.lucene.lucene_xml.test.IndexMetricsTest;
import com.isogen.lucene.lucene_xml.test.IndexMaintenanceTest;
import junit.framework.*;

public class AllTests {

  public static Test suite() {
    TestSuite suite= new TestSuite("All JUnit Tests");
    suite.addTest(new TestSuite(com.isogen.lucene.lucene_xml.test.IndexXmlTest.class));
    suite.addTest(new TestSuite(com.isogen.lucene.lucene_xml.test.IndexMetricsTest.class));
    suite.addTest(new TestSuite(com.isogen.lucene.lucene_xml.test.IndexMaintenanceTest.class));
    return suite;
  }

  public static void main(java.lang.String[] args) {
      junit.textui.TestRunner.run(suite());
  }

}




