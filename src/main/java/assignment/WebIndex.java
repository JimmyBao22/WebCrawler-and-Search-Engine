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

    // stores all parsed pages
    private Collection<Page> pages;

    // stores all words from all pages. Further, for each word, this stores also a list of all pages containing that words
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