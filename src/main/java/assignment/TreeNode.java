package assignment;

public class TreeNode {

    private Token token;
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
