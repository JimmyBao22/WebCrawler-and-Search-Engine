package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TestWebGraph {

    private int n, m;
    private List<String> allStrings;
    private WebPage[] webPages;
    private WebCrawler webCrawler;
    private WebIndex webIndex;
    private WebQueryEngine webQueryEngine;

    @Test
    public void testPhrase() throws IOException, ClassNotFoundException {
        generateGraph(100, 100);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                Collection<Page> ret = webQueryEngine.query("\"" + allStrings.get(i) + " " + allStrings.get(j) + "\"");

                Collection<String> correctWebPageLinks = new HashSet<>();
                o: for (int k = 0; k < n; k++) {
                    List<String> words = webPages[k].getWords();
                    if (words.contains(allStrings.get(i)) && words.contains(allStrings.get(j))) {
                        for (int t = 0; t < words.size() - 1; t++) {
                            if (words.get(t).equals(allStrings.get(i)) && words.get(t+1).equals(allStrings.get(j))) {
                                correctWebPageLinks.add(webPages[k].getUrl());
                                continue o;
                            }
                        }
                    }
                }

                Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

                for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                    Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
                }
            }
        }
    }

    @Test
    public void testImplicitAnd() throws IOException, ClassNotFoundException {
        generateGraph(100, 100);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                Collection<Page> ret = webQueryEngine.query(allStrings.get(i) + " " + allStrings.get(j));

                Collection<String> correctWebPageLinks = new HashSet<>();
                for (int k = 0; k < n; k++) {
                    if (webPages[k].getWords().contains(allStrings.get(i)) &&
                            webPages[k].getWords().contains(allStrings.get(j))) {
//                    System.out.println(webPages[j].getUrl());
                        correctWebPageLinks.add(webPages[k].getUrl());
                    }
                }

                Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

                for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                    Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
                }
            }
        }
    }

    @Test
    public void testOr() throws IOException, ClassNotFoundException {
        generateGraph(100, 100);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                Collection<Page> ret = webQueryEngine.query("(" + allStrings.get(i) + "|" + allStrings.get(j) + ")");

                Collection<String> correctWebPageLinks = new HashSet<>();
                for (int k = 0; k < n; k++) {
                    if (webPages[k].getWords().contains(allStrings.get(i)) ||
                            webPages[k].getWords().contains(allStrings.get(j))) {
//                    System.out.println(webPages[j].getUrl());
                        correctWebPageLinks.add(webPages[k].getUrl());
                    }
                }

                Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

                for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                    Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
                }
            }
        }
    }

    @Test
    public void testAnd() throws IOException, ClassNotFoundException {
        generateGraph(100, 100);

        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                Collection<Page> ret = webQueryEngine.query("(" + allStrings.get(i) + "&" + allStrings.get(j) + ")");

                Collection<String> correctWebPageLinks = new HashSet<>();
                for (int k = 0; k < n; k++) {
                    if (webPages[k].getWords().contains(allStrings.get(i)) &&
                            webPages[k].getWords().contains(allStrings.get(j))) {
//                    System.out.println(webPages[j].getUrl());
                        correctWebPageLinks.add(webPages[k].getUrl());
                    }
                }

                Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

                for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                    Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
                }
            }
        }
    }

    @Test
    public void testNegativeWord() throws IOException, ClassNotFoundException {
        generateGraph(500, 500);

        for (int i = 0; i < m; i++) {
            Collection<Page> ret = webQueryEngine.query("!" + allStrings.get(i));

            Collection<String> correctWebPageLinks = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if (!webPages[j].getWords().contains(allStrings.get(i))) {
//                    System.out.println(webPages[j].getUrl());
                    correctWebPageLinks.add(webPages[j].getUrl());
                }
            }

            Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

            for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
            }
        }
    }

    @Test
    public void testWord() throws IOException, ClassNotFoundException {
        generateGraph(500, 500);

        for (int i = 0; i < m; i++) {
            Collection<Page> ret = webQueryEngine.query(allStrings.get(i));

            Collection<String> correctWebPageLinks = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if (webPages[j].getWords().contains(allStrings.get(i))) {
//                    System.out.println(webPages[j].getUrl());
                    correctWebPageLinks.add(webPages[j].getUrl());
                }
            }

            Assertions.assertEquals(correctWebPageLinks.size(), ret.size());

            for (Page page : ret) {
//                System.out.println(page.getURL().toString());
                Assertions.assertTrue(correctWebPageLinks.contains(page.getURL().toString()));
            }
        }
    }

    @Test
    public void testGenerateGraph() throws IOException, ClassNotFoundException {
        generateGraph(500, 500);
    }

    public void generateGraph(int n, int m) throws IOException, ClassNotFoundException {
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

        Assertions.assertEquals(n, webIndex.getPages().size());

        webQueryEngine = WebQueryEngine.fromIndex(
                (WebIndex) Index.load("index.db"));
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