package assignment;

public class QueryTree {

    private String query;
    private QueryTreeNode root;
    private int index;

    public QueryTree(String query) {
        this.query = query;
        root = new QueryTreeNode();
        index = 0;
        buildTree(root);
    }

    private void buildTree(QueryTreeNode current) {
        char curChar = query.charAt(index);
        index++;
        if (curChar == '(') {
            // build based on left child
            buildTree(current.getLeft());
            current.getLeft().setParent(current);
            buildTree(current);
        }
        else if (curChar == ')') {
            buildTree(current);
        }
        else if (curChar == '&' || curChar == '|') {
            // build based on right child
            current.setString(String.valueOf(curChar));
            buildTree(current.getRight());
            current.getRight().setParent(current);
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
            return;
        }
    }

    // checks if what is at index is a character
    private boolean isCharacter(int index) {
        char c = String.valueOf(query.charAt(index)).toLowerCase().charAt(0);
        return (c - 'a' >= 0 && c - 'z' <= 0) || (c - '0' >= 0 && c - '9' <= 0);
    }

    public class QueryTreeNode {

        private String string;
        private QueryTreeNode left, right, parent;

        public QueryTreeNode() {
//            left = new QueryTreeNode();
//            right = new QueryTreeNode();
        }

        public QueryTreeNode getLeft() {
            if (left == null) {
                left = new QueryTreeNode();
            }
            return left;
        }

        public QueryTreeNode getRight() {
            if (right == null) {
                right = new QueryTreeNode();
            }
            return right;
        }

        public QueryTreeNode getParent() {
            return parent;
        }

        public void setString(String string) {
            this.string = string;
        }

        public void setParent(QueryTreeNode parent) {
            this.parent = parent;
        }
    }
}