package assignment;

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
        Collection<Page> pages = null;
        if (current.getChildren().size() == 0) {
            // this is a word
            pages = webIndex.getTrie().getPages(current.getString());
        }
        else {
            for (QueryTreeNode queryTreeNode : current.getChildren()) {
                Collection<Page> returned = dfs(webIndex, queryTreeNode);
//                if (returned == null) {
//                    continue;
//                }
                if (current.getString().equals("&")) {
                    for (Page page : returned) {
                        pages.add(page);
                    }
                }
                else {
                    if (pages == null) {
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
        return pages;
    }

    private void buildTree(QueryTreeNode current) {
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
        else if (isCharacter(index-1)) {
            // character following english alphabet // TODO check if this is correct
            int i = index-1;
            while (i < query.length() && isCharacter(i)) {
                i++;
            }

            String word = query.substring(index-1, i);
            current.setString(word);
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

        public QueryTreeNode() {
            children = new ArrayList<>();
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

        public void setString(String string) {
            this.string = string;
        }

        public void setParent(QueryTreeNode parent) {
            this.parent = parent;
        }
    }
}