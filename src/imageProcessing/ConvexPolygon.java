package imageProcessing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;



public class ConvexPolygon extends Polygon {
		
		static final int maxNumPoints=3;
		static Random gen = new Random();
		static int max_X,max_Y;
		NumberFormat nf = new DecimalFormat("##.00");
		
		
		// randomly generates a polygon
		public ConvexPolygon(int numPoints){
			super();
			genRandomConvexPolygone(numPoints);
			int r = gen.nextInt(256);
			int g = gen.nextInt(256);
			int b = gen.nextInt(256); 
			this.setFill(Color.rgb(r, g, b));
			this.setOpacity(gen.nextDouble());
		}
		
		public ConvexPolygon(double rr, double gg, double bb, ArrayList<Double> po){
			super();
			for (Double d : po)
				getPoints().add(d);
			double r = rr;
			double g = gg;
			double b = bb; 
			this.setFill(Color.color(r, g, b));
			this.setOpacity(gen.nextDouble());
		}
		
		//copies an element by allocating memory
		public ConvexPolygon Copier() {
			ConvexPolygon cp = new ConvexPolygon();
			for (Double p : this.getPoints()) {
				double d = p.doubleValue();
				cp.getPoints().add(new Double(d));
			}
			cp.setFill(this.getFill());
			cp.setOpacity(this.getOpacity());
			return cp;
		}
		
		//modifies a polygon's color 
		public void ChangerCouleur() {
			Color p = (Color) this.getFill();
			int n = gen.nextInt(3);
			double m = Math.random()*0.05;
			int op = gen.nextInt(2);
			double couleurModifiee=0;
			//modification of the red component
			if (n==0) 		
				couleurModifiee = p.getRed();
			//modification of the green component
			else if (n==1) 
				couleurModifiee = p.getGreen();
			//modification of the blue component
			else if (n==2) 
				couleurModifiee = p.getBlue();
			//changes the color
			if (couleurModifiee + m > 1)
				couleurModifiee -= m;
			else if (couleurModifiee - m < 0) 
				couleurModifiee += m;
			else {
				if (op == 0)
					couleurModifiee += m;
				else 
					couleurModifiee -= m;
			}
			//sets the new color
			if (n==0)					this.setFill(Color.color(couleurModifiee, p.getGreen(), p.getBlue()));
			else if (n==1)				this.setFill(Color.color(p.getRed(), couleurModifiee, p.getBlue()));
			else						this.setFill(Color.color(p.getRed(), p.getGreen(), couleurModifiee));
		}
		
		//removes a vertex from the polygon
		public void SupprimerPoint() {
			if (this.getPoints().size()>6) {
				int n = gen.nextInt(this.getPoints().size()-1);
				if (n%2==1)
					n--;
				this.getPoints().remove(n);
				this.getPoints().remove(n);
			}
		}
		
		//translate a polygon's vertex 
		public void Translater_Point() {
			//chooses randomly a vertex
			int n = gen.nextInt(this.getPoints().size()-1);
			if (n%2==1)
				n--;
			//changes the x coordinate
			int d_x = gen.nextInt(10);
			int op = gen.nextInt(2);
			if (op == 0) d_x = (-1)*d_x;
			if (this.getPoints().get(n) + d_x <0)
				this.getPoints().set(n, 0.0);
			if (this.getPoints().get(n) + d_x > max_X)
				this.getPoints().set(n, (double)max_X);
			//changes the y coordinate
			int d_y = gen.nextInt(10);
			op = gen.nextInt(2);
			if (op == 0) d_y = (-1)*d_y;
			if (this.getPoints().get(n+1) + d_y <0)
				this.getPoints().set(n+1, 0.0);
			if (this.getPoints().get(n+1) + d_y > max_Y)
				this.getPoints().set(n+1, (double)max_Y);
		}
		
		
		public ConvexPolygon(){
			super();
		}
		
		public String toString(){
			String res = super.toString();
			res += " " + this.getFill() + " opacity " + this.getOpacity();
			return res;
		}
			
		public void addPoint(double x, double y){
			getPoints().add(x);
			getPoints().add(y);
		}
		
		// http://cglab.ca/~sander/misc/ConvexGeneration/convex.html
		public void genRandomConvexPolygone(int n){
			List<Point> points = new LinkedList<Point>();
			List<Integer> abs = new ArrayList<>();
			List<Integer> ord = new ArrayList<>();
			
			for (int i=0;i<n;i++){
				abs.add(gen.nextInt(max_X));
				ord.add(gen.nextInt(max_Y));
			}
			Collections.sort(abs);
			Collections.sort(ord);
			//System.out.println(abs + "\n" + ord);
			int minX = abs.get(0);
			int maxX = abs.get(n-1);
			int minY = ord.get(0);
			int maxY = ord.get(n-1);
			
			List<Integer> xVec = new ArrayList<>();
			List<Integer> yVec = new ArrayList<>();
			
			int top= minX, bot = minX;
			for (int i=1;i<n-1;i++){
				int x = abs.get(i);
				
				if (gen.nextBoolean()){
					xVec.add(x-top);
					top = x;
				} else{
					xVec.add(bot-x);
					bot = x;
				}
			}
			xVec.add(maxX-top);
			xVec.add(bot-maxX);
			
			int left= minY, right = minY;
			for (int i=1;i<n-1;i++){
				int y = ord.get(i);
				
				if (gen.nextBoolean()){
					yVec.add(y-left);
					left = y;
				} else{
					yVec.add(right-y);
					right = y;
				}
			}
			yVec.add(maxY-left);
			yVec.add(right-maxY);
			
			Collections.shuffle(yVec);
			
			List<Point> lpAux = new ArrayList<>();
			for (int i=0;i<n;i++)
				lpAux.add(new Point(xVec.get(i), yVec.get(i)));
		
			
			// sort in order by angle
			Collections.sort(lpAux, (x,y) ->  Math.atan2(x.getY(), x.getX())  < Math.atan2(y.getY(), y.getX()) ? -1 :
				Math.atan2(x.getY(), x.getX())  == Math.atan2(y.getY(), y.getX()) ? 0 : 1);
				
			int x=0,y=0;
			int minPolX=0, minPolY=0;
			
			for (int i=0;i<n;i++){
				points.add(new Point(x,y));
				x += lpAux.get(i).getX();
				y += lpAux.get(i).getY(); 
				
				if (x < minPolX)
					minPolX=x;
				if (y<minPolY)
					minPolY=y;
			}
				
			int xshift = gen.nextInt(max_X - (maxX-minX)) ;
			int yshift = gen.nextInt(max_Y - (maxY-minY)) ;
			xshift -= minPolX;
			yshift -= minPolY;
			for (int i=0;i<n;i++){
				Point p = points.get(i);
				p.translate(xshift,yshift);
			}
			for (Point p : points)
				addPoint(p.getX(), p.getY());			
		}
		
		public void genRandomConvexPolygoneBIS(int n, int MaxX, int MinX, int MaxY, int MinY){
			List<Point> points = new LinkedList<Point>();
			List<Integer> abs = new ArrayList<>();
			List<Integer> ord = new ArrayList<>();
			
			for (int i=0;i<n;i++){
				abs.add(gen.nextInt(MaxX-MinX)+MinX);
				ord.add(gen.nextInt(MaxY-MinY)+MinY);
			}
			Collections.sort(abs);
			Collections.sort(ord);
			//System.out.println(abs + "\n" + ord);
			int minX = abs.get(0);
			int maxX = abs.get(n-1);
			int minY = ord.get(0);
			int maxY = ord.get(n-1);
			
			List<Integer> xVec = new ArrayList<>();
			List<Integer> yVec = new ArrayList<>();
			
			int top= minX, bot = minX;
			for (int i=1;i<n-1;i++){
				int x = abs.get(i);
				
				if (gen.nextBoolean()){
					xVec.add(x-top);
					top = x;
				} else{
					xVec.add(bot-x);
					bot = x;
				}
			}
			xVec.add(maxX-top);
			xVec.add(bot-maxX);
			
			int left= minY, right = minY;
			for (int i=1;i<n-1;i++){
				int y = ord.get(i);
				
				if (gen.nextBoolean()){
					yVec.add(y-left);
					left = y;
				} else{
					yVec.add(right-y);
					right = y;
				}
			}
			yVec.add(maxY-left);
			yVec.add(right-maxY);
			
			Collections.shuffle(yVec);
			
			List<Point> lpAux = new ArrayList<>();
			for (int i=0;i<n;i++)
				lpAux.add(new Point(xVec.get(i), yVec.get(i)));
		
			
			// sort in order by angle
			Collections.sort(lpAux, (x,y) ->  Math.atan2(x.getY(), x.getX())  < Math.atan2(y.getY(), y.getX()) ? -1 :
				Math.atan2(x.getY(), x.getX())  == Math.atan2(y.getY(), y.getX()) ? 0 : 1);
				
			int x=0,y=0;
			int minPolX=0, minPolY=0;
			
			for (int i=0;i<n;i++){
				points.add(new Point(x,y));
				x += lpAux.get(i).getX();
				y += lpAux.get(i).getY(); 
				
				if (x < minPolX)
					minPolX=x;
				if (y<minPolY)
					minPolY=y;
			}
				
			int xshift = gen.nextInt(MaxX - (maxX-minX)) ;
			int yshift = gen.nextInt(MaxY - (maxY-minY)) ;
			xshift -= minPolX;
			yshift -= minPolY;
			for (int i=0;i<n;i++){
				Point p = points.get(i);
				p.translate(xshift,yshift);
			}
			for (Point p : points)
				addPoint(p.getX(), p.getY());
			
		}
		
		public static ConvexPolygon CrossRandom(ConvexPolygon p1, ConvexPolygon p2) {
			Color c1 = (Color) p1.getFill();
			Color c2 = (Color) p2.getFill();
			int numP1 = p1.getPoints().size();
			System.out.println(numP1);
			int numP2 = p2.getPoints().size();
			System.out.println(numP2);
			List<Double> lp1 = p1.getPoints();
			List<Double> lp2 = p2.getPoints();
			for (Double d : lp2) {
				if (!lp1.contains((Double) d))
					lp1.add(d);
			}
			double minR = Math.abs(c1.getRed()-c2.getRed())/2;
			double maxR = (c1.getRed()+c2.getRed())/2;
			double r = gen.nextDouble()*(maxR-minR)+minR;
			double minG = Math.abs(c1.getGreen()-c2.getGreen())/2;
			double maxG = (c1.getGreen()+c2.getGreen())/2;
			double g = gen.nextDouble()*(maxG-minG)+minG;
			double minB = Math.abs(c1.getBlue()-c2.getBlue())/2;
			double maxB = (c1.getBlue()+c2.getBlue())/2;
			double b = gen.nextDouble()*(maxB-minB)+minB;
			int n = gen.nextInt(Math.abs(numP2-numP1)+1)+Math.min(numP2, numP1);
			ArrayList<Double> listePoints = new ArrayList<>();
			for (int x=0; x<n; x++) {
				int i = gen.nextInt(lp1.size());
				double d = lp1.get(i).doubleValue();
				listePoints.add(new Double(d));
			}
			ConvexPolygon c = new ConvexPolygon(r,g,b,listePoints);
			return c;
		}
	
		
		
		public class Point {

			int x,y;

			// generate a random point
			public Point(){
				x= gen.nextInt(max_X);
				y= gen.nextInt(max_Y);
			}
			
			public Point(int x, int y){
				this.x=x;
				this.y=y;
			}
			
			public int getX(){return x;}
			public int getY(){return y;}
			public void translate(int vx,int vy){
				x += vx;
				y += vy;
			}
			
			public boolean equals(Object o){
				if (o==null)
					return false;
				else if (o == this)
					return true;
				else if (o instanceof Point)
					return ((Point) o).x== this.x && ((Point) o).y== this.y;
				else
					return false;
			}
			
			public String toString(){
				return "(" + x + "," + y+")"; 
			}
			
		}
		
		
	
}