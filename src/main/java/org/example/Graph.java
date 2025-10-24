package org.example;

import java.util.*;

public class Graph {
    public final Set<String> vertices = new LinkedHashSet<>();
    public final List<Edge> edges = new ArrayList<>();
    public final Map<String, List<Edge>> adj = new HashMap<>();

    public Graph() {}

    public void addVertex(String v) {
        if (!vertices.contains(v)) {
            vertices.add(v);
            adj.put(v, new ArrayList<>());
        }
    }

    public void addEdge(String u, String v, double w) {
        addVertex(u);
        addVertex(v);
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        adj.get(v).add(e);
    }

    public int vertexCount() { return vertices.size(); }
    public int edgeCount() { return edges.size(); }

    public List<Edge> getEdgesCopy() { return new ArrayList<>(edges); }
}
