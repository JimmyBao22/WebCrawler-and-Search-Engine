package assignment;

import java.net.*;
import java.util.*;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 * TODO: Implement this!
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    // TODO: Implement all of this! You may choose your own data structures an internal APIs.
    // You should not need to worry about serialization (just make any other data structures you use
    // here also serializable - the Java standard library data structures already are, for example).

    // stores parsed pages, need quick lookup of certain information in the pages.
        // also need to utilize this for web servers

    private Collection<Page> pages;

    // stores a all words in all pages, and it stores also a list of pages containing the words
//    private Trie trie;

    public WebIndex() {
        pages = new ArrayList<>();
//        trie = new Trie();
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public Collection<Page> getPages() {
        return pages;
    }

//    public Trie getTrie() {
//        return trie;
//    }
}