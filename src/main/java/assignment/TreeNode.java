package assignment;

public class TreeNode {

    // stores the corresponding token that is at this node
    private Token token;
    // left and right children
    private TreeNode left, right;

    public TreeNode(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }
}
