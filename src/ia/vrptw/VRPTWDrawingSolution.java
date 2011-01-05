package ia.vrptw;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Random;

import javax.swing.*;

public class VRPTWDrawingSolution {

	VRPTWSolution _solution;
	
	public static void main(String[] args) {
		
		VRPTWProblem problem = new VRPTWProblem("C101", 50, 200);
		//problem.show();
		VRPTWSolver solver = new VRPTWSolver(1); // processori
		//solver.activateDebugMode();
		System.out.println("* inizio ottimizzazione *");
		final VRPTWSolution solution = solver.generateFirstSolution(problem);
		System.out.println("* ottimizzazione terminata *");
		//solution.show();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				DrawingArea drawingArea = new DrawingArea(solution);
				
				JFrame.setDefaultLookAndFeelDecorated(true);
				JFrame frame = new JFrame("Solution");
				frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				frame.getContentPane().add(drawingArea);
				frame.setSize(800, 600);
				frame.setLocationRelativeTo( null );
				frame.setVisible(true);
			}
		});
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

			//  Custom code to paint all the Rectangles from the List
			Graphics2D g2 = (Graphics2D) g;
			double zoom = 10;
			
			for (VRPTWRoute route : _solution.routes) {
			
				Random r = new Random();
				Color  c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
				g2.setColor(c);
				
				double pre_x = route._warehouse._position_x*zoom;
				double pre_y = route._warehouse._position_y*zoom;
				
				double x = pre_x;
				double y = pre_y;
				
				for (VRPTWCustomer customer : route.customers) {
					
						x = customer._position_x*zoom;
						y = customer._position_y*zoom;
						Line2D lin = new Line2D.Double(pre_x, pre_y, x, y);
						//System.out.println(pre_x + "-" + pre_y + " " + x + "-" + y);
						if (!customer.isWarehouse()) {
							g2.draw(lin);
						}
						pre_x = x;
						pre_y = y;
					}
				}
	        
			}
	        
		}

	}
