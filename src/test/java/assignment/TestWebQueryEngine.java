package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class TestWebQueryEngine {

    private int n, m;
    private List<String> allStrings;
    private WebPage[] webPages;
    private WebCrawler webCrawler;
    private WebIndex webIndex;
    private WebQueryEngine webQueryEngine;

    public void testQuery() {
        String testString = "(should & quiet)";
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

        ArrayDeque<Token> tokenList = new ArrayDeque<>();
        for (int i = 0; i < queryString.length(); i++) {
            if (queryString.charAt(i) == ' ') {
                continue;
            }
            Token current = webQueryEngine.getToken(queryString.substring(i));
            if (current == null) {          // invalid query found
                Assertions.assertTrue(false);
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

        Assertions.assertEquals(correctTokenList.size(), tokenList.size());

        int i = 0;
        while (!tokenList.isEmpty()) {
            Token correctToken = correctTokenList.get(i);
            Token token = tokenList.poll();
            Assertions.assertEquals(correctToken.toString(), token.toString());
            i++;
        }
    }

    public void generateGraph(int n, int m) throws FileNotFoundException {
        this.n = n;
        this.m = m;

        // generate all strings that
        allStrings = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            allStrings.add(generateString());
        }

        webPages = new WebPage[n];
        // generate the files
        for (int i = 0; i < n; i++) {
            webPages[i] = new WebPage("file:/Users/jimmybao/CS/School/CS314H/prog7/testingFiles/file" + i + ".html");
            PrintWriter printWriter = new PrintWriter("testingFiles/file" + i + ".html");
            printWriter.println("<!DOCTYPE HTML>");

            // first set up the links that this webpage points to.
            for (int j = 0; j < n; j++) {
                // always point to the next one to make it easier
                if (j == (i+1) % n) {
                    printWriter.println("<a href=\"file" + j + ".html\">file" + j + ".html</a>");
                }
                else {
                    if (Math.random() < 0.33) {
                        printWriter.println("<a href=\"file" + j + ".html\">file" + j + ".html</a>");
                    }
                }
            }

            // write the text
            printWriter.print("<p>");
            for (String string : allStrings) {
                if (Math.random() < 0.33) {
                    printWriter.print(string + " ");
                    webPages[i].getWords().add(string);
                }
            }
            printWriter.println("</p>");

            printWriter.close();
        }

        webCrawler = new WebCrawler();
        webCrawler.main(new String[]{"file:///Users/jimmybao/CS/School/CS314H/prog7/testingFiles/file0.html"});

        webIndex = (WebIndex) webCrawler.getHandler().getIndex();

        Assertions.assertEquals(n, webIndex.getPages().size());

        webQueryEngine = new WebQueryEngine(webIndex);
    }

    private class WebPage {
        private List<String> words;
        private String url;

        public WebPage(String url) {
            words = new ArrayList<>();
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public List<String> getWords() {
            return words;
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

    private boolean isCharacter(char i) {
        char c = String.valueOf(i).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }
}