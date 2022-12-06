package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class TestWebQueryEngine {

    private int n;
    private WebIndex webIndex;
    private WebQueryEngine webQueryEngine;

    @Test
    public void testQueries() {
        testQuery();
        testQueryAnd();
        testQueryOr();
        testQueryNegation();
        testQueryPhrase();
        testQueryImplicitAnd();
        testComplexQuery1();
        testComplexQuery2();
    }

    public void testComplexQuery2() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "(\"internet is a\" | danger) the (!hello | !hi)";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Or", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Phrase internet is a", treeNode.getLeft().getLeft().getToken().toString());
        Assertions.assertEquals("Word danger false", treeNode.getLeft().getRight().getToken().toString());
        Assertions.assertEquals("And", treeNode.getRight().getToken().toString());
        Assertions.assertEquals("Word the false", treeNode.getRight().getLeft().getToken().toString());
        Assertions.assertEquals("Or", treeNode.getRight().getRight().getToken().toString());
        Assertions.assertEquals("Word hello true", treeNode.getRight().getRight().getLeft().getToken().toString());
        Assertions.assertEquals("Word hi true", treeNode.getRight().getRight().getRight().getToken().toString());
    }

    public void testComplexQuery1() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "officials  (!hello | john)   senator";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Word officials false", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("And", treeNode.getRight().getToken().toString());
        Assertions.assertEquals("Or", treeNode.getRight().getLeft().getToken().toString());
        Assertions.assertEquals("Word senator false", treeNode.getRight().getRight().getToken().toString());
        Assertions.assertEquals("Word hello true", treeNode.getRight().getLeft().getLeft().getToken().toString());
        Assertions.assertEquals("Word john false", treeNode.getRight().getLeft().getRight().getToken().toString());
    }

    public void testQueryAnd() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "(should & quiet)";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Word should false", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Word quiet false", treeNode.getRight().getToken().toString());
    }

    public void testQueryOr() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "(should | quiet)";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("Or", treeNode.getToken().toString());
        Assertions.assertEquals("Word should false", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Word quiet false", treeNode.getRight().getToken().toString());
    }

    public void testQueryNegation() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "(!should & !quiet)";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Word should true", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Word quiet true", treeNode.getRight().getToken().toString());
    }

    public void testQueryPhrase() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "(\"should we\" & quiet)";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Phrase should we", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Word quiet false", treeNode.getRight().getToken().toString());
    }

    public void testQueryImplicitAnd() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "should quiet";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("And", treeNode.getToken().toString());
        Assertions.assertEquals("Word should false", treeNode.getLeft().getToken().toString());
        Assertions.assertEquals("Word quiet false", treeNode.getRight().getToken().toString());
    }

    public void testQuery() {
        webQueryEngine = new WebQueryEngine(webIndex);
        String testString = "should";
        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(testString);
        TreeNode treeNode = webQueryEngine.parseQuery(tokenList);
        Assertions.assertEquals("Word should false", treeNode.getToken().toString());
    }

    @RepeatedTest(1000)
    public void testTokenGeneration() {
        webQueryEngine = new WebQueryEngine(webIndex);

        n = 1000;

        StringBuilder query = new StringBuilder();
        // build query string. For this method, the query string does not need to be valid

        List<Token> correctTokenList = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double prob = Math.random();
            if (prob < 0.17) {
                query.append("&");
                correctTokenList.add(new Token("And"));
            }
            else if (prob < 0.33) {
                query.append("|");
                correctTokenList.add(new Token("Or"));
            }
            else if (prob < 0.5) {
                query.append("(");
                correctTokenList.add(new Token("Left Parenthesis"));
            }
            else if (prob < 0.67) {
                query.append(")");
                correctTokenList.add(new Token("Right Parenthesis"));
            }
            else if (prob < 0.83) {
                String cur = generateString();
                boolean negative = Math.random() < 0.5;
                correctTokenList.add(new Token("Word", cur, negative));
                if (negative) query.append("!");
                query.append(cur + " ");
            }
            else {
                // phrase
                String cur = generateString();
                correctTokenList.add(new Token("Phrase", cur, false));
                query.append("\"" + cur + "\" ");
            }
        }
        String queryString = query.toString();

        ArrayDeque<Token> tokenList = webQueryEngine.getTokenList(queryString);

        Assertions.assertEquals(correctTokenList.size(), tokenList.size());

        int i = 0;
        while (!tokenList.isEmpty()) {
            Token correctToken = correctTokenList.get(i);
            Token token = tokenList.poll();
            Assertions.assertEquals(correctToken.toString(), token.toString());
            i++;
        }
    }

    private String generateString() {
        int length = (int)(Math.random() * 10) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char)('a' + (int)(Math.random() * 26));
            sb.append(c);
        }
        return sb.toString();
    }
}