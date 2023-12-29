import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Grid {
    private final int numRows;
    private final  int numCols;
    private final char[][] grid;
    private int cost;
    private int heuristic;
    public static final char LIT_CELL = 'W';
    public static final char EMPTY_CELL = ' ';
    public static final char BORDER_CELL = '#';


    // constructors

    // the initialization of the grid "level"
    public Grid(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        numRows = lines.size();
        numCols = lines.get(0).length();
        grid = new char[numRows][numCols];
        this.cost = 0;


        for (int i = 0; i < numRows; i++) {
            String row = lines.get(i);
            for (int j = 0; j < numCols; j++) {
                grid[i][j] = row.charAt(j);
            }
        }
    }
    // another constructor for non-start state grids :
    public Grid(char[][] grid) {
        this.numRows = grid.length;
        this.numCols = grid[0].length;
        this.grid = new char[numRows][numCols];
        // Perform deep copy of the grid
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, numCols);
        }
    }

    // setters and getters
    public char[][] getGrid() {
        return grid;
    }
    public int getCost() {return cost;}
    public void setCost(int cost){this.cost = cost;}
    public int getHeuristic() { return heuristic;}
    public void setHeuristic(int heuristic) { this.heuristic = heuristic;}
    public int getNumRows() {
        return numRows;
    }
    public int getNumCols() {
        return numCols;
    }


    public static int getTotalCost(Grid grid) {
        return  grid.getHeuristic() + grid.getCost();
    }

    // Get all possible moves
    public List<Point> checkMoves() {
        List<Point> possible_moves = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == Grid.LIT_CELL)
                    possible_moves.add(new Point(i, j));
            }
        }
        return possible_moves;
    }

    // Create a deep copy the grid
    public static char[][] deepCopyGrid(char[][] originalGrid) {
        int numRows = originalGrid.length;
        return Arrays.copyOf(originalGrid, numRows);
    }

    // Heuristic Method
    public void calculateGameHeuristic(Grid grid) {
        double totalEuclideanDistance = 0;
        int totalWCells = 0;
        char[][] gridArray = grid.getGrid();

        for (int i = 0; i < grid.getNumRows(); i++) {
            for (int j = 0; j < grid.getNumCols(); j++) {
                if (gridArray[i][j] == Grid.LIT_CELL) {
                    totalWCells++;

                    for (int x = 0; x < grid.getNumRows(); x++) {
                        for (int y = 0; y < grid.getNumCols(); y++) {
                            if (gridArray[x][y] == Grid.LIT_CELL) {
                                double distance = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
                                totalEuclideanDistance += distance;
                            }
                        }
                    }
                }
            }
        }

        // Calculate the average Euclidean distance
        double averageEuclideanDistance = totalWCells == 0 ? 0 : totalEuclideanDistance / totalWCells;

        // Set the heuristic value
        grid.setHeuristic((int) averageEuclideanDistance);
    }

    // get all possible next states then add them to the graph.
    public void getNextState(Grid grid, Graph graph) {
        List<Point> possible_moves = checkMoves();
        char[][] currentGrid = grid.getGrid();
        int parentCost = grid.cost;
        // Add the current grid to the graph if it doesn't exist
        if (graph.containsNode(grid)) {
            graph.addNode(grid);
        }

        for (Point move : possible_moves) {
            // Create a new copy of the grid for each move
            char[][] copiedGrid = deepCopyGrid(currentGrid);
            Grid copiedGridInstance = new Grid(copiedGrid);
            copiedGridInstance.setCost(parentCost + 1);

            // Perform the move on the copied grid
            copiedGridInstance.move((int) move.getX(), (int) move.getY());
            calculateGameHeuristic(copiedGridInstance);

            // Add the resulting grid to the graph only if it represents a valid move
            if (!Arrays.deepEquals(copiedGridInstance.getGrid(), currentGrid)) {
                // Add the copied grid to the graph if it doesn't exist
                if (graph.containsNode(copiedGridInstance)) {
                    graph.addNode(copiedGridInstance);
                }
                // Add an edge from the current grid to the copiedGridInstance
                graph.addEdge(grid, copiedGridInstance);

            }
        }
    }

    // Print the grid
    public void printGrid() {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + "  ");
            }
            System.out.println();
        }
    }

    public void flipCellState(char[][] grid, int row, int col) {
        if (grid[row][col] != Grid.BORDER_CELL) {
            grid[row][col] = (grid[row][col] == Grid.LIT_CELL) ? Grid.EMPTY_CELL : Grid.LIT_CELL;
        }
    }

    public void move(int row, int col) {

        // Check if the selected cell is within the grid boundaries
        if (row >= 0 && row < numRows && col >= 0 && col < numCols && grid[row][col] == Grid.LIT_CELL) {
            flipCellState(grid, row, col);

            // Flip upper cell if it is within grid boundaries and not a wall
            if (row - 1 >= 0 && grid[row - 1][col] != Grid.BORDER_CELL) {
                flipCellState(grid, row - 1, col);
            }

            // Flip lower cell if it is within grid boundaries and not a wall
            if (row + 1 < numRows && grid[row + 1][col] != Grid.BORDER_CELL) {
                flipCellState(grid, row + 1, col);
            }

            // Flip left cell if it is within grid boundaries and not a wall
            if (col - 1 >= 0 && grid[row][col - 1] != Grid.BORDER_CELL) {
                flipCellState(grid, row, col - 1);
            }

            // Flip right cell if it is within grid boundaries and not a wall
            if (col + 1 < numCols && grid[row][col + 1] != Grid.BORDER_CELL) {
                flipCellState(grid, row, col + 1);
            }
        } else {
            System.out.println("Invalid move. Please select a lit cell within the grid boundaries.");
        }
    }

    public static boolean isFinal(Grid grid) {
        char[][] gridArray = grid.getGrid();
        return Arrays.stream(gridArray)
                .flatMapToInt(row -> new String(row).chars())
                .noneMatch(cell -> cell == Grid.LIT_CELL);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grid otherGrid = (Grid) obj;
        return Arrays.deepEquals(this.grid, otherGrid.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.grid);
    }


    public static void main(String[] args) throws IOException {
        Grid grid = new Grid("src/Levels/level_7.txt");
        Logic L = new Logic();
        System.out.println("Would you like to play? (y/n) ");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if(answer.equalsIgnoreCase("y"))
            L.userPlay();
        else {
            System.out.println("""
                    Choose an Algorithm :
                    1. BFS\s
                    2. DFS\s
                    3. UFS\s
                    4. Hill Climbing\s
                    5. A*\s
                    """);
            int algorithm = scanner.nextInt();
            switch (algorithm) {
                case 1 -> L.BFS(grid);
                case 2 -> L.DFS(grid);
                case 3 -> L.UCS(grid);
                case 4 -> L.hillClimbing(grid);
                case 5 -> L.aStar(grid);
            }
        }
    }
}


