package assignment;

import java.io.File;
import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 * TODO: Implement this!
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    private WebIndex webIndex;
    private List<URL> urls;
    private Page page;
    private URL url;
    private Stack<Boolean> parseText;
//    private boolean parseText;
    private StringBuilder allText;

    public CrawlingMarkupHandler() {
        webIndex = new WebIndex();
//        urls = new ArrayList<>();
//        parseText = false;
//        parseText = new Stack<>();
    }

    /**
    * This method returns the complete index that has been crawled thus far when called.
    */
    public Index getIndex() {
        // TODO: Implement this!
        return webIndex;
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {
        // TODO: Implement this!
        List<URL> newURLs = new ArrayList<>(urls);
        urls.clear();
        return newURLs;
    }

    public void setUrl(URL url) {
        this.url = url;
        page = new Page(url);
    }

    /**
    * These are some of the methods from AbstractSimpleMarkupHandler.
    * All of its method implementations are NoOps, so we've added some things
    * to do; please remove all the extra printing before you turn in your code.
    *
    * Note: each of these methods defines a line and col param, but you probably
    * don't need those values. You can look at the documentation for the
    * superclass to see all of the handler methods.
    */

    /**
    * Called when the parser first starts reading a document.
    * @param startTimeNanos  the current time (in nanoseconds) when parsing starts
    * @param line            the line of the document where parsing starts
    * @param col             the column of the document where parsing starts
    */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // TODO: Implement this.
        System.out.println("Start of document");

        // initiate instance variables
        urls = new ArrayList<>();
//        parseText = false;
        parseText = new Stack<>();
        allText = new StringBuilder();
    }

    /**
    * Called when the parser finishes reading a document.
    * @param endTimeNanos    the current time (in nanoseconds) when parsing ends
    * @param totalTimeNanos  the difference between current times at the start
    *                        and end of parsing
    * @param line            the line of the document where parsing ends
    * @param col             the column of the document where the parsing ends
    */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) {
        // TODO: Implement this.
        System.out.println("End of document");

        webIndex.addPage(page);
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this element appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {
        // TODO: Implement this.
        System.out.println("Start element: " + elementName);

        // do not parse certain tags that contain text that isn’t displayed on the screen
        if (elementName.equals("style") || elementName.equals("script")) {
//            parseText = false;
            parseText.push(false);
        }
        else {
//            parseText = true;
            parseText.push(true);
        }

        if (attributes != null && attributes.containsKey("href")) {
            // add the link to urls. Since file path is relative, need to find the correct file path
            try {
                StringBuilder urlLink = new StringBuilder(attributes.get("href"));

                if (!checkEnding(urlLink)) return;

                if (url != null) {
                    // find the right relative file path
                    int count = 1;
                    while (urlLink.length() >= 3 && urlLink.substring(0, 3).equals("../")) {
                        count++;
                        urlLink = new StringBuilder(urlLink.substring(3));
                    }

                    StringBuilder currentURL = new StringBuilder(url.toString());
                    for (int i = 0; i < count && currentURL.lastIndexOf("/") != -1; i++) {
                        currentURL = new StringBuilder(currentURL.substring(0, currentURL.lastIndexOf("/")));
                    }
                    currentURL.append('/');

                    urlLink = currentURL.append(urlLink);
                }

                URL newURL = new URL(urlLink.toString());
                urls.add(newURL);
                System.out.println("HERE " + url.toString() + " " + attributes.get("href") + " " + urlLink);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // checks if the url ends in .txt, .htm, or .html
    private boolean checkEnding(StringBuilder url) {
        int n = url.length();
        if ((n >= 4 && (url.substring(n-4).equals(".txt") || url.substring(n-4).equals(".htm"))) ||
                (n >= 5 && url.substring(n-5).equals(".html"))) {
            return true;
        }
        return false;
    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this element appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        // TODO: Implement this.
        System.out.println("End element:   " + elementName);

        parseText.pop();
    }

    /**
    * Called whenever characters are found inside a tag. Note that the parser is not
    * required to return all characters in the tag in a single chunk. Whitespace is
    * also returned as characters.
    * @param ch      buffer containing characters; do not modify this buffer
    * @param start   location of 1st character in ch
    * @param length  number of characters in ch
    */
    public void handleText(char[] ch, int start, int length, int line, int col) {
        // TODO: Implement this.

//        if (!parseText) {
//            return;
//        }

        if (parseText.size() > 0 && !parseText.peek()) {
            return;
        }

        System.out.print("Characters:    \"");

        for(int i = start; i < start + length; i++) {
            // Instead of printing raw whitespace, we're escaping it
            switch(ch[i]) {
                case '\\':
                    System.out.print("\\\\");
                    break;
                case '"':
                    System.out.print("\"");
                    break;
                case '\n':
                    System.out.print("\\n");
                    break;
                case '\r':
                    System.out.print("\\r");
                    break;
                case '\t':
                    System.out.print("\\t");
                    break;
                case ' ':
                    System.out.print(" ");
                    break;
                default:
                    System.out.print(ch[i]);
                    if (isCharacter(ch[i])) {
//                         System.out.println("HERE");
                        StringBuilder current = new StringBuilder(String.valueOf(ch[i++]));
                        while (i < start + length && isCharacter(ch[i])) {
                            current.append(ch[i]);
                            i++;
                        }
                        String currentString = current.toString().toLowerCase();
                        webIndex.getTrie().add(currentString, page);
                        i--;
                        System.out.print("(" + currentString + ")");
                    }
                    break;
            }
        }

        System.out.println("\"");
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}
