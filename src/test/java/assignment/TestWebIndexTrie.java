package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestWebIndexTrie {

    @Test
    void testContains() throws IOException {
        WebCrawler webCrawler = new WebCrawler();
        webCrawler.main(new String[]{"file:///Users/jimmybao/CS/School/CS314H/prog7/president96/test.html"});

        WebIndex webIndex = (WebIndex) webCrawler.getHandler().getIndex();
        Assertions.assertTrue(webIndex.getTrie().contains("hello"));
        Assertions.assertTrue(webIndex.getTrie().contains("one"));
        Assertions.assertTrue(webIndex.getTrie().contains("two"));
        Assertions.assertTrue(webIndex.getTrie().contains("bye"));
        Assertions.assertTrue(webIndex.getTrie().contains("hello"));
        Assertions.assertTrue(!webIndex.getTrie().contains("hellooo"));
        Assertions.assertTrue(!webIndex.getTrie().contains("onetwo"));

        System.out.println(webIndex.getTrie().getPages("hello"));

        Assertions.assertTrue(webIndex.getTrie().contains("second"));
    }
}