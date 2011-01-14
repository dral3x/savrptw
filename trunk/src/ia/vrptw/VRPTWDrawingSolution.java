package ia.vrptw;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.*;

public class VRPTWDrawingSolution {

	static final int BORDER_LEFT = 10;
	static final int BORDER_TOP = 10;
	
	VRPTWSolution _solution;
	static Color standard_colors[] = new Color[50];
	static {
		Random r = new Random();
		for (int i = 0; i < 50; i++)
			standard_colors[i] = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
		}

	public static void main(String[] args) {

		VRPTWProblem problem = new VRPTWProblem("C101", 200);
		// problem.show();
		VRPTWSolver solver = new VRPTWSolver(); // processori
		// solver.activateDebugMode();
		System.out.println("* inizio ottimizzazione *");
		final VRPTWSolution solution = solver.generateFirstSolution(problem);
		System.out.println("* ottimizzazione terminata *");
		// solution.show();
		
		solver.printSolution(solution, solution, 0);
		
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

	static class DrawingArea extends JPanel {

		VRPTWSolution _solution, _initial_solution;

		public DrawingArea(VRPTWSolution solution, VRPTWSolution initial_solution) {
			setBackground(Color.WHITE);
			_solution = solution;
			_initial_solution = initial_solution;
		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			// Custom code to paint all the Rectangles from the List
			Graphics2D g2 = (Graphics2D) g;
			double zoom = 8;
			Color colors[] = standard_colors;

			// stampo customers e tragitti
			int c = 0;
			for (VRPTWRoute route : _solution.routes) {

				double pre_x = BORDER_LEFT + route._warehouse._position_x * zoom;
				double pre_y = BORDER_TOP + route._warehouse._position_y * zoom;

				double x = pre_x;
				double y = pre_y;
				
				for (VRPTWCustomer customer : route.customers) {
					x = BORDER_LEFT + customer._position_x * zoom;
					y = BORDER_TOP +  customer._position_y * zoom;

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
			
			// disegno le due barre
			Rectangle2D barra_km = new Rectangle2D.Double(900.0, 550-500*(_solution.totalTravelDistance()/_initial_solution.totalTravelDistance()), 25.0, 500*(_solution.totalTravelDistance()/_initial_solution.totalTravelDistance()));
			g2.setColor(Color.blue);
			g2.setPaint(Color.blue);
			g2.draw(barra_km);
			g2.fill(barra_km);
			g.drawString("km", 903, 570);
			
			double altezza_barra_mezzi = (double)_solution.routes.size()/(double)_initial_solution.routes.size();
			Rectangle2D barra_mezzi = new Rectangle2D.Double(950.0, 550-500*(altezza_barra_mezzi), 25.0, 500*(altezza_barra_mezzi));
			g2.setColor(Color.green);
			g2.setPaint(Color.green);
			g2.draw(barra_mezzi);
			g2.fill(barra_mezzi);
			g.drawString("mezzi", 944, 570);
			
			// riepilogo km e mezzi in basso a destra
			g2.setColor(Color.black);
			g2.setPaint(Color.black);
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			g.drawString("km: "+twoDForm.format(_solution.totalTravelDistance()), 903, 635);
			g.drawString("mezzi: "+_solution.routes.size(), 903, 650);
			
		}

	}

}
