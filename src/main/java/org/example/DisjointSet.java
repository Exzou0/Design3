package org.example;

import java.util.*;

public class DisjointSet {
    private final Map<String, String> parent = new HashMap<>();
    private final Map<String, Integer> rank = new HashMap<>();

    public void makeSet(Collection<String> items) {
        for (String v : items) {
            parent.put(v, v);
            rank.put(v, 0);
        }
    }

    public String find(String v) {
        String p = parent.get(v);
        if (p == null) return null;
        if (!p.equals(v)) parent.put(v, find(p));
        return parent.get(v);
    }

    public boolean union(String a, String b) {
        String ra = find(a);
        String rb = find(b);
        if (ra == null || rb == null) return false;
        if (ra.equals(rb)) return false;
        int rA = rank.getOrDefault(ra, 0);
        int rB = rank.getOrDefault(rb, 0);
        if (rA < rB) parent.put(ra, rb);
        else if (rA > rB) parent.put(rb, ra);
        else {
            parent.put(rb, ra);
            rank.put(ra, rA + 1);
        }
        return true;
    }
}
