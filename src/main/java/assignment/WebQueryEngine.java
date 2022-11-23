package assignment;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 * TODO: Implement this!
 */
public class WebQueryEngine {

    private Collection<Page> pages;
    private WebIndex index;

    public WebQueryEngine(WebIndex index) {
        pages = new HashSet<Page>();
        this.index = index;
    }

    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        // TODO: Implement this!
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param queryString A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String queryString) {
        // TODO: Implement this!
        StringBuilder query = new StringBuilder(queryString.toLowerCase());
        boolean word = false;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (c == '&' || c == '|') {
                word = false;
            }
            if (isCharacter(c)) {
                // add an & sign due to implicit and
                if (word && i > 0 && query.charAt(i-1) == ' ') {
                    query = new StringBuilder(query.substring(0, i-1)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i-1). + "&" + query.substring(i);
                }
                else if (word && i > 0 && query.charAt(i-1) == '(') {
                    if (i > 1) {
                        query = new StringBuilder(query.substring(0, i-2)).append("&").append(new StringBuilder(query.substring(i)));
//                        query = query.substring(0, i-2) + "&" + query.substring(i);
                    }
                    else {
                        query = new StringBuilder("&").append(query);
//                        query = "&" + query;
                    }
                }
                else if (word && i > 0 && query.charAt(i-1) == ')') {
                    query = new StringBuilder(query.substring(0, i)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i) + "&" + query.substring(i);
                }
                word = true;
            }
        }
        System.out.println(query);
        QueryTree queryTree = new QueryTree(query.toString());
        pages = queryTree.dfs(index, queryTree.getRoot());
        return pages;
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}