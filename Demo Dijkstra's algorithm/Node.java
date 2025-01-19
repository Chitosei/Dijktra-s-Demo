import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JButton;

public class Node extends JButton implements MouseListener {

    Node parent;
    int col;
    int row;
    int cost;
    int previousCost = 0;
    boolean start, end, solid, open, explored;
    private Color previousColor;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
        this.cost = generateRandomCost();
        setText(String.valueOf(cost));
        int intensity = 205 - (cost * 40);
        this.setBackground(new Color(intensity, 200, intensity));
        this.setForeground(Color.BLACK);
        this.setFont(new Font("Arial", Font.BOLD, 18));
        addMouseListener(this);
    }

    public void setAsStart() {
        setBackground(Color.BLUE);
        setForeground(Color.WHITE);
        setText("S");
        start = true;
    }

    public void setAsGoal() {
        setBackground(Color.RED);
        setForeground(Color.WHITE);
        setText("G");
        end = true;
    }

    public void setAsSolid() {
        if (!start && !end) {
            previousColor = getBackground();
            setBackground(Color.BLACK);
            setForeground(Color.BLACK);
            solid = true;
            setText("");
        }
    }

    private int generateRandomCost() {
        Random rand = new Random();
        return rand.nextInt(5) + 1; // Random cost from 1 to 5
    }

    public void resetToPreviousState() {
        if (previousColor != null) {
            setBackground(previousColor); // Revert to the previous color
            setForeground(Color.BLACK);
            setText(String.valueOf(cost));
            solid = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
            if (solid) {
                resetToPreviousState(); // Reset the node to its previous state
            }
        } else if (e.getButton() == MouseEvent.BUTTON1) {
            if (!solid) {
                setAsSolid();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
