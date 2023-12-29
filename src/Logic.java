import java.io.IOException;
import java.util.*;
import java.util.List;

public class Logic {

    // player logic
    public void userPlay() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose a level between 1 and 20 : ");
        int levelNumber = scanner.nextInt();
        String filename = "src/Levels/level_" + levelNumber + ".txt";
        scanner.nextLine(); // Consume the newline character

        Grid grid = new Grid(filename);
        // print the start grid .
        grid.printGrid();
        while (!Grid.isFinal(grid)) {
            System.out.println("Enter the coordinates for the cell (or enter 'q' to quit):");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting the program.");
                break; // Exit the loop if the user enters 'q'
            } else {
                try {
                    int row = Integer.parseInt(input.split(" ")[0]);
                    int col = Integer.parseInt(input.split(" ")[1]);
                    grid.move(row, col);
                    // Print the updated grid
                    grid.printGrid();
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid input. Please enter valid coordinates.");
                }
            }
        }
        if (Grid.isFinal(grid)) System.out.println("You Won!");
    }
    // here are the rest of the search strategies

    // ** blind search algorithms ** :

    // BFS
    public void BFS(Grid start) {
        Graph vertices = new Graph();
        Set<Grid> visited = new HashSet<>();
        Queue<Grid> queue = new LinkedList<>();
        Map<Grid, Grid> parentMap = new HashMap<>();

        parentMap.put(start, null); // start node has no parent .

        vertices.addNode(start);
        queue.add(start);
        visited.add(start);


        while (!queue.isEmpty()) {
            Grid node = queue.poll();


            // if target node is found, get the path and print it then break .
            if (Grid.isFinal(node)) {
                getPath(node, parentMap, vertices);
                System.out.println("Number of visited nodes : " + visited.size());
                break;
            }

            // process the node :
            node.getNextState(node, vertices);

            for (Grid neighbor : vertices.get(node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parentMap.put(neighbor, node);
                }
            }
        }
    }

    // DFS
    public void DFS(Grid start) {
        Graph vertices = new Graph();
        Stack<Grid> stack = new Stack<>();
        Set<Grid> visited = new HashSet<>();
        Map<Grid, Grid> parentMap = new HashMap<>();
        stack.push(start);



        while (!stack.isEmpty()) {
            Grid node = stack.pop();

            if (Grid.isFinal(node)) {
                getPath(node, parentMap, vertices);
                System.out.println("Number of visited nodes : " + visited.size());
                break;
            }
            if (!visited.contains(node)) {
                visited.add(node);
                // process the node :
                node.getNextState(node, vertices);
            }

            for (Grid neighbor : vertices.get(node)) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    parentMap.put(neighbor, node);
                }
            }
        }
    }

    // UCS : Uniform Cost Search
    public void UCS(Grid start) {
        Graph graph = new Graph();
        PriorityQueue<Grid> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Grid::getCost));
        Set<Grid> visited = new HashSet<>();
        Map<Grid, Grid> parentMap = new HashMap<>();

        priorityQueue.add(start);

        while (!priorityQueue.isEmpty()) {
            Grid node = priorityQueue.poll();

            if (Grid.isFinal(node)) {
                getPath(node, parentMap, graph);
                int maxDepth = calculateMaxDepth(graph,start);
                System.out.println("Number of visited nodes: " + visited.size());
                System.out.println("Priority Queue Size: " + priorityQueue.size());
                System.out.println("Maximum Depth: " + maxDepth);
                break;
            }

            if (!visited.contains(node)) {
                visited.add(node);
                // process the node:
                node.getNextState(node, graph);
            }
            for (Grid neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    // Remove the old version of the node from priorityQueue
                    priorityQueue.remove(neighbor);
                    // Add the node with a better (lower) cost to priorityQueue
                    priorityQueue.add(neighbor);
                    parentMap.put(neighbor, node);
                }
            }
        }
    }
    // ** informed search algorithms ** :

    // Hill Climbing :
    public void hillClimbing(Grid start) {
        Graph graph = new Graph();
        PriorityQueue<Grid> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Grid::getHeuristic));

        Set<Grid> visited = new HashSet<>();
        Map<Grid, Grid> parentMap = new HashMap<>();

        priorityQueue.add(start);

        while (!priorityQueue.isEmpty()) {
            Grid node = priorityQueue.poll();

            if (Grid.isFinal(node)) {
                getPath(node, parentMap, graph);
                int maxDepth = calculateMaxDepth(graph,start);
                System.out.println("Number of visited nodes: " + visited.size());
                System.out.println("Priority Queue Size: " + priorityQueue.size());
                System.out.println("Maximum Depth: " + maxDepth);
                break;
            }

            if (!visited.contains(node)) {
                visited.add(node);
                // process the node:
                node.getNextState(node, graph);


                for (Grid neighbor : graph.get(node)) {
                    if (!visited.contains(neighbor)) {
                        priorityQueue.add(neighbor);
                        parentMap.put(neighbor, node);
                    }
                }
            }
        }
    }

    // A*
    public void aStar(Grid start) {
        Graph graph = new Graph();
        PriorityQueue<Grid> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Grid::getTotalCost));

        Set<Grid> visited = new HashSet<>();
        Map<Grid, Grid> parentMap = new HashMap<>();


        priorityQueue.add(start);


        while (!priorityQueue.isEmpty()) {
            Grid node = priorityQueue.poll();


            if (Grid.isFinal(node)) {
                getPath(node, parentMap, graph);
                int maxDepth = calculateMaxDepth(graph,start);
                System.out.println("Number of visited nodes: " + visited.size());
                System.out.println("Priority Queue Size: " + priorityQueue.size());
                System.out.println("Maximum Depth: " + maxDepth);
                break;
            }

            if (!visited.contains(node)) {
                visited.add(node);
                // process the node:
                node.getNextState(node, graph);

                for (Grid neighbor : graph.get(node)) {
                    if (!visited.contains(neighbor)) {
                        priorityQueue.remove(neighbor);
                        priorityQueue.add(neighbor);
                        parentMap.put(neighbor, node);
                    }
                }
            }
        }
    }

    // print path method :
        public void getPath (Grid finalNode, Map < Grid, Grid > parentMap, Graph vertices){
            Grid currentNode = finalNode;
            List<Grid> path = new ArrayList<>();
            // Found the target node (final state)
            System.out.println("Target node found");
            while (currentNode != null) {
                path.add(currentNode);
                currentNode = parentMap.get(currentNode); // getting the parent node of currentNode
            }
            Collections.reverse(path);
            for (Grid grid : path) {
                grid.printGrid();
                grid.calculateGameHeuristic(grid);
                System.out.println("Grid Heuristic is : " + grid.getHeuristic());
                System.out.println();
            }
            System.out.println("Number of states (steps) to reach target node: " + (path.size() - 1));
            System.out.println("Path Size: " + path.size());
            System.out.println("Graph Size : " + vertices.getSize());
        }

    public int calculateMaxDepth(Graph graph, Grid start) {
        Set<Grid> visited = new HashSet<>();
        int maxDepth = 0;

        Stack<DFSNode> stack = new Stack<>();
        stack.push(new DFSNode(start, 1));

        while (!stack.isEmpty()) {
            DFSNode current = stack.pop();
            Grid currentNode = current.grid;
            int currentDepth = current.depth;

            visited.add(currentNode);
            maxDepth = Math.max(maxDepth, currentDepth);

            for (Grid neighbor : graph.get(currentNode)) {
                if (!visited.contains(neighbor)) {
                    stack.push(new DFSNode(neighbor, currentDepth + 1));
                }
            }
        }

        return maxDepth;
    }

    private static class DFSNode {
        Grid grid;
        int depth;

        public DFSNode(Grid grid, int depth) {
            this.grid = grid;
            this.depth = depth;
        }
    }

}