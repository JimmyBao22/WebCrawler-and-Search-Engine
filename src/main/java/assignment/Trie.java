package assignment;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trie implements Serializable {

    private static final long serialVersionUID = 5462433323595652858L;
    private HashMap<Character, Trie> map;
    private boolean isWord;
    private List<Page> pages;

    public Trie() {
        map = new HashMap<>();
        isWord = false;
        pages = new ArrayList<>();
    }

    public void add(String word, Page storePage) {
        word = word.toLowerCase();
        Trie current = this;
        for (int i = 0; i < word.length(); i++) {
            if (!current.getMap().containsKey(word.charAt(i))) {
                current.getMap().put(word.charAt(i), new Trie());
            }
            current = current.getMap().get(word.charAt(i));
        }

        current.setWord(true);

        if (storePage != null) {
            // store this page in the list, as this page contains this word
            current.getPages().add(storePage);
        }
    }

    // TODO not sure if this is needed, since if isWord = true, then pages isn't empty
    public boolean contains(String word) {
        word = word.toLowerCase();
        Trie current = this;
        for (int i = 0; i < word.length(); i++) {
            if (!current.getMap().containsKey(word.charAt(i))) {
                return false;
            }
            current = current.getMap().get(word.charAt(i));
        }

        return current.getIsWord();
    }

    public List<Page> getPages(String word) {
        word = word.toLowerCase();
        Trie current = this;
        for (int i = 0; i < word.length(); i++) {
            if (!current.getMap().containsKey(word.charAt(i))) {
                return null;
            }
            current = current.getMap().get(word.charAt(i));
        }

        return current.getPages();
    }

    public HashMap<Character, Trie> getMap() {
        return map;
    }

    public void setWord(boolean isWord) {
        this.isWord = isWord;
    }

    public List<Page> getPages() {
        return pages;
    }

    public boolean getIsWord() {
        return isWord;
    }
}