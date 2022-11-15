import java.io.File;

public class Batch {
    public int size;
    public File[] Files;
    public UnionFind EqData;
    public Batch(int size){
        this.size=size;
        Files=new File[size];
        EqData=new UnionFind(size);
    }
}
class UnionFind {
    Node[] node;
    private static class Node{
        int parent;
        boolean root;

        private Node(){
            parent = 1;
            root = true;
        }
    }
    public UnionFind(int n){
        node = new Node[n + 1];
        for(int e= 0; e <= n; e++){
            node[e] = new Node();
        }
    }
    public int find(int e){
        while(!node[e].root){
            e = node[e].parent;
        }
        return e;
    }
    public void union(int a, int b){
        node[a].parent += node[b].parent;
        node[b].root = false;
        node[b].parent = a;
    }
}
