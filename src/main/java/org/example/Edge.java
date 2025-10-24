package org.example;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    public final String u;
    public final String v;
    public final double w;

    public Edge(String u, String v, double w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.w, other.w);
    }

    @Override
    public String toString() {
        return String.format("%s-%s(%.2f)", u, v, w);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;
        Edge e = (Edge) o;
        return ((u.equals(e.u) && v.equals(e.v)) || (u.equals(e.v) && v.equals(e.u))) && Double.compare(w, e.w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, w) + Objects.hash(v, u, w);
    }
}
