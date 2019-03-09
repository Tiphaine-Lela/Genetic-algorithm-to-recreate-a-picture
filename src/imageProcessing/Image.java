package imageProcessing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

//represents a collection of polygons

public class Image implements Serializable {
	public ArrayList<ConvexPolygon> listePolygones = new ArrayList<>();
	public Group img;
	private double note;
	private static Color[][] tabOrigine;
	private int MaxX, MaxY;
	
	public Image(int SommetMax, Color[][] tab) {
		tabOrigine = tab;
		MaxX = tab.length;
		MaxY = tab[0].length;
		img = new Group();
		int n = RandInclu(50,2);		//the random number of generated polygons, with between 3 and SommetMax vertices
		for (int i=0; i<n; i++) {
			int nsommets = RandInclu(SommetMax,3);		
			ConvexPolygon p = new ConvexPolygon(nsommets);
			listePolygones.add(p);
		}
		for (ConvexPolygon p : listePolygones)
			img.getChildren().add(p);
		this.Evaluation();
	}
	
	public Image(List<ConvexPolygon> l, Color[][] tab) {
		tabOrigine = tab;
		MaxX = tab.length;
		MaxY = tab[0].length;
		img = new Group();
		for (ConvexPolygon p : l) {
			listePolygones.add(p);
			img.getChildren().add(p);
		}
		this.Evaluation();
	}
	
	//add a polygon to the polygon's list if it's not already full
	public void Ajouter(ConvexPolygon pol) {
		if (listePolygones.size()<50) {
			listePolygones.add(pol);
			img.getChildren().add(pol);
		}
		this.Evaluation();
	}
	
	//removes a polygon from the list
	public void Supprimer(ConvexPolygon pol) {
		listePolygones.remove(pol);				
		img.getChildren().remove((ConvexPolygon) pol);
		this.Evaluation();
	}
	
	//returns true if there is no polygon
	public boolean ImageVide() {
		if (listePolygones.size()==0 && img.getChildren().size()==0)
			return true;
		return false;
	}
	
	//returns the grade of the picture
	public double getNote() {
		return note;
	}
	
	//returns the Euclidian distance between RGB vector of two pixels
	private double Distance2Vects(Color c1, Color c2) {
		double res = 0;
		res = Math.pow(c1.getBlue()-c2.getBlue(),2)
				+Math.pow(c1.getRed()-c2.getRed(),2)
				+Math.pow(c1.getGreen()-c2.getGreen(),2);
		return res;
	}
	
	//computes the fitness function (picture's grade)
	public void Evaluation() {			
		double no = 0;
		WritableImage wimg = new WritableImage(MaxX,MaxY);
		img.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		for (int i=0; i<MaxX; i++) {
			for (int j=0; j<MaxY; j++) {
				Color c = pr.getColor(i, j);
				no += Distance2Vects(c,tabOrigine[i][j]);
			}
		}
		this.note = Math.sqrt(no);
	}
	
	//returns a sub-list of polygons' list (deep copy)
	private ArrayList<ConvexPolygon> CopSubListe(int from, int to) {
		List<ConvexPolygon> tmp = listePolygones.subList(from, to);
		ArrayList<ConvexPolygon> lp = new ArrayList<>();
		for (ConvexPolygon cp : tmp) {
			lp.add(cp.Copier());
		}
		return lp;
	}
	
	//crossover function
	public static Image[] Recombiner(Image i1, Image i2) {
		ArrayList<Image> listeImages = new ArrayList<>();
		//chooses two crossover points
		int r1 = RandExclu(Math.min(i1.listePolygones.size(), i2.listePolygones.size()),0);
		int r2;
		do {
			r2 = RandExclu(Math.min(i1.listePolygones.size(), i2.listePolygones.size()),0);
		}
		while (r2==r1);
		
		//cut the pictures according to the first crossover point and recombine them
		ArrayList<ConvexPolygon> l1 = i1.CopSubListe(0, r1);
		ArrayList<ConvexPolygon> l3 = i1.CopSubListe(r1, i1.listePolygones.size());
		
		ArrayList<ConvexPolygon> l2 = i2.CopSubListe(0, r1);
		ArrayList<ConvexPolygon> l4 = i2.CopSubListe(r1, i2.listePolygones.size());
		
		l1.addAll(l4);
		l2.addAll(l3);
		
		listeImages.add(new Image(l1,tabOrigine));
		listeImages.add(new Image(l2,tabOrigine));
		
		//cut the pictures according to the second crossover point and recombine them
		ArrayList<ConvexPolygon> l5 = i1.CopSubListe(0, r2);
		ArrayList<ConvexPolygon> l7 = i1.CopSubListe(r2, i1.listePolygones.size());
		
		ArrayList<ConvexPolygon> l6 = i2.CopSubListe(0, r2);
		ArrayList<ConvexPolygon> l8 = i2.CopSubListe(r2, i2.listePolygones.size());
		
		l5.addAll(l8);
		l6.addAll(l7);
		
		listeImages.add(new Image(l5,tabOrigine));
		listeImages.add(new Image(l6,tabOrigine));
		
		//removes the pictures which haven't polygons
		for (Image i : listeImages) {
			if (i.ImageVide())
				listeImages.remove((Image) i);
		}
		
		//converts the ArrayList in an array
		Image[] tab = new Image[listeImages.size()];
		for (int i=0; i<listeImages.size(); i++) 
			tab[i] = listeImages.get(i);
		
		return tab;
	}
	
	//mutation function
	public static Image Muter (Image i) {
		//chooses the action to do
		double pro = RandFloat();
		//chooses the picture to modify
		int n = RandExclu(i.listePolygones.size(),0);
		ArrayList<ConvexPolygon> l = i.CopSubListe(0, i.listePolygones.size());
		Image mutant = new Image(l,tabOrigine);
		ConvexPolygon cp = mutant.listePolygones.get(n);
		
		//changes the polygon's color
		if (0<pro && pro<=0.35) {
			cp.ChangerCouleur();
		}
		//removes one point from the polygon
		else if (0.35<pro && pro<=0.5) {
			cp.SupprimerPoint();
		}
		//translate a polygon's vertex
		else if (0.5<pro && pro <= 0.7) {
			cp.Translater_Point();
		}
		//exchange the position of two polygons in the picture
		else if (0.70<pro && pro<=0.85) {
			int x1 = RandExclu(mutant.listePolygones.size(),0);
			int x2;
			do {
				x2 = RandExclu(mutant.listePolygones.size(),0);
			}
			while (x2==x1);
			ConvexPolygon c1 = mutant.listePolygones.get(x1);
			ConvexPolygon c2 = mutant.listePolygones.get(x2);
			mutant.listePolygones.set(x1, c2);
			mutant.listePolygones.set(x2, c1);
			mutant.img = new Group();
			for (ConvexPolygon p : mutant.listePolygones) 
				mutant.img.getChildren().add(p);
		}
		//add or remplace a polygon
		else {	
			int ri = RandInclu(6,3);
			ConvexPolygon c = new ConvexPolygon(ri);
			if (mutant.listePolygones.size()<50) {
				mutant.listePolygones.add(c);
				mutant.img.getChildren().add(c);
			}
			else {
				int m = RandExclu(mutant.listePolygones.size(),0);
				mutant.listePolygones.set(m, c);
				mutant.img.getChildren().set(m, c);
			}
		}
		mutant.Evaluation();
		return mutant;
	}
	
	//returns an integer between min and max (included)
	private static int RandInclu(int max, int min) {
		Random r = new Random();
		int n = r.nextInt(max - min +1) + min;
		return n;
	}
	
	//returns an integer between min and max (excluded)
	private static int RandExclu(int max, int min) {
		Random r = new Random();
		int n = r.nextInt(max - min) + min;
		return n;
	}
	
	//returns a random float
	private static float RandFloat() {
		Random r = new Random();
		float n = r.nextFloat();
		return n;
	}
}
