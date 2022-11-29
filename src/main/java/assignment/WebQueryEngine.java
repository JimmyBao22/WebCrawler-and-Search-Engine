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
            else if (c == ')' || c == '(' || c == '!') {
                query.append(c);
            }
            else if (isCharacter(c)) {
                // add an & sign due to implicit and. Whenever, you add implicit and, you need to add a set of parenthesis
                if (word && i > 0 && queryString.charAt(i-1) == ' ') {
//                    query = new StringBuilder(query.substring(0, i-1)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i-1). + "&" + query.substring(i);
                    query.append("&");
                    query = new StringBuilder("(").append(query);
                }
                else if (word && i > 1 && queryString.charAt(i-1) == '(') {
//                        query = new StringBuilder(query.substring(0, i-2)).append("&").append(new StringBuilder(query.substring(i)));
//                        query = query.substring(0, i-2) + "&" + query.substring(i);
                    query.deleteCharAt(query.length()-1);
                    query.append('&');
                    query.append('(');
                    query = new StringBuilder("(").append(query);
                }
                else if (word && i > 0 && queryString.charAt(i-1) == ')') {
//                    query = new StringBuilder(query.substring(0, i)).append("&").append(new StringBuilder(query.substring(i)));
//                    query = query.substring(0, i) + "&" + query.substring(i);
                    query.append('&');
                    query = new StringBuilder("(").append(query);
                }
                query.append(c);
                word = true;
            }
        }

        int countCloseParenthesis = 0;
        int countOperators = 0;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (c == ')') {
                countCloseParenthesis++;
            }
            else if (c == '|' || c == '&') {
//                if (countOperators != countCloseParenthesis) {
//                    // place closed parenthesis before this character
//                    query = new StringBuilder(query.substring(0, i)).append(')').append(query.substring(i));
//                    countCloseParenthesis++;
//                    i++;
//                }

                countOperators++;
            }
//            else if (isCharacter(c) && i > 0 && query.charAt(i-1) == ')'
//                    && countOperators != countCloseParenthesis && (countOperators > 1 || query.charAt(i-1) == ')')) {
////            if (!isCharacter(c) && i > 0 && isCharacter(query.charAt(i-1)) && countOperators != countCloseParenthesis) {
////            else if (isCharacter(c) && (i == query.length()-1 || !isCharacter(query.charAt(i+1))) &&
////                    countOperators != countCloseParenthesis) {
//                // need to add a close parenthesis
//                // TODO better stringbuilder method for this?
////                query = new StringBuilder(query.substring(0, i+1)).append(')').append(query.substring(i+1));
//                query = new StringBuilder(query.substring(0, i)).append(')').append(query.substring(i));
//                i--;
//            }
        }

        if (countOperators != countCloseParenthesis) {
            // add closed parenthesis to the end
            query.append(')');
            countCloseParenthesis++;
        }

//        System.out.println(query);
//        System.out.println(countOperators + " " + countCloseParenthesis);
//
//        for (int i = query.length()-1; i >= 0; i--) {
//            char c = query.charAt(i);
//            if (c == ')') {
//                countCloseParenthesis--;
//            }
//            else if (c == '|' || c == '&') {
//                countOperators--;
//
////                System.out.println(i + " " + )
//
////                if (i > 0 && isCharacter(query.charAt(i-1)) && countOperators != countCloseParenthesis) {
//                if (countOperators > countCloseParenthesis) {
//                    // place closed parenthesis before this character
//                    query = new StringBuilder(query.substring(0, i)).append(')').append(query.substring(i));
//                    countCloseParenthesis++;
//                }
//
//                System.out.println(query.substring(0, i) + " " + countOperators + " " + countCloseParenthesis);
//            }
//        }

        System.out.println(query);
//        QueryTree queryTree = new QueryTree(query.toString());
//        System.out.println("root string: " + queryTree.getRoot().getString());
//        pages = queryTree.dfs(index, queryTree.getRoot());
        return pages;
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}