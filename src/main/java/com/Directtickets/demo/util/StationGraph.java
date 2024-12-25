package com.Directtickets.demo.util;

import java.util.*;

public class StationGraph {
    private final Map<String, Map<String, Integer>> graph;

    public StationGraph() {
        graph = new HashMap<>();
    }

    public void addStation(String station) {
        graph.putIfAbsent(station, new HashMap<>());
    }

    public void addRoute(String fromStation, String toStation, int weight) {
        if (!graph.containsKey(fromStation) || !graph.containsKey(toStation)) {
            throw new IllegalArgumentException("Both stations must exist in the graph.");
        }
        graph.get(fromStation).put(toStation, weight);
        graph.get(toStation).put(fromStation, weight); // Undirected graph assumption
    }

    public List<String> dijkstra(String start, String end) {
        if (!graph.containsKey(start) || !graph.containsKey(end)) {
            throw new IllegalArgumentException("Both start and end stations must exist in the graph.");
        }

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));
        Map<String, Integer> minDistance = new HashMap<>();
        Map<String, String> prevStation = new HashMap<>();
        Set<String> visited = new HashSet<>();
        pq.add(new Node(start, 0));
        minDistance.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentStation = current.station;

            if (visited.contains(currentStation)) {
                continue;
            }
            visited.add(currentStation);

            if (currentStation.equals(end)) {
                break;
            }

            for (Map.Entry<String, Integer> neighbor : graph.get(currentStation).entrySet()) {
                String nextStation = neighbor.getKey();
                int newDist = current.cost + neighbor.getValue();

                if (newDist < minDistance.getOrDefault(nextStation, Integer.MAX_VALUE)) {
                    minDistance.put(nextStation, newDist);
                    prevStation.put(nextStation, currentStation);
                    pq.add(new Node(nextStation, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        for (String at = end; at != null; at = prevStation.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        if (path.isEmpty() || !path.getFirst().equals(start)) {
            return Collections.emptyList();
        } else {
            return path;
        }
    }

    public List<List<String>> yenKShortestPaths(String source, String destination, int k) {
        if (!graph.containsKey(source) || !graph.containsKey(destination)) {
            throw new IllegalArgumentException("Both source and destination stations must exist in the graph.");
        }

        List<List<String>> kShortestPaths = new ArrayList<>();
        List<String> shortestPath = dijkstra(source, destination);

        if (shortestPath.isEmpty()) {
            return kShortestPaths;
        }

        kShortestPaths.add(shortestPath);
        PriorityQueue<Path> potentialPaths = new PriorityQueue<>(Comparator.comparingInt(p -> p.cost));

        for (int i = 1; i < k; i++) {
            for (int j = 0; j < shortestPath.size() - 1; j++) {
                String spurNode = shortestPath.get(j);
                List<String> rootPath = new ArrayList<>(shortestPath.subList(0, j + 1));

                Map<String, Map<String, Integer>> removedEdges = new HashMap<>();

                for (List<String> path : kShortestPaths) {
                    if (rootPath.equals(path.subList(0, j + 1))) {
                        String from = path.get(j);
                        String to = path.get(j + 1);

                        if (graph.get(from) != null && graph.get(from).containsKey(to)) {
                            removedEdges.putIfAbsent(from, new HashMap<>());
                            removedEdges.get(from).put(to, graph.get(from).get(to));
                            graph.get(from).remove(to);
                        }
                    }
                }

                List<String> spurPath = dijkstra(spurNode, destination);

                if (!spurPath.isEmpty()) {
                    List<String> totalPath = new ArrayList<>(rootPath);
                    totalPath.addAll(spurPath.subList(1, spurPath.size()));

                    int pathCost = calculatePathCost(totalPath);
                    potentialPaths.add(new Path(totalPath, pathCost));
                }

                for (Map.Entry<String, Map<String, Integer>> entry : removedEdges.entrySet()) {
                    String from = entry.getKey();
                    for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
                        graph.get(from).put(neighbor.getKey(), neighbor.getValue());
                    }
                }
            }

            if (potentialPaths.isEmpty()) {
                break;
            }

            Path nextShortestPath = potentialPaths.poll();
            kShortestPaths.add(nextShortestPath.path);
        }

        return kShortestPaths;
    }

    private int calculatePathCost(List<String> path) {
        int totalCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            totalCost += graph.get(from).get(to);
        }
        return totalCost;
    }

    private static class Node {
        String station;
        int cost;

        Node(String station, int cost) {
            this.station = station;
            this.cost = cost;
        }
    }

    private static class Path {
        List<String> path;
        int cost;

        Path(List<String> path, int cost) {
            this.path = path;
            this.cost = cost;
        }
    }
}