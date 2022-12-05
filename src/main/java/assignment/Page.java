package assignment;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The Page class holds anything that the QueryEngine returns to the server.  The field and method
 * we provided here is the bare minimum requirement to be a Page - feel free to add anything you
 * want as long as you don't break the getURL method.
 *
 */
public class Page implements Serializable {

    private static final long serialVersionUID = 6487935751107137193L;

    // The URL the page was located at.
    private URL url;

    private HashMap<String, List<Integer>> mapStringtoIndex;
    private List<String> mapIndextoString;

    /**
     * Creates a Page with a given URL.
     * @param url The url of the page.
     */
    public Page(URL url) {
        this.url = url;
        mapStringtoIndex = new HashMap<>();
        mapIndextoString = new ArrayList<>();
    }

    /**
     * @return the URL of the page.
     */
    public URL getURL() { return url; }

    public HashMap<String, List<Integer>> getMapStringtoIndex() {
        return mapStringtoIndex;
    }

    public List<String> getMapIndextoString() {
        return mapIndextoString;
    }
}
