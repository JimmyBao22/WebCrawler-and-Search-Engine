package assignment;
import com.sun.source.tree.Tree;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 * A query engine which holds an underlying web index and can answer textual queries with a
 * collection of relevant pages.
 *
 */
public class WebQueryEngine {

    private Collection<Page> pages;
    private WebIndex index;

    public WebQueryEngine(WebIndex index) {
        pages = new HashSet<>();
        this.index = index;
    }

    /**
     * Returns a WebQueryEngine that uses the given Index to construct answers to queries.
     *
     * @param index The WebIndex this WebQueryEngine should use.
     * @return A WebQueryEngine ready to be queried.
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        return new WebQueryEngine(index);
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the query expression.
     *
     * @param queryString A query expression.
     * @return A collection of web pages satisfying the query.
     */
    public Collection<Page> query(String queryString) {
        ArrayDeque<Token> tokenList = new ArrayDeque<>();
        for (int i = 0; i < queryString.length(); i++) {
            if (queryString.charAt(i) == ' ') {
                continue;
            }
            Token current = getToken(queryString.substring(i));
            if (current == null) {          // invalid query found
                return null;
            }
            tokenList.add(current);
            if (current.isWord() || current.isPhrase()) {
                i += current.getWord().length() - 1;
                if (current.getNegation()) {
                    i++;
                }
                if (current.isPhrase()) {
                    i += 2;
                }
            }
        }

//        while (!tokenList.isEmpty()) {
//            System.out.println(tokenList.poll());
//        }
//        System.out.println();

        TreeNode root = parseQuery(tokenList);

        Collection<Page> queriedPages = dfs(index, root);
        System.out.println(queriedPages.size());
        return queriedPages;
    }

    // iterate over query tree by recursion
    private Collection<Page> dfs(WebIndex webIndex, TreeNode current) {
        if (current == null) {
            return null;
        }
        Collection<Page> pages = new HashSet<>();
        Token currentToken = current.getToken();
        if (currentToken.isWord()) {
            // this is a word, search for it in the webindex

            if (currentToken.getNegation()) {
                if (webIndex.getStringtoPages().get(currentToken.getWord()) == null) {
                    pages = webIndex.getPages();
                }
                else {
                    for (Page page : webIndex.getPages()) {
                        if (webIndex.getStringtoPages().get(currentToken.getWord()) == null) {
                            // edge case: if no pages contain this word, that means this page doesn't contain the word either
                            pages.add(page);
                        }
                        else if (!webIndex.getStringtoPages().get(currentToken.getWord()).contains(page)) {
                            // add page if the page does not contain the word
                            pages.add(page);
                        }
                    }
                }
            }
            else {
                pages = webIndex.getStringtoPages().get(currentToken.getWord());
                if (pages == null) {
                    pages = new HashSet<>();
                }
            }
            System.out.println(currentToken.getWord() + " " + pages.size());
        }
        else if (currentToken.isPhrase()) {
            // this is a phrase, search for full phrase in the webindex by iterating over phrase
            String phrase = currentToken.getWord();
            String lastWord = null;
            HashMap<Page, List<Integer>> lastWordIndices = new HashMap<>();

            for (int i = 0; i < phrase.length(); i++) {
                if (!isCharacter(phrase.charAt(i))) {
                    continue;
                }

                // loop over and find the current string, which is the substring from i to j
                int j = i + 1;
                while (j < phrase.length() && isCharacter(phrase.charAt(j))) {
                    j++;
                }
                String currentWord = phrase.substring(i, j).toLowerCase();

                if (lastWord == null) {
                    for (Page page : webIndex.getStringtoPages().get(currentWord)) {
                        lastWordIndices.put(page, page.getMapStringtoIndex().get(currentWord));
                    }
                }
                else {
                    // only add pages that contain a phrase with the current and last words consecutively appearing on the webpage
                    HashMap<Page, List<Integer>> updatedWordIndices = new HashMap<>();
                    for (Page page : lastWordIndices.keySet()) {
                        List<Integer> lastIndices = lastWordIndices.get(page);
                        List<Integer> indices = new ArrayList<>();
                        for (int k = 0; k < lastIndices.size(); k++) {
                            int lastIndex = lastIndices.get(k);
                            if (page.getMapIndextoString().get(lastIndex + 1).equals(currentWord)) {
                                indices.add(lastIndex + 1);
                            }
                        }

                        if (indices.size() > 0) {
                            updatedWordIndices.put(page, indices);
                        }
                    }

                    lastWordIndices = updatedWordIndices;
                }

                lastWord = currentWord;
                i = j - 1;
            }

            pages = lastWordIndices.keySet();
        }
        else {
            Collection<Page> leftPages = dfs(webIndex, current.getLeft());
            Collection<Page> rightPages = dfs(webIndex, current.getRight());

            System.out.println("left: " + leftPages.size());
            System.out.println("right: " + rightPages.size());

            if (currentToken.isAnd()) {
                // add all pages that are in both left and right subtrees into the current page
                for (Page page : rightPages) {
                    if (leftPages.contains(page)) {
                        pages.add(page);
                    }
                }
            }
            else if (currentToken.isOr()) {
                // add all pages from left and right subtrees into the current page
                for (Page page : leftPages) {
                    pages.add(page);
                }
                for (Page page : rightPages) {
                    pages.add(page);
                }
            }
            else {
                System.err.println("Query Error");
                return new HashSet<>();
            }
        }

        return pages;
    }

    private TreeNode parseQuery(ArrayDeque<Token> tokenList) {
        // detect whether or not an implicit and can occur
        boolean canBeImplicitAnd = false;

        ArrayDeque<Token> copy = new ArrayDeque<>();
        while (!tokenList.isEmpty()) {
            Token current = tokenList.poll();
            if (canBeImplicitAnd && (current.isWord() || current.isPhrase() || current.isLeftParens())) {
                // parse query due to implicit and
                tokenList.push(current);

                TreeNode currentNode = new TreeNode(new Token("And"));
                TreeNode left = parseQueryPrime(copy);
                TreeNode right = parseQuery(tokenList);
                currentNode.setLeft(left);
                currentNode.setRight(right);

                return currentNode;
            }

            copy.add(current);

            canBeImplicitAnd = (current.isWord() || current.isRightParens() || current.isPhrase());
        }

        return parseQueryPrime(copy);
    }

    private TreeNode parseQueryPrime(ArrayDeque<Token> tokenList) {
        if (tokenList.isEmpty()) {
            System.err.println("Invalid Query");
            return null;
        }
        Token currentToken = tokenList.poll();
//        System.out.println(currentToken);

        if (currentToken.isLeftParens()) {
            // recursively build the left subtree
            TreeNode left = parseQueryPrime(tokenList);
            // get the binary operator: AND or OR
            Token operator = tokenList.poll();
            // recursively build the right subtree
            TreeNode right = parseQueryPrime(tokenList);
            // read the remaining Right Parens
            tokenList.poll();

            TreeNode current = new TreeNode(operator);
            current.setLeft(left);
            current.setRight(right);

            return current;
        }
        else if (currentToken.isWord() || currentToken.isPhrase()) {
            return new TreeNode(currentToken);
        }
        else {
            System.err.println("Unable to Parse Query");
            return null;
        }
    }

    private Token getToken(String query) {
        char c = query.charAt(0);
        if (c == '&') {
            return new Token("And");
        }
        else if (c == '|') {
            return new Token("Or");
        }
        else if (c == '(') {
            return new Token("Left Parenthesis");
        }
        else if (c == ')') {
            return new Token("Right Parenthesis");
        }
        else if (isCharacter(c) || c == '!'){
            int i = 1;
            while (i < query.length() && isCharacter(query.charAt(i))) {
                i++;
            }
            if (c == '!') {
                return new Token("Word", query.substring(1, i), true);
            }
            else {
                return new Token("Word", query.substring(0, i), false);
            }
        }
        else if (c == '"') {
            return new Token("Phrase", query.substring(1, query.indexOf('"', 1)), false);
        }
        else {
            System.err.println("Invalid Character in Query");
            return null;
        }
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}