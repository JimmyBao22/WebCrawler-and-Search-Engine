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

        ArrayDeque<Token> tokenList = new ArrayDeque<>();
        for (int i = 0; i < queryString.length(); i++) {
            Token current = getToken(queryString.substring(i));
            if (current == null) {          // invalid query found
                return null;
            }
            tokenList.add(current);
            if (current.isWord()) {
                i += current.getWord().length() - 1;
            }
        }

        TreeNode root = parseQuery(tokenList);

        return dfs(index, root);
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
                for (Page page : webIndex.getPages()) {
                    // add page only if the page does not contain the word
                    if (!webIndex.getStringtoPages().get(currentToken.getWord()).contains(page)) {
                        pages.add(page);
                    }
                }
            }
            else {
                pages = webIndex.getStringtoPages().get(currentToken.getWord());
            }
//            System.out.println(pages + " " + pages.size());
        }
        else if (currentToken.isPhrase()) {
            // this is a phrase, search for full phrase in the webindex by iterating over phrase
            String phrase = currentToken.getWord();
            int spaceIndex = phrase.indexOf(' ');
            String currWord = phrase.substring(0, spaceIndex);
            pages = webIndex.getStringtoPages().get(currWord);

            // continuously see if the page contains the consecutive words and updating the saved pages accordingly
            while (spaceIndex != -1) {
                String newWord = phrase.substring(spaceIndex+1, phrase.indexOf(' ', spaceIndex+1));
                spaceIndex = phrase.indexOf(' ', spaceIndex+1);
                Collection<Page> updatedPages = new HashSet<>();
                for (Page page : pages) {
                    if (page.getMapConsecutiveStrings().get(currWord).contains(newWord)) {
                        updatedPages.add(page);
                    }
                }
                currWord = newWord;
                pages = updatedPages;
            }
        }
        else {
            Collection<Page> leftPages = dfs(webIndex, current.getLeft());
            Collection<Page> rightPages = dfs(webIndex, current.getRight());

            if (currentToken.isAnd()) {
                // add all pages that are in both left and right subtrees into the current page
                for (Page page : rightPages) {
                    if (leftPages.contains(page)) {
                        pages.add(page);
                    }
                }
            } else if (currentToken.isOr()) {
                // add all pages from left and right subtrees into the current page
                for (Page page : leftPages) {
                    pages.add(page);
                }
                for (Page page : rightPages) {
                    pages.add(page);
                }
            } else {
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
        Token currentToken = tokenList.poll();
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
            System.err.println("Invalid Query");
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
            while (isCharacter(query.charAt(i))) {
                i++;
            }
            i--;
            if (c == '!') {
                return new Token("Word", query.substring(1, i+1), true);
            }
            else {
                return new Token("Word", query.substring(0, i+1), false);
            }
        }
        else if (c == '"') {
            return new Token("Phrase", query.substring(0, query.indexOf('"', 1)), false);
        }
        else {
            System.err.println("Invalid Query");
            return null;
        }
    }

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}