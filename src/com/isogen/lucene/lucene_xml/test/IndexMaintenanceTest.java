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

public class IndexMaintenanceTest extends TestCase {

    public IndexMaintenanceTest(String name) {
      super(name);
     }

  public void removeDirectory(String path) {
      java.io.File dirToKill = new java.io.File(path);
      //System.out.println("fullpath=" + dirToKill.getAbsolutePath());
      if (dirToKill.exists()) {
        //System.out.println(newIndexPath + " Exists, deleting it");
        int numFiles = dirToKill.listFiles().length;
        //System.out.println("numFiles=" + numFiles);
        java.io.File[] files = dirToKill.listFiles();
        for (int i = 0; i < numFiles; i++) {
          java.io.File fileToKill = files[i];
          //System.out.println("fileName to delete=" + fileToKill.getAbsolutePath());
          fileToKill.delete();
        }
        dirToKill.delete();
      }
  }

  public void testSetIndexPath() {
      XMLSandRManager mgr = new XMLSandRManager();
      String curPath = mgr.getIndexPath();
      String newIndexPath = "newIndex";
      java.io.File newDir1 = new java.io.File(newIndexPath);
      removeDirectory(newIndexPath);
      try {
        mgr.setIndexPath(newIndexPath);
      } catch (IndexError exc) {
        exc.printStackTrace();
        this.assertTrue("Unexpected exception: " + exc.getMessage(), false);
      }
      this.assertEquals(newDir1.getAbsolutePath(),
                        mgr.getIndexPath());
      this.assertEquals(newDir1.listFiles().length, 1);
      removeDirectory(newDir1.getAbsolutePath());

      String newIndexPath2 = "newIndex2";
      java.io.File badFile = new java.io.File(newIndexPath2);
      try {
        badFile.createNewFile();
      } catch (java.io.IOException exc) {
        System.out.println("Failed to create new file for testing");
        this.assertTrue("Exception: " + exc.getMessage(), false);
      }
      boolean gotException = false;
      try {
        mgr.setIndexPath(newIndexPath2);
      } catch (IndexError exc) {
        gotException = true;
      }
      assertTrue("Failed to get exception on create over existing file", gotException);
      assertEquals(new java.io.File(newIndexPath).getAbsolutePath(),
                   mgr.getIndexPath());
      badFile.delete();
      assertTrue(!badFile.exists());

      removeDirectory(newIndexPath2);
  }

}
