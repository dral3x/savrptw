package ia.vrptw;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

public class VRPTWDrawingSolution {

	VRPTWSolution _solution;
	static Color standard_colors[] = new Color[50];
	static {
		Random r = new Random();
		for (int i = 0; i < 50; i++)
			standard_colors[i] = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		}

	public static void main(String[] args) {

		VRPTWProblem problem = new VRPTWProblem("C101", 50, 200);
		// problem.show();
		VRPTWSolver solver = new VRPTWSolver(1); // processori
		// solver.activateDebugMode();
		System.out.println("* inizio ottimizzazione *");
		final VRPTWSolution solution = solver.generateFirstSolution(problem);
		System.out.println("* ottimizzazione terminata *");
		// solution.show();
		
		solver.printSolution(solution, "prova.png");
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//
//				DrawingArea drawingArea = new DrawingArea(solution);
//
//				JFrame.setDefaultLookAndFeelDecorated(true);
//				JFrame frame = new JFrame("Solution");
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame.getContentPane().add(drawingArea);
//				frame.setSize(800, 600);
//				frame.setLocationRelativeTo(null);
//				frame.setVisible(true);
//
//				// salvo lo screenshot come png
////				BufferedImage awtImage = new BufferedImage(frame.getWidth(),
////						frame.getHeight(), BufferedImage.TYPE_INT_RGB);
////
////				Graphics g = awtImage.getGraphics();
////				frame.printAll(g);
////
////				File file = new File("prova.png");
////				try {
////					ImageIO.write(awtImage, "png", file);
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//
//			}
//		});
	}

	public VRPTWDrawingSolution(VRPTWSolution solution) {
		_solution = solution;
	}

	static class DrawingArea extends JPanel {

		VRPTWSolution _solution;

		public DrawingArea(VRPTWSolution solution) {
			setBackground(Color.WHITE);
			_solution = solution;
		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			// Custom code to paint all the Rectangles from the List
			Graphics2D g2 = (Graphics2D) g;
			double zoom = 8;
			Color colors[] = standard_colors;

			int c = 0;
			for (VRPTWRoute route : _solution.routes) {

				double pre_x = route._warehouse._position_x * zoom;
				double pre_y = route._warehouse._position_y * zoom;

				double x = pre_x;
				double y = pre_y;
				
				for (VRPTWCustomer customer : route.customers) {
					x = customer._position_x * zoom;
					y = customer._position_y * zoom;

					Ellipse2D circle = new Ellipse2D.Double();
					circle.setFrameFromCenter(x, y, x + 2, y + 2);
					g2.setColor(Color.black);
					g2.draw(circle);
					g2.setPaint(Color.black);
					g2.fill(circle);

					g2.setColor(colors[c]);
					g2.setPaint(colors[c]);
					Line2D lin = new Line2D.Double(pre_x, pre_y, x, y);
					// System.out.println(pre_x + "-" + pre_y + " " + x + "-" +
					// y);
					if (!customer.isWarehouse()) {
						g2.draw(lin);
					}
					pre_x = x;
					pre_y = y;
				}

				c++;

			}

		}

	}

}
