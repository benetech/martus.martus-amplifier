package com.isogen.lucene.lucene_xml;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import java.io.Reader;
import java.util.Hashtable;

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
 *
 */

public class StemmingAnalyzer extends Analyzer
{
    /**
     * An array containing some common words that
     * are not usually useful for searching.
     *
     * FIXME: need to read this from a file probably.
     */
    private static final String[] STOP_WORDS =
        {
        "a"       , "and"     , "are"     , "as"      ,
            "at"      , "be"      , "but"     , "by"      ,
            "for"     , "if"      , "in"      , "into"    ,
            "is"      , "it"      , "no"      , "not"     ,
            "of"      , "on"      , "or"      , "s"       ,
            "such"    , "t"       , "that"    , "the"     ,
            "their"   , "then"    , "there"   , "these"   ,
            "they"    , "this"    , "to"      , "was"     ,
            "will"    ,
            "with"
    };

    /*
     * Stop table
     */
    final static private Hashtable stopTable = StopFilter.makeStopTable(STOP_WORDS);

    /*
     * Create a token stream for this analyzer.
     */
    public final TokenStream tokenStream(String fieldName, final Reader reader)
    {
        TokenStream result = new StandardTokenizer(reader);

        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopTable);
        result = new PorterStemFilter(result);

        return result;
    }
}
