import java.util.*;
import java.awt.*;

public class AStarComparison {

    static class Node {
        int x, y;
        double g, h;
        Node parent;

        Node(int x, int y, double g, double h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        double f() { return g + h; }
    }

    static class Grid {
        int size;
        int[][] grid;
        Point start, goal;
        Random rand = new Random();

        Grid(int size, double obstacleProb) {
            this.size = size;
            grid = new int[size][size];
            generateGrid(obstacleProb);
        }

        void generateGrid(double obstacleProb) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    grid[i][j] = (Math.random() < obstacleProb) ? 1 : 0; // 1 = obstacle
                }
            }
            do {
                start = new Point(rand.nextInt(size), rand.nextInt(size));
            } while (grid[start.x][start.y] == 1);

            do {
                goal = new Point(rand.nextInt(size), rand.nextInt(size));
            } while (grid[goal.x][goal.y] == 1 || goal.equals(start));
        }

        boolean isValid(int x, int y) {
            return x >= 0 && y >= 0 && x < size && y < size && grid[x][y] == 0;
        }
    }

    interface Heuristic {
        double compute(Point a, Point b);
    }

    static double aStar(Grid grid, Heuristic heuristic) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        boolean[][] visited = new boolean[grid.size][grid.size];
        Node startNode = new Node(grid.start.x, grid.start.y, 0, heuristic.compute(grid.start, grid.goal), null);
        open.add(startNode);

        int nodesExpanded = 0;
        Node goalNode = null;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!open.isEmpty()) {
            Node curr = open.poll();
            nodesExpanded++;

            if (curr.x == grid.goal.x && curr.y == grid.goal.y) {
                goalNode = curr;
                break;
            }

            if (visited[curr.x][curr.y]) continue;
            visited[curr.x][curr.y] = true;

            for (int[] d : dirs) {
                int nx = curr.x + d[0], ny = curr.y + d[1];
                if (grid.isValid(nx, ny) && !visited[nx][ny]) {
                    double gNew = curr.g + 1;
                    double hNew = heuristic.compute(new Point(nx, ny), grid.goal);
                    open.add(new Node(nx, ny, gNew, hNew, curr));
                }
            }
        }

        double pathLen = 0;
        if (goalNode != null) {
            Node temp = goalNode;
            while (temp.parent != null) {
                pathLen++;
                temp = temp.parent;
            }
        }

        return pathLen > 0 ? pathLen : Double.POSITIVE_INFINITY; // Infinity if no path
    }

    public static void main(String[] args) {
        Heuristic manhattan = (a, b) -> Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
        Heuristic euclidean = (a, b) -> Math.hypot(a.x - b.x, a.y - b.y);
        Heuristic diagonal = (a, b) -> Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));

        int trials = 10;
        int gridSize = 20;
        double obstacleProb = 0.25;

        double[] totalTime = new double[3];
        double[] totalPath = new double[3];
        int[] success = new int[3];

        for (int t = 0; t < trials; t++) {
            Grid grid = new Grid(gridSize, obstacleProb);

            Heuristic[] heuristics = {manhattan, euclidean, diagonal};
            for (int i = 0; i < heuristics.length; i++) {
                long start = System.nanoTime();
                double pathLen = aStar(grid, heuristics[i]);
                long end = System.nanoTime();

                if (pathLen < Double.POSITIVE_INFINITY) {
                    success[i]++;
                    totalPath[i] += pathLen;
                }
                totalTime[i] += (end - start) / 1e6;
            }
        }

        String[] names = {"Manhattan", "Euclidean", "Diagonal"};
        System.out.println("Heuristic Comparison (" + trials + " runs)");
        System.out.println("--------------------------------------");
        for (int i = 0; i < names.length; i++) {
            System.out.printf("%s:\n", names[i]);
            System.out.printf("  Success Rate: %.2f%%\n", (success[i] * 100.0 / trials));
            System.out.printf("  Avg Path Length: %.2f\n", (success[i] > 0 ? totalPath[i] / success[i] : 0));
            System.out.printf("  Avg Time (ms): %.2f\n\n", totalTime[i] / trials);
        }
    }
}
