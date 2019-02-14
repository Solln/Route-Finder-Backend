package main.GraphElements;

public class GraphNode {

    private GraphNode mid, left, right;
    private RouteEdge value;

    public GraphNode(RouteEdge value){
        mid = null;
        left = null;
        right = null;
        this.value = value;
    }

    public GraphNode getMid() {
        return mid;
    }

    public void setMid(GraphNode mid) {
        this.mid = mid;
    }

    public GraphNode getLeft() {
        return left;
    }

    public void setLeft(GraphNode left) {
        this.left = left;
    }

    public GraphNode getRight() {
        return right;
    }

    public void setRight(GraphNode right) {
        this.right = right;
    }

    public RouteEdge getValue() {
        return value;
    }

    public void setValue(RouteEdge value) {
        this.value = value;
    }
}
