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
        Map<String,Object> kruskal;
        Map<String,Object> prim;
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

                System.out.printf("Graph %d → V=%d, E=%d%n",
                        id, graph.vertexCount(), graph.edgeCount());

                // --- Kruskal ---
                KruskalMST.Result kr = KruskalMST.run(graph);

                // --- Prim ---
                PrimMST.Result pr = PrimMST.run(graph);

                // запись результата
                OutputEntry out = new OutputEntry();
                out.graphId = id;
                out.V = graph.vertexCount();
                out.E = graph.edgeCount();

                Map<String,Object> kmap = new LinkedHashMap<>();
                kmap.put("totalWeight", kr.totalWeight);
                kmap.put("mstEdgeCount", kr.mstEdges.size());
                kmap.put("timeMs", kr.timeMs);
                kmap.put("comparisons", kr.comparisons);
                kmap.put("unions", kr.unions);
                kmap.put("edges", serializeEdges(kr.mstEdges));
                out.kruskal = kmap;

                Map<String,Object> pmap = new LinkedHashMap<>();
                pmap.put("totalWeight", pr.totalWeight);
                pmap.put("mstEdgeCount", pr.mstEdges.size());
                pmap.put("timeMs", pr.timeMs);
                pmap.put("edgeChecks", pr.edgeChecks);
                pmap.put("edges", serializeEdges(pr.mstEdges));
                out.prim = pmap;

                allResults.add(out);
            }

            // сохраняем результаты
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String outJson = gson.toJson(allResults);
            Files.write(Paths.get(outputPath), outJson.getBytes());
            System.out.println("✅ Saved: " + outputPath);
        }
    }

    private static List<Map<String,Object>> serializeEdges(List<Edge> edges) {
        List<Map<String,Object>> list = new ArrayList<>();
        for (Edge e : edges) {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("u", e.u);
            m.put("v", e.v);
            m.put("w", e.w);
            list.add(m);
        }
        return list;
    }
}
