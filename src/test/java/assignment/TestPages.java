package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class TestPages {

    private int n, m;
    private List<String> allStrings;
    private WebPage[] webPages;
    private WebCrawler webCrawler;
    private WebIndex webIndex;

    @Test
    public void testPages() throws FileNotFoundException {
        generateGraph(10, 10);

        // for each webpage, check its instance variables
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            // get the right page
            Page current = null;
            for (int j = 0; j < webIndex.getPages().size(); j++) {
                current = (Page) ((ArrayList)(webIndex.getPages())).get(j);
                if (webPages[i].getUrl().equals(current.getURL().toString())) {
                    break;
                }
            }

            Assertions.assertEquals(webPages[i].getUrl(), current.getURL().toString());

            HashMap<String, List<Integer>> mapStringtoIndex = current.getMapStringtoIndex();
            List<String> mapIndextoString = current.getMapIndextoString();

            int firstIndex = mapStringtoIndex.get(webPages[i].getWords().get(0)).get(0);

            for (int j = 0; j < webPages[i].getWords().size(); j++) {
                String currentWord = webPages[i].getWords().get(j);
                Assertions.assertTrue(mapStringtoIndex.containsKey(currentWord) && mapStringtoIndex.get(currentWord).contains(j + firstIndex));
                Assertions.assertEquals(currentWord, mapIndextoString.get(j + firstIndex));
            }
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