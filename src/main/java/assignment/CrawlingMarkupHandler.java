package assignment;

import java.io.File;
import java.util.*;
import java.net.*;
import org.attoparser.simple.*;

/**
 * A markup handler which is called by the Attoparser markup parser as it parses the input;
 * responsible for building the actual web index.
 *
 */
public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

    private WebIndex webIndex;
    private List<URL> urls;
    private Page page;
    private URL url;
    private Stack<Boolean> parseText;
    private StringBuilder allText;

    public CrawlingMarkupHandler() {
        webIndex = new WebIndex();
    }

    /**
    * This method returns the complete index that has been crawled thus far when called.
    */
    public Index getIndex() {
        return webIndex;
    }

    /**
    * This method returns any new URLs found to the Crawler; upon being called, the set of new URLs
    * should be cleared.
    */
    public List<URL> newURLs() {
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

        // initiate instance variables
        urls = new ArrayList<>();
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

        webIndex.addPage(page);

        // parse all text at once. This is to make it easier to parse text, especially when text are
            // in different lines and also in different tags
        String lastWord = null;
        for (int i = 0; i < allText.length(); i++) {
            if (!isCharacter(allText.charAt(i))) {
                continue;
            }
            int j = i+1;
            while (j < allText.length() && isCharacter(allText.charAt(j))) {
                j++;
            }
            String currentString = allText.substring(i, j);
            i = j-1;
            // webIndex.getTrie().add(currentString, page);
            if (lastWord != null) {
                if (!page.getMapConsecutiveStrings().containsKey(lastWord)) {
                    page.getMapConsecutiveStrings().put(lastWord, new HashSet<>());
                }
                page.getMapConsecutiveStrings().get(lastWord).add(currentString);
            }
            lastWord = currentString;
//            System.out.print("(" + currentString + ")");
        }
    }

    /**
    * Called at the start of any tag.
    * @param elementName the element name (such as "div")
    * @param attributes  the element attributes map, or null if it has no attributes
    * @param line        the line in the document where this element appears
    * @param col         the column in the document where this element appears
    */
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) {

        // do not parse certain tags that contain text that isn’t displayed on the screen
        if (elementName.equals("style") || elementName.equals("script") || elementName.equals("span")) {
            parseText.push(false);
        } else {
            parseText.push(true);
            allText.append(" ");
        }

        if (elementName.equals("a") && attributes != null && attributes.containsKey("href")) {
            // add the link to urls. Since file path is relative, need to find the correct file path
            try {
                URL newURL = new URL(url, attributes.get("href"));

                urls.add(newURL);
//                System.out.println("HERE " + url.toString() + " " + attributes.get("href") + " " + urlLink);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
    * Called at the end of any tag.
    * @param elementName the element name (such as "div").
    * @param line        the line in the document where this element appears.
    * @param col         the column in the document where this element appears.
    */
    public void handleCloseElement(String elementName, int line, int col) {
        if (parseText.pop()) {
            allText.append(" ");
        }
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
        if (parseText.size() > 0 && !parseText.peek()) {
            return;
        }

        for(int i = start; i < start + length; i++) {
            if (isCharacter(ch[i]) || ch[i] == ' ') {
                allText.append(ch[i]);
            }
        }
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}
