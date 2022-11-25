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
//        StringBuilder query = new StringBuilder(queryString.toLowerCase());
        StringBuilder query = new StringBuilder();
        boolean word = false;
        for (int i = 0; i < queryString.length(); i++) {
            char c = queryString.charAt(i);
            if (c == ' ') {
                // can skip this character
                continue;
            }
            else if (c == '&' || c == '|') {
                word = false;
                query.append(c);
            }
            else if (c == ')' || c == '(') {
                query.append(c);
            }
            else if (isCharacter(c)) {
                // add an & sign due to implicit and
                if (word && i > 0 && queryString.charAt(i-1) == ' ') {
//                    query = new StringBuilder(query.substring(0, i-1)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i-1). + "&" + query.substring(i);
                    query.append("&");
                }
                else if (word && i > 1 && queryString.charAt(i-1) == '(') {
//                        query = new StringBuilder(query.substring(0, i-2)).append("&").append(new StringBuilder(query.substring(i)));
//                        query = query.substring(0, i-2) + "&" + query.substring(i);
                    query.deleteCharAt(query.length()-1);
                    query.append('&');
                    query.append('(');
                }
                else if (word && i > 0 && queryString.charAt(i-1) == ')') {
//                    query = new StringBuilder(query.substring(0, i)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i) + "&" + query.substring(i);
                    query.append('&');
                }
                query.append(c);
                word = true;
            }
        }

        System.out.println(query);
        QueryTree queryTree = new QueryTree(query.toString());
        System.out.println("root string: " + queryTree.getRoot().getString());
        pages = queryTree.dfs(index, queryTree.getRoot());
        return pages;
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}