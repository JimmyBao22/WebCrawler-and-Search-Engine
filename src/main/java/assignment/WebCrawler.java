package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;
import org.attoparser.config.ParseConfiguration;

/**
 * The entry-point for WebCrawler; takes in a list of URLs to start crawling from and saves an index
 * to index.db.
 */
public class WebCrawler {

    /**
    * The WebCrawler's main method starts crawling a set of pages.  You can change this method as
    * you see fit, as long as it takes URLs as inputs and saves an Index at "index.db".
    */
    public static void main(String[] args) {
        // Basic usage information
        if (args.length == 0) {
            System.err.println("Error: No URLs specified.");
            System.exit(1);
        }

        // We'll throw all the args into a queue for processing.
        Queue<URL> remaining = new LinkedList<>();
        for (String url : args) {
            try {
                remaining.add(new URL(url));
            } catch (MalformedURLException e) {
                // Throw this one out!
                System.err.printf("Error: URL '%s' was malformed and will be ignored!%n", url);
            }
        }

        // Create a parser from the attoparser library, and our handler for markup.
        ISimpleMarkupParser parser = new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();

        // Try to start crawling, adding new URLS as we see them.
        try {
            while (!remaining.isEmpty()) {
                // Parse the next URL's page
                parser.parse(new InputStreamReader(remaining.poll().openStream()), handler);

                // Add any new URLs
                remaining.addAll(handler.newURLs());
            }

            handler.getIndex().save("index.db");
        } catch (Exception e) {
            // Bad exception handling :(
            System.err.println("Error: Index generation failed!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
