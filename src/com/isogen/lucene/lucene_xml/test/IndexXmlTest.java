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

import junit.framework.TestCase;
import com.isogen.lucene.lucene_xml.*;
import org.apache.lucene.search.Hits;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class IndexXmlTest extends TestCase
{

    String newIndexPath = "";

    public IndexXmlTest(String name)
    {
        super(name);
    }

    public void testXmlIndexing()
    {
        XMLSandRManager mgr = new XMLSandRManager();
        XMLIndexer indexer = mgr.getIndexer();
        mgr.initializeIndex();
        String docDir = "src/com/isogen/lucene/lucene_xml/test/testdocs/";
        try
        {
            indexer.indexNewDocument(docDir + "doc1.xml");
            indexer.indexNewDocument(docDir + "doc2.xml");
            indexer.indexNewDocument(docDir + "doc3.xml");
        }
        catch (IndexError e)
        {
            this.assertTrue("Indexer threw exception " + e.getMessage(), false);
        }
        Hits hits1 = null;
        XMLSearcher searcher = null;
        try
        {
            searcher = mgr.createXMLSearcher();
        }
        catch (SearchError e)
        {
            assertTrue("Got exception creating XMLSearcher: " + e.getMessage(),
                       false);
        }

        // Simple content search:
        try
        {
            hits1 = searcher.search("berries"); // NOTE: 'content' is default field
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits1 is null", hits1);
        //System.out.println("Got " + hits1.length() + " hits for 'berries' in content");
        assertTrue("Expected 8 hits, got " + hits1.length(),
                   hits1.length() == 8);

        try
        {
            Document doc = hits1.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("para") || gifld.equals("title") );
            String docidfld = doc.get("docid");
            assertNotNull(docidfld);
            for (int i = 0; i < hits1.length(); i++)
            {
                Document testdoc = hits1.doc(i);
                assertTrue(testdoc.get("docid").endsWith("doc1.xml") ||
                           testdoc.get("docid").endsWith("doc2.xml"));
            }
            String typefld = doc.get("nodetype");
            assertNotNull(typefld);
            assertTrue(typefld.equals("ELEMENT_NODE"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace()
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        // Test searching on attributes
        Hits hits2 = null;
        try
        {
            hits2 = searcher.search("att#language:en_ww");
//            hits2 = searcher.search("att#language:fr_ww");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits2 is null", hits2);
        //System.out.println("Got " + hits2.length() + " hits for 'en_ww' in language=");
        assertTrue("Expected 1 hit, got " + hits2.length(),
                   hits2.length() == 1);

        try
        {
            Document doc = hits2.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("mydoc"));
            String docidfld = doc.get("docid");
            assertNotNull(docidfld);
            assertTrue(docidfld.endsWith("doc1.xml"));
            String langfld = doc.get("att#language");
            assertNotNull(langfld);
            assertTrue(langfld.equals("en_ww"));
            String treelocfld = doc.get("treeloc");
            assertNotNull(treelocfld);
            assertTrue(treelocfld.equals("0"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        // Now try some contextual queries:
        Hits hits3 = null;
        try
        {
            hits3 = searcher.search("berries AND tagname:title");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits3 is null", hits3);
        //System.out.println("Got " + hits2.length() + " hits for 'en_ww' in language=");
        assertTrue("Expected 2 hits, got " + hits3.length(),
                   hits3.length() == 2);

        try
        {
            Document doc = hits3.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("title"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        Hits hits4 = null;
        try
        {
            hits4 = searcher.search("tagname:mydoc AND att#language:de_ww");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits4 is null", hits4);
        //System.out.println("Got " + hits2.length() + " hits for 'en_ww' in language=");
        assertTrue("Expected 1 hit, got " + hits4.length(),
                   hits4.length() == 1);

        try
        {
            Document doc = hits4.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("mydoc"));
            String langfld = doc.get("att#language");
            assertNotNull(langfld);
            assertTrue(langfld.equals("de_ww"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        //Search using path to root element field
        Hits hits5 = null;
        try
        {
            hits5 = searcher.search("sour AND tagname:strong AND ancestors:emphasis AND ancestors:para AND ancestors:mydoc");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits5 is null", hits5);
        //System.out.println("Got " + hits2.length() + " hits for 'en_ww' in language=");
        assertTrue("Expected 2 hits, got " + hits5.length(),
                   hits5.length() == 2);

        try
        {
            Document doc = hits5.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("strong"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        Hits hits6 = null;
        try
        {
            hits6 = searcher.search("sour AND tagname:strong AND ancestors:(\"mydoc chap para emphasis\")");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits6 is null", hits6);
        //System.out.println("Got " + hits2.length() + " hits for 'en_ww' in language=");
        assertTrue("Expected 1 hit, got " + hits6.length(),
                   hits6.length() == 1);

        try
        {
            Document doc = hits6.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("strong"));
            String treelocfld = doc.get("treeloc");
            assertNotNull(treelocfld);
            assertTrue("Expected '0 0 3 0 0', got " + treelocfld,
                       treelocfld.equals("0 0 3 0 0"));

        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        Hits hits7 = null;
        try
        {
            hits7 = searcher.search("piname:MyPi");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits7 is null", hits7);
        assertTrue("Expected 1 hit, got " + hits7.length(),
                   hits7.length() == 1);

        try
        {
            Document doc = hits7.doc(0);
            assertNotNull(doc);
            String namefld = doc.get("piname");
            assertNotNull(namefld);
            assertTrue(namefld.equals("MyPi"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        Hits hits8 = null;
        try
        {
            hits8 = searcher.search("pitext:page*");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits8 is null", hits8);
        assertTrue("Expected 1 hit, got " + hits8.length(),
                   hits8.length() == 1);

        try
        {
            Document doc = hits8.doc(0);
            assertNotNull(doc);
            String namefld = doc.get("piname");
            assertNotNull(namefld);
            assertTrue(namefld.equals("MyPi"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }


        // Currently, our DOM implementation is not returning comment nodes.
        Hits hits9 = null;
        try
        {
            hits9 = searcher.search("commenttext:replace");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits9 is null", hits9);
        assertTrue("Expected 1 hit, got " + hits9.length(),
                   hits9.length() == 1);

        try
        {
            Document doc = hits9.doc(0);
            assertNotNull(doc);
            String docidfld = doc.get("docid");
            assertNotNull(docidfld);
            assertTrue(docidfld.endsWith("doc1.xml"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }


        // Currently, our DOM implementation is not returning comment nodes.
        Hits hits10 = null;
        try
        {
            hits10 = searcher.search("apple AND nodetype:ALL_CONTENT");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits10 is null", hits10);
        assertTrue("Expected 3 hits, got " + hits10.length(),
                   hits10.length() == 3);

        try
        {
            Document doc = hits10.doc(0);
            assertNotNull(doc);
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        //Search for parent.
        //Assert that treelocs for two separate gi/parent elements are different
        Hits hits11 = null;
        Hits hits12 = null;
        try
        {
            hits11 = searcher.search("sour AND tagname:strong AND parent:emphasis");
            hits12 = searcher.search("sour AND tagname:strong AND parent:para");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits11 is null", hits11);
        assertTrue("Expected 1 hit, got " + hits11.length(),
                   hits11.length() == 1);
        assertNotNull("hits12 is null", hits12);
        assertTrue("Expected 1 hit, got " + hits12.length(),
                   hits12.length() == 1);

        try
        {
            Document doc = hits11.doc(0);
            assertNotNull(doc);
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("strong"));

            Document doc2 = hits12.doc(0);
            assertNotNull(doc2);
            String gifld2 = doc2.get("tagname");
            assertNotNull(gifld2);
            assertTrue(gifld2.equals("strong"));

            boolean isEquals = (doc2.get("treeloc") == doc.get("treeloc"));
            this.assertEquals(isEquals, false);
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        try
        {
            searcher.close();
        }
        catch (Throwable e)
        {
            assertTrue("Unexpected exception: " + e.getMessage(), false);
        }

    }


    /**
     * Tests both searching and storage of absolute child position indexing.
     */
    public void testAbsoluteChildPosSearch() {
        XMLSandRManager mgr = new XMLSandRManager();
        XMLIndexer indexer = mgr.getIndexer();
        mgr.initializeIndex();
        String docDir = "src/com/isogen/lucene/lucene_xml/test/testdocs/";
        try
        {
            indexer.indexNewDocument(docDir + "doc1.xml");
            indexer.indexNewDocument(docDir + "doc2.xml");
            indexer.indexNewDocument(docDir + "doc3.xml");
        }
        catch (IndexError e)
        {
            this.assertTrue("Indexer threw exception " + e.getMessage(), false);
        }
        XMLSearcher searcher = null;
        try
        {
            searcher = mgr.createXMLSearcher();
        }
        catch (SearchError e)
        {
            assertTrue("Got exception creating XMLSearcher: " + e.getMessage(),
                       false);
        }

        //tests the absolute child position
        Hits hits1 = null;
        Hits hits2 = null;
        try
        {
            hits1 = searcher.search("cairns AND tagname:para");
            hits2 = searcher.search("hounds AND tagname:para");

        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        assertNotNull("hits1 is null", hits1);
        assertTrue("Expected 1 hit, got " + hits1.length(),
                   hits1.length() == 1);
        assertNotNull("hits2 is null", hits2);
        assertTrue("Expected 1 hit, got " + hits2.length(),
                   hits2.length() == 1);

        try
        {
            Document doc = hits1.doc(0);
            Document doc2 = hits2.doc(0);
            assertNotNull(doc);
            assertNotNull(doc2);
            assertEquals("6", doc.get("childnum"));
            assertEquals("7", doc2.get("childnum"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        //tests searching for the absolute child position
        Hits hits3 = null;
        Hits hits4 = null;
        try
        {
            hits3 = searcher.search("tagname:chap AND att#note:draft AND parent:mydoc");
            hits4 = searcher.search("childnum:1 AND tagname:chap AND parent:mydoc");

        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits3 is null", hits3);
        assertTrue("Expected 1 hit, got " + hits3.length(),
                   hits3.length() == 1);
        assertNotNull("hits4 is null", hits4);
        assertTrue("Expected 3 hit, got " + hits4.length(),
                   hits4.length() == 3);

        try
        {
            Document doc = hits3.doc(0);
            assertNotNull(doc);

            Document doc2_1 = hits4.doc(0);
            assertNotNull(doc2_1);
            Document doc2_2 = hits4.doc(1);
            assertNotNull(doc2_2);
            Document doc2_3 = hits4.doc(2);
            assertNotNull(doc2_3);
            Document goalDoc = null;

            String note = doc2_1.get("att#note");
            if (note == null) {
              note = doc2_2.get("att#note");
              if (note == null) {
                note = doc2_3.get("att#note");
                assertNotNull("failed to find a noted chapter", note);
                if (note.equals("draft")) {
                  goalDoc = doc2_3;
                }
              } else {
                if (note.equals("draft")) {
                  goalDoc = doc2_2;
                }
              }
            } else {
              if (note.equals("draft")) {
                goalDoc = doc2_1;
              }
            }
            assertNotNull("failed to find the draft noted chapter", goalDoc);

            String abschild = doc.get("childnum");
            String abschild2 = goalDoc.get("childnum");
            assertEquals("1", abschild2);
            assertEquals(abschild, abschild2);
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        Hits hits5 = null;
        try
        {
            hits5 = searcher.search("tagname:mydoc AND att#author:'Brandon Jockman'");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertNotNull("hits5 is null", hits5);
        assertTrue("Expected 1 hit, got " + hits5.length(),
                   hits5.length() == 1);

        try
        {
            Document doc = hits5.doc(0);
            assertNotNull(doc);
            String abschild = doc.get("childnum");
            assertNotNull(abschild);
            assertTrue(abschild.equals("1"));
            String gifld = doc.get("tagname");
            assertNotNull(gifld);
            assertTrue(gifld.equals("mydoc"));
            String langfld = doc.get("att#language");
            assertNotNull(langfld);
            assertTrue(langfld.equals("fr_ww"));
        }
        catch (java.io.IOException e)
        {
            //e.printStackTrace();
            assertTrue("Exception getting doc(0) from hits: " + e.getClass() + ", " +
                       e.getMessage(), false);
        }

        try
        {
            searcher.close();
        }
        catch (Throwable e)
        {
            assertTrue("Unexpected exception: " + e.getMessage(), false);
        }
    }

    public void testAttributeIndexing()
    {
        XMLSandRManager mgr = new XMLSandRManager();
        XMLIndexer indexer = mgr.getIndexer();
        mgr.initializeIndex();
        String docDir = "src/com/isogen/lucene/lucene_xml/test/testdocs/";
        try
        {
            indexer.indexNewDocument(docDir + "doc1.xml");
        }
        catch (IndexError e)
        {
            this.assertTrue("Indexer threw exception " + e.getMessage(), false);
        }
        Hits hits1 = null;
        XMLSearcher searcher = null;
        try
        {
            searcher = mgr.createXMLSearcher();
        }
        catch (SearchError e)
        {
            assertTrue("Got exception creating XMLSearcher: " + e.getMessage(),
                       false);
        }

        try
        {
            hits1 = searcher.search("attributenames:language");
        }
        catch (Exception e)
        {
            assertTrue("Exception while searching " + e.getClass() + ", " +
                       e.getMessage(), false);
        }
        assertEquals(1, hits1.length());
        Document doc1 = null;
        try
        {
            doc1 = hits1.doc(0);
        }
        catch (java.io.IOException exc)
        {
            assertTrue("Exception getting doc(0): " +
                       exc.getMessage(), false);
        }

        try
        {
            searcher.close();
        }
        catch (Throwable e)
        {
            assertTrue("Unexpected exception: " + e.getMessage(), false);
        }

    }

    /**
     * Tests the getAbsoluteChildPosOfSelf() method of the XMLIndexer.
     */
    public void testGetAbsoluteChildPosOfSelf() {
        XMLSandRManager mgr = new XMLSandRManager();
        XMLIndexer indexer = mgr.getIndexer();
        String pos = null;
        String nodelist = "0";
        pos = indexer.getAbsoluteChildPosOfSelf(nodelist);
        assertNotNull(pos);
        assertEquals("1", pos);

        nodelist = "0 1 1 2";
        pos = indexer.getAbsoluteChildPosOfSelf(nodelist);
        assertNotNull(pos);
        assertEquals("3", pos);

        nodelist = "0 1 1 2 ";
        pos = indexer.getAbsoluteChildPosOfSelf(nodelist);
        assertNotNull(pos);
        assertEquals("0", pos);

        nodelist = " ";
        pos = indexer.getAbsoluteChildPosOfSelf(nodelist);
        assertNotNull(pos);
        assertEquals("0", pos);

        nodelist = "0 9";
        pos = indexer.getAbsoluteChildPosOfSelf(nodelist);
        assertNotNull(pos);
        assertEquals("10", pos);
    }

}
