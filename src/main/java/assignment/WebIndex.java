package assignment;

import java.net.*;
import java.util.*;

/**
 * A web-index which efficiently stores information about pages. Serialization is done automatically
 * via the superclass "Index" and Java's Serializable interface.
 *
 */
public class WebIndex extends Index {
    /**
     * Needed for Serialization (provided by Index) - don't remove this!
     */
    private static final long serialVersionUID = 1L;

    // stores parsed pages, need quick lookup of certain information in the pages.
        // also need to utilize this for web servers

    private Collection<Page> pages;

    // stores a all words in all pages, and it stores also a list of pages containing the words
    private HashMap<String, HashSet<Page>> stringtoPages;

    public WebIndex() {
        pages = new ArrayList<>();
        stringtoPages = new HashMap<>();
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public Collection<Page> getPages() {
        return pages;
    }

    public HashMap<String, HashSet<Page>> getStringtoPages() {
        return stringtoPages;
    }
}