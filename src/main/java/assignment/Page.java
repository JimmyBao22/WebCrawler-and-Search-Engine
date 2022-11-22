package assignment;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;

/**
 * The Page class holds anything that the QueryEngine returns to the server.  The field and method
 * we provided here is the bare minimum requirement to be a Page - feel free to add anything you
 * want as long as you don't break the getURL method.
 *
 * TODO: Implement this!
 */
public class Page implements Serializable {

    private static final long serialVersionUID = 6487935751107137193L;
    // The URL the page was located at.
    private URL url;

    // contains all words that are in the page
//    private Trie trie;

//    public Page() {
//        trie = new Trie();
//    }
//
//    public Trie getTrie() {
//        return trie;
//    }

    /**
     * Creates a Page with a given URL.
     * @param url The url of the page.
     */
    public Page(URL url) {
        this.url = url;
    }

    /**
     * @return the URL of the page.
     */
    public URL getURL() { return url; }
}
