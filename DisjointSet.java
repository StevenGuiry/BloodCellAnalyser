package sample;

import java.util.Arrays;

public class DisjointSet {
    // The number of elements in this disjoint set
    private int size;

    // Used to track the size of each of the component
    private int[] setSize;

    // id[i] points to the parent of i, if id[i] = i then i is a root node
    private int[] id;

    private int numComponents;

    public DisjointSet(int size) {

        if (size <= 0) throw new IllegalArgumentException("Size <= 0 is not allowed");

        this.size = numComponents = size;
        setSize = new int[size];
        id = new int[size];

        for (int i = 0; i < size; i++) {
            id[i] = i; // Link to itself (self root)
            setSize[i] = 1; // Each component is originally of size one
        }
    }

    //returns the size of the set p belongs to
    public int componentSize(int p) {
        return setSize[find(p)];
    }

    public int components() {
        return numComponents;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public int find(int p) {

        // Find the root of the component/set
        int root = p;
        while (root != id[root]) root = id[root];

        // Compress the path leading back to the root.
        // Doing this operation is called "path compression"
        while (p != root) {
            int next = id[p];
            id[p] = root;
            p = next;
        }

        return root;
    }

    public void unify(int p, int q) { //performs path compression when unified

        int root1 = find(p);
        int root2 = find(q);

        // These elements are already in the same group!
        if (root1 == root2) return;

        // Merge smaller component/set into the larger one.
        if (setSize[root1] < setSize[root2]) {
            setSize[root2] += setSize[root1];
            id[root1] = root2;
        } else {
            setSize[root1] += setSize[root2];
            id[root2] = root1;
        }
        numComponents--; //decreases by one when 2 sets are unified
    }

    // Return the number of elements in this Disjoint set
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "DisjointSet{" +
                "size=" + size +
                ", setSize=" + Arrays.toString(setSize) +
                ", id=" + Arrays.toString(id) +
                ", numComponents=" + numComponents +
                '}';
    }
}

