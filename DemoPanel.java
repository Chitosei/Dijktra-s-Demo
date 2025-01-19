import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class DemoPanel extends JPanel {

    // SCREEN SETTING
    final int maxCol = 15;
    final int maxRow = 10;
    final int nodeSize = 70;
    final int screenWidth = nodeSize * maxCol;
    final int screenHeight = nodeSize * maxRow;

    // NODE
    Node[][] node = new Node[maxCol][maxRow];
    Node startNode, destinationNode, currentNode;
    PriorityQueue<Node> openList;
    Map<Node, Integer> distances;
    Map<Node, Node> predecessors;

    public DemoPanel() {
        this.setPreferredSize(new DimensionUIResource(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setLayout(new GridLayout(maxRow, maxCol));

        // PLACE NODES
        int col = 0;
        int row = 0;
        while (col < maxCol && row < maxRow) {
            node[col][row] = new Node(col, row);

            node[col][row].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Node clickedNode = (Node) e.getSource();
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        if (clickedNode.solid) // Right-click to set solid
                            clickedNode.resetToPreviousState();
                    } else {
                        if (!clickedNode.solid)
                            clickedNode.setAsSolid(); // Left-click (example action)
                    }
                    requestFocusInWindow();
                }
            });

            this.add(node[col][row]);
            col++;
            if (col == maxCol) {
                col = 0;
                row++;
            }

        }
        // SET START AND GOAL NODE
        setStartNode(0, 9);
        setGoalNode(14, 0);

        // SET SOLID NODE MANUALLY

        // Initialize algorithm structures
        distances = new HashMap<>();
        predecessors = new HashMap<>();
        openList = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // PRESS ENTER TO RUN
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent k) {
                if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enter pressed!");
                    resetAlgorithm();
                    try {
                        runDijkstra();
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        this.requestFocusInWindow();

    }

    private void setStartNode(int col, int row) {
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
    }

    private void setGoalNode(int col, int row) {
        node[col][row].setAsGoal();
        destinationNode = node[col][row];

    }

    private void setSolidNode(int col, int row) {
        node[col][row].setAsSolid();

    }

    private void resetAlgorithm() {
        // Reset distances, predecessors, and openList before running
        distances.clear();
        predecessors.clear();
        openList.clear();
        System.out.println("Resetting algorithm...");
    }

    public void runDijkstra() throws InterruptedException {
        System.out.println("Running Dijkstra...");
        for (int col = 0; col < maxCol; col++) {
            for (int row = 0; row < maxRow; row++) {
                distances.put(node[col][row], Integer.MAX_VALUE);
                predecessors.put(node[col][row], null);
                node[col][row].setBackground(Color.WHITE); // Reset color
            }
        }

        distances.put(startNode, 0);
        openList.add(startNode);
        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();// Dequeue

            if (currentNode == destinationNode) {
                reconstructPath();
                System.out.println("Destination reached!");
                return;
            }

            // UPDATE NEIGHBORS
            for (Node neighbor : getNeighbors(currentNode)) {
                if (!neighbor.solid && distances.get(currentNode) + neighbor.cost < distances.get(neighbor)) {
                    distances.put(neighbor, distances.get(currentNode) + neighbor.cost);
                    predecessors.put(neighbor, currentNode);
                    openList.add(neighbor);

                    neighbor.setBackground(Color.BLUE);
                    neighbor.setForeground(Color.orange);

                }
            }

        }
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        int[] dx = { -1, 1, 0, 0 }; // Left, Right, Up, Down
        int[] dy = { 0, 0, -1, 1 };

        for (int i = 0; i < 4; i++) {
            int newCol = node.col + dx[i];
            int newRow = node.row + dy[i];

            if (newCol >= 0 && newCol < maxCol && newRow >= 0 && newRow < maxRow) {
                neighbors.add(this.node[newCol][newRow]);
            }
        }

        return neighbors;
    }

    // Reconstruct the path from the start to the goal
    private void reconstructPath() {
        Node currentNode = destinationNode;
        while (currentNode != null) {
            currentNode.setBackground(new Color(19, 62, 135)); // Path nodes
            currentNode = predecessors.get(currentNode);
        }
    }
}
