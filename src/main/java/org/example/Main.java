package org.example;

import com.google.gson.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    static class OutputEntry {
        int graphId;
        int V;
        int E;
        Map<String, Object> kruskal;
        Map<String, Object> prim;
    }

    public static void main(String[] args) throws IOException {
        String[] inputs = {
                "data/small_graphs.json",
                "data/medium_graphs.json",
                "data/large_graphs.json",
                "data/extra_large_graphs.json"
        };

        Files.createDirectories(Paths.get("output"));

        for (String inputPath : inputs) {
            String datasetName = inputPath.substring(inputPath.lastIndexOf('/') + 1)
                    .replace(".json", "");
            String outputPath = "output/results_" + datasetName + ".json";

            System.out.println("\n=== Processing " + datasetName + " ===");

            String text = new String(Files.readAllBytes(Paths.get(inputPath)));
            JsonObject root = JsonParser.parseString(text).getAsJsonObject();
            JsonArray graphs = root.getAsJsonArray("graphs");

            List<OutputEntry> allResults = new ArrayList<>();

            for (JsonElement ge : graphs) {
                JsonObject gobj = ge.getAsJsonObject();
                int id = gobj.get("id").getAsInt();

                Graph graph = new Graph();
                JsonArray nodes = gobj.getAsJsonArray("nodes");
                for (JsonElement n : nodes) graph.addVertex(n.getAsString());

                JsonArray edges = gobj.getAsJsonArray("edges");
                for (JsonElement e : edges) {
                    JsonObject eo = e.getAsJsonObject();
                    String from = eo.get("from").getAsString();
                    String to = eo.get("to").getAsString();
                    double w = eo.get("weight").getAsDouble();
                    graph.addEdge(from, to, w);
                }

                System.out.printf("Graph %d → V=%d, E=%d%n", id, graph.vertexCount(), graph.edgeCount());

                // --- Kruskal ---
                KruskalMST.Result kr = KruskalMST.run(graph);

                // --- Prim ---
                PrimMST.Result pr = PrimMST.run(graph);

                OutputEntry out = new OutputEntry();
                out.graphId = id;
                out.V = graph.vertexCount();
                out.E = graph.edgeCount();

                Map<String, Object> kmap = new LinkedHashMap<>();
                kmap.put("totalWeight", kr.totalWeight);
                kmap.put("mstEdgeCount", kr.mstEdges.size());
                kmap.put("timeMs", kr.timeMs);
                kmap.put("comparisons", kr.comparisons);
                kmap.put("unions", kr.unions);
                kmap.put("edges", serializeEdges(kr.mstEdges));
                out.kruskal = kmap;

                Map<String, Object> pmap = new LinkedHashMap<>();
                pmap.put("totalWeight", pr.totalWeight);
                pmap.put("mstEdgeCount", pr.mstEdges.size());
                pmap.put("timeMs", pr.timeMs);
                pmap.put("edgeChecks", pr.edgeChecks);
                pmap.put("edges", serializeEdges(pr.mstEdges));
                out.prim = pmap;

                allResults.add(out);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            JsonObject rootOut = new JsonObject();
            JsonArray resultsArray = new JsonArray();

            for (OutputEntry entry : allResults) {
                JsonObject obj = new JsonObject();
                obj.addProperty("graph_id", entry.graphId);

                JsonObject inputStats = new JsonObject();
                inputStats.addProperty("vertices", entry.V);
                inputStats.addProperty("edges", entry.E);
                obj.add("input_stats", inputStats);

                // --- Prim ---
                JsonObject prim = new JsonObject();
                prim.add("mst_edges", gson.toJsonTree(entry.prim.get("edges")));
                prim.addProperty("total_cost", round2((double) entry.prim.get("totalWeight")));
                prim.addProperty("operations_count", ((Number) entry.prim.get("edgeChecks")).longValue());
                prim.addProperty("execution_time_ms", ((Number) entry.prim.get("timeMs")).doubleValue());
                obj.add("prim", prim);

                // --- Kruskal ---
                JsonObject kr = new JsonObject();
                kr.add("mst_edges", gson.toJsonTree(entry.kruskal.get("edges")));
                kr.addProperty("total_cost", round2((double) entry.kruskal.get("totalWeight")));
                kr.addProperty("operations_count",
                        ((Number) entry.kruskal.get("comparisons")).longValue()
                                + ((Number) entry.kruskal.get("unions")).longValue());
                kr.addProperty("execution_time_ms", ((Number) entry.kruskal.get("timeMs")).doubleValue());
                obj.add("kruskal", kr);

                resultsArray.add(obj);
            }

            rootOut.add("results", resultsArray);
            Files.write(Paths.get(outputPath), gson.toJson(rootOut).getBytes());
            System.out.println("Saved formatted JSON: " + outputPath);

            // CSV Summary
            writeSummaryCsv(allResults, datasetName);
            System.out.println("Summary CSV updated for " + datasetName);
        }
    }

    private static List<Map<String, Object>> serializeEdges(List<Edge> edges) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Edge e : edges) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("from", e.u);
            m.put("to", e.v);
            m.put("weight", e.w);
            list.add(m);
        }
        return list;
    }

    private static void writeSummaryCsv(List<OutputEntry> results, String datasetName) throws IOException {
        Path csvPath = Paths.get("output/results_summary.csv");
        boolean writeHeader = !Files.exists(csvPath);

        List<String> lines = new ArrayList<>();

        if (writeHeader) {
            lines.add("dataset,id,algo_name,vertices,execute_time_ms,operation_count,total_cost");
        }

        for (OutputEntry entry : results) {
            int id = entry.graphId;
            int v = entry.V;

            // Kruskal
            lines.add(String.join(",",
                    datasetName, String.valueOf(id), "Kruskal",
                    String.valueOf(v),
                    entry.kruskal.get("timeMs").toString(),
                    String.valueOf(((Number) entry.kruskal.get("comparisons")).longValue()
                            + ((Number) entry.kruskal.get("unions")).longValue()),
                    entry.kruskal.get("totalWeight").toString()
            ));

            // Prim
            lines.add(String.join(",",
                    datasetName, String.valueOf(id), "Prim",
                    String.valueOf(v),
                    entry.prim.get("timeMs").toString(),
                    entry.prim.get("edgeChecks").toString(),
                    entry.prim.get("totalWeight").toString()
            ));
        }

        Files.write(csvPath, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
