package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TestWebGraphEdgeCase {

    private int n, m;
    private ArrayList<Integer>[] g;
    private List<String> allStrings;
    private WebPage[] webPages;
    private WebCrawler webCrawler;
    private WebIndex webIndex;

    // tests graphs that don't go through all vertices
    @Test
    public void testGenerateGraphEdgeCase() throws FileNotFoundException {
        generateGraphEdgeCase(500, 500);
        // see how many links I can react through the graph
        boolean[] visited = new boolean[n];

        // do a dfs through the graph to see what pages would get visited
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        while (!stack.isEmpty()) {
            int cur = stack.pop();
            if (visited[cur]) continue;
            visited[cur] = true;
            for (Integer to : g[cur]) {
                stack.push(to);
            }
        }

        int count = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) {
                count++;
                // get the right page
                boolean found = false;
                Page current;
                for (int j = 0; j < webIndex.getPages().size(); j++) {
                    current = (Page) ((ArrayList)(webIndex.getPages())).get(j);
                    if (webPages[i].getUrl().equals(current.getURL().toString())) {
                        found = true;
                        break;
                    }
                }

                // if the page was visited, it must have been found and added to webIndex
                Assertions.assertEquals(true, found);
            }
        }

        Assertions.assertEquals(count, webIndex.getPages().size());
    }

    public void generateGraphEdgeCase(int n, int m) throws FileNotFoundException {
        this.n = n;
        this.m = m;
        g = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            g[i] = new ArrayList<>();
        }

        // generate all strings that
        allStrings = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            allStrings.add(generateString());
        }

        webPages = new WebPage[n];
        // generate the files
        for (int i = 0; i < n; i++) {
            webPages[i] = new WebPage("file:/Users/[name]/CS/School/CS314H/prog7/testingFiles/file" + i + ".html");
            PrintWriter printWriter = new PrintWriter("testingFiles/file" + i + ".html");
            printWriter.println("<!DOCTYPE HTML>");

            // first set up the links that this webpage points to, and create the directed graph
            for (int j = 0; j < n; j++) {
                if (Math.random() < 0.33) {
                    printWriter.println("<a href=\"file" + j + ".html\">file" + j + ".html</a>");
                    g[i].add(j);
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
        webCrawler.main(new String[]{"file:///Users/[name]/CS/School/CS314H/prog7/testingFiles/file0.html"});

        webIndex = (WebIndex) webCrawler.getHandler().getIndex();
    }

    @Test
    public void testGenerateGraphEdgeCase2() throws FileNotFoundException {
        generateGraphEdgeCase2(500, 500);
    }

    // ensure that all webpages included don't have # and ?
    public void generateGraphEdgeCase2(int n, int m) throws FileNotFoundException {
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
            webPages[i] = new WebPage("file:/Users/[name]/CS/School/CS314H/prog7/testingFiles/file" + i + ".html");
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

                if (Math.random() < 0.2) {
                    printWriter.println("<a href=\"file" + j + ".html?a\">file" + j + ".html</a>");
                }
                if (Math.random() < 0.2) {
                    printWriter.println("<a href=\"file" + j + ".html#a\">file" + j + ".html</a>");
                }
                if (Math.random() < 0.2) {
                    printWriter.println("<a href=\"file" + j + ".html?a#b\">file" + j + ".html</a>");
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
        webCrawler.main(new String[]{"file:///Users/[name]/CS/School/CS314H/prog7/testingFiles/file0.html"});

        webIndex = (WebIndex) webCrawler.getHandler().getIndex();
        ArrayList<Page> pages = (ArrayList<Page>) webIndex.getPages();

        for (int i = 0; i < pages.size(); i++) {
            Assertions.assertTrue(!pages.get(i).getURL().toString().contains("?"));
            Assertions.assertTrue(!pages.get(i).getURL().toString().contains("#"));
        }
    }

    @Test
    public void testInvalidCases() {
        webCrawler.main(new String[]{});
        webCrawler.main(new String[]{"bad file path"});
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
}