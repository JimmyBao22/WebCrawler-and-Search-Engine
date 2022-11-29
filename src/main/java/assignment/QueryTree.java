/*package assignment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class QueryTree {

    private String query;
    private QueryTreeNode root;
    private int index;

    public QueryTree(String query) {
        this.query = query.toLowerCase();
        root = new QueryTreeNode();
        index = 0;
        buildTree(root);
    }

    // iterate over query tree by recursion
    public Collection<Page> dfs(WebIndex webIndex, QueryTreeNode current) {
        Collection<Page> pages = new HashSet<>();
        if (current.getChildren().size() == 0) {
            // this is a word
            if (current.getNegative()) {
                // add all pages that DO NOT contain this current string word
                Collection<Page> unwantedPages = webIndex.getTrie().getPages(current.getString());
                for (Page page : webIndex.getPages()) {
                    if (!unwantedPages.contains(page)) {
                        pages.add(page);
                    }
                }
            }
            else {
                // add all pages that DO contain this current string word
                pages = webIndex.getTrie().getPages(current.getString());
            }
        }
        else {
            for (QueryTreeNode queryTreeNode : current.getChildren()) {
                Collection<Page> returned = dfs(webIndex, queryTreeNode);
//                if (returned == null) {
//                    continue;
//                }
                if (current.getString().equals("|")) {
                    for (Page page : returned) {
                        pages.add(page);
                    }
                }
                else {
                    if (pages.size() == 0) {
                        pages = returned;
                    }
                    else {
                        Collection<Page> updated = new HashSet<>();
                        for (Page page : returned) {
                            if (pages.contains(page)) {
                                updated.add(page);
                            }
                        }
                        pages = updated;
                    }
                }
            }
        }
        System.out.println(pages + " " + pages.size());
        return pages;
    }

    private void buildTree(QueryTreeNode current) {
        if (index >= query.length()) {
            return;
        }
        char curChar = query.charAt(index);
        index++;
        while (index < query.length() && query.charAt(index) == ' ') {
            index++;
        }
        if (curChar == '(') {
            // start to build
            QueryTreeNode child = new QueryTreeNode();
            current.getChildren().add(child);
            child.setParent(current);
            buildTree(child);
            buildTree(current);
        }
        else if (curChar == ')') {
            buildTree(current);
        }
        else if (curChar == '&' || curChar == '|') {
            // build based on right child
            current.setString(String.valueOf(curChar));
            QueryTreeNode child = new QueryTreeNode();
            current.getChildren().add(child);
            child.setParent(current);
            buildTree(child);
        }
        else if (isCharacter(index-1) || curChar == '!') {
            // character following english alphabet // TODO check if this is correct
            int i = index;
            while (i < query.length() && isCharacter(i)) {
                i++;
            }

            String word = curChar == '!' ? query.substring(index, i) : query.substring(index-1, i);
            current.setString(word);
            if (curChar == '!') {
                current.setNegative(true);
            }
            index = i;
        }
        else if (curChar == ' ') {
            // TODO can there be other stuff here?
            buildTree(current);
        }
        else {
            System.err.println("invalid query");
        }
    }

    // checks if what is at index is a character
    private boolean isCharacter(int index) {
        char c = String.valueOf(query.charAt(index)).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }

    public QueryTreeNode getRoot() {
        return root;
    }

    public class QueryTreeNode {

        private String string;
        private QueryTreeNode parent;
        private List<QueryTreeNode> children;
        private boolean negative;

        public QueryTreeNode() {
            children = new ArrayList<>();
            negative = false;
        }

        public QueryTreeNode getParent() {
            return parent;
        }

        public List<QueryTreeNode> getChildren() {
            return children;
        }

        public String getString() {
            return string;
        }

        public boolean getNegative() {
            return negative;
        }

        public void setString(String string) {
            this.string = string;
        }

        public void setParent(QueryTreeNode parent) {
            this.parent = parent;
        }

        public void setNegative(boolean negative) {
            this.negative = negative;
        }
    }
}

 */