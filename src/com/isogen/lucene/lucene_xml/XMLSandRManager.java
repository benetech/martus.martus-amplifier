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

public class XMLSandRManager {

    XMLIndexer indexer = null;
    String pathToIndex;

    public XMLSandRManager() {
        pathToIndex = System.getProperty("user.dir") + java.io.File.separator + "testindex";
        indexer = new XMLIndexer(pathToIndex);
    }

    public void initializeIndex() {
        indexer.initializeIndex(0);
    }

    public XMLIndexer getIndexer() {
        return indexer;
    }

    public XMLSearcher createXMLSearcher() throws SearchError {
        return new XMLSearcher(this.pathToIndex);
    }

    public String getIndexPath() {
        return indexer.pathToIndex;
    }

    /**
     * Sets the index path to be used by any new indexers or
     * searchers. Note that existing searchers and indexers are
     * not affected.
     */
    public void setIndexPath(String thePath) throws IndexError {
      java.io.File indexDir = new java.io.File(thePath);
      if (indexDir.exists() && !indexDir.isDirectory()) {
        throw new IndexError("setIndexPath: Path '" + thePath + "' is not a directory.");
      }
      pathToIndex = thePath;
      indexer = new XMLIndexer(pathToIndex);
      if (!indexDir.exists()) {
        initializeIndex();
      } else {
        if (indexDir.listFiles().length == 0) {
          initializeIndex();
        }
      }
    }
}