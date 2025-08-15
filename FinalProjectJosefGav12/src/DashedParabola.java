import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/*
 * Class I made to play around with dashed parabolas. 
 * I will potentially use this to display a dashed porabola projectile path
 * Josef Gavronskiy FINAL PROJECT
 * ICS4U 2024/2025
 * 
 * UPDATE ***
 * I DIDNT USE IN MY PROJECT DUE TO TIME
 * */

public class DashedParabola extends JPanel {

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
      
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10));
        
        g2d.setColor(new Color(0,0,0,100));
        
        // Draw the parabola as a series of small line segments
        for (int x = -200; x < 200; x++) {
        	if (x%3 == 0) {
        		int y = (int) (-0.01 * x * x);  // Parabola equation: y = -0.01 * x^2
		        int nextX = x + 1;
		        int nextY = (int) (-0.01 * nextX * nextX);
		        g2d.draw(new Line2D.Float(x + 250, -y + 250, nextX + 250, -nextY + 250)); // Translate to fit the screen
        	}
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dashed Parabola");
        DashedParabola panel = new DashedParabola();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(panel);
        frame.setVisible(true);
    }
}