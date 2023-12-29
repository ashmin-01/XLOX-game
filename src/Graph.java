import java.util.*;
import java.util.List;

class Graph {
    private final Map<Grid, List<Grid>> vertices;

    public Graph() {
        this.vertices = new HashMap<>();
    }

    public List<Grid> get(Grid node) {
        return vertices.getOrDefault(node, Collections.emptyList());
    }


    public boolean containsNode(Grid grid) {
        return vertices.get(grid) == null;
    }

    // Method to add a node to the graph
    public void addNode(Grid node) {
        vertices.put(node, new ArrayList<>());
    }
    // Method to add an Edge between two grids - nodes
    public void addEdge(Grid sourceNode, Grid destinationNode) {
        if (!vertices.containsKey(sourceNode) || !vertices.containsKey(destinationNode)) {
            throw new IllegalArgumentException("Source or destination node does not exist in the graph.");
        }
        vertices.get(sourceNode).add(destinationNode);
    }

    // get graph size
    public int getSize() {
        return vertices.size();
    }
}