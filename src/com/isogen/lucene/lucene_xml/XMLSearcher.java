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

import org.apache.lucene.search.Hits;
import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.analysis.StopAnalyzer;

public class XMLSearcher {

    String pathToIndex = "";
    IndexSearcher searcher = null;
    public XMLSearcher(String indexPath) throws SearchError {
        pathToIndex = indexPath;
        try {
            searcher = new IndexSearcher(pathToIndex);
        } catch (java.io.IOException e) {
            throw new SearchError("Failed to created new IndexSearcher at path '" + pathToIndex + "'");
        }
    }

    public synchronized Hits search(String queryStr) throws SearchError{
        StemmingAnalyzer analyzer = new StemmingAnalyzer();
        //Analyzer analyzer = new SimpleAnalyzer();
        //System.out.println("stopAnalyzer created");
        Query query = null;
        try {
            //System.out.println("query string='" + queryStr + "'");
            query = new QueryParser("content", analyzer).parse(queryStr);
        } catch (Throwable e) {
            throw new SearchError("Exception creating query: " + e.getMessage());
        }
        //System.out.println("query created: " + query.getClass());

        Hits hits = null;
        try {
            hits = searcher.search(query);
        } catch (java.io.IOException e) {
            throw new SearchError("Search failed with exception: " + e.getMessage());
        }
        //System.out.println("search completed: " + hits.length());
        return hits;
    }

    public void close() throws SearchError {
        try {
            searcher.close();
        } catch (java.io.IOException e) {
            throw new SearchError("searcher.close() failed with exception: " + e.getMessage());
        }
    }
}