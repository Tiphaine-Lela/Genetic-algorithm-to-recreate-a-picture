package imageProcessing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.scene.paint.Color;

//represents a population of pictures

public class Tableaux {
	
	private ArrayList<Image> images;
	private int taille_pop;
	BufferedImage joconde;
	private double proba_mutation;
	private int seuil_fin;
	Color[][] tabColor;
	
	public Tableaux(int t_p, BufferedImage j, double prob_mut, int s_f, Color[][] tabC) {
		images = new ArrayList<Image>();
		taille_pop = t_p;
		joconde = j;
		proba_mutation = prob_mut;
		seuil_fin = s_f;
		tabColor = tabC;
	}
	
	//initialization and filling of Tableaux are two different steps
	//initializes the population of pictures
	public void init_population() {
		for (int i=0; i<taille_pop; i++) {
			Image current = new Image(6,tabColor);			
			images.add(current);
		}
	}
	
	//returns the best picture of the population
	public Image get_meilleure_im() {
		return images.get(0);
	}
	
	//sorts the pictures from the arraylist images by grades' increasing order
	public void tri_fusion(int deb, int fin) {
		//there are only two elements in the array
		if (fin-deb == 1) {
			if (images.get(deb).getNote() > images.get(fin).getNote()) {
				Image tmp = images.get(fin);
				images.set(fin, images.get(deb));
				images.set(deb, tmp);
			}
		}
		//there are more than two elements
		else if (fin - deb > 1){
			int milieu = deb +(fin-deb)/2;
			tri_fusion(deb, milieu);
			tri_fusion(milieu+1, fin);
			fusion(deb, fin);
		}
	}
	
	//sorts the list in parameters
	public static Image[] tri_fusion(Image[] t1, int deb, int fin) {
		//there are only two elements in the array
		Image[] im = new Image[5];
		if (fin-deb == 1) {
			Image[] deux = t1;
			if (deux[0].getNote() > deux[1].getNote()) {
				Image tmp = deux[1];
				deux[1] = deux[0];
				deux[0] = tmp;
			}
			return deux;
		}
		//there are more than two elements
		else if (fin - deb > 1){
			int milieu = deb +(fin-deb)/2;
			Image[] prem_part = tri_fusion(t1, deb, milieu);
			Image[] deux_part = tri_fusion(t1, milieu+1, fin);
			Image[] fus = fusion(prem_part, deux_part);
			return fus;
		}
		return im;
	}
	 
	//merge both parts of images 
	public void fusion(int deb, int fin) {
		int milieu = deb + (fin-deb)/2 + 1;
		int curs1 = deb;
		int curs2 = milieu;
		//the array to stock the sorted pictures
		Image[] trie = new Image[fin-deb+1];
		int curs_trie = 0;
		//sorts the pictures and stocks them in another array
		while ((curs1<milieu) && (curs2<=fin)) {
			if (images.get(curs1).getNote() < images.get(curs2).getNote()) {
				trie[curs_trie++] = images.get(curs1 ++);
			}
			else {
				trie[curs_trie++] = images.get(curs2 ++);
			}
		}
		if (curs1 == milieu) {
			for (int i=curs2; i<fin+1; i++)
				trie[curs_trie++] = images.get(i);
		}
		else {
			for (int i=curs1; i<milieu; i++)
				trie[curs_trie++] = images.get(i);
		}
		//recopies each element
		for (int i =deb; i<fin+1; i++) {
			images.set(i, trie[i-deb]);
		}
	}
	
	//merges two sorted arrays
	public static Image[] fusion(Image[] t1, Image[] t2) {
		Image[] trie = new Image[t1.length + t2.length];
		int curs1 = 0;
		int curs2 = 0;
		////the array to stock the sorted pictures
		int curs_trie = 0;
		//sorts the pictures and stocks them in another array
		while ((curs1<t1.length) && (curs2<t2.length)) {
			if (t1[curs1].getNote() < t2[curs2].getNote()) {
				trie[curs_trie++] = t1[curs1++];
			}
			else {
				trie[curs_trie++] = t2[curs2++];
			}
		}
		if (curs1 == t1.length) {
			for (int i=curs2; i<t2.length; i++)
				trie[curs_trie++] = t2[i];
		}
		else {
			for (int i=curs1; i<t1.length; i++)
				trie[curs_trie++] = t1[i];
		}
		return trie;
	}
	
	//inserts the elements of a sorted array into the array images
	public void insertion(Image[] tab_ins) {
		Image[] trie = new Image[images.size()+tab_ins.length];
		int curs_al = 0;
		int curs_t = 0;
		int curs_trie = 0;
		//insertion of the elements of both arrays
		while ((curs_al < images.size()) && (curs_t < tab_ins.length)) {
			if (images.get(curs_al).getNote() < tab_ins[curs_t].getNote()) {
				trie[curs_trie++] = images.get(curs_al++);
			}
			else {
				trie[curs_trie++] = tab_ins[curs_t++];
			}
		}
		if (curs_al == images.size()) {
			for (int i=curs_t; i<tab_ins.length; i++)
				trie[curs_trie++] = tab_ins[i];
		}
		else {
			for (int i=curs_al; i< images.size(); i++)
				trie[curs_trie++] = images.get(i);
		}
		//replaces the new sorted array in images
		for (int i=0; i<images.size(); i++)
			images.set(i, trie[i]);
		int size_im = images.size();
		for (int i=images.size(); i<size_im + tab_ins.length; i++)
			images.add(trie[i]);
	}
	
	//removes the worst elements in the array (there're at the end)
	public void suppression_mauvais() {
		int nb_el_suppr = images.size() - taille_pop;
		for (int i=0; i<nb_el_suppr; i++) {
			images.remove(images.size()-1);
		}
	}
	
	//returns a sample of the pictures in the array images
	public Image[] echantillon() {
		int taille_echant = images.size()/4;
		Image[] echantillon = new Image[taille_echant];
		int[] num_pris = new int[taille_echant];
		for (int i=0; i<taille_echant; i++) {
			//draw while checking the number hasn't been taken yet
			int ind_hasard =0;
			boolean non_pris = true;
			while (non_pris) {
				ind_hasard = (int) (Math.random() * images.size());
				for (int j=0; j<i; j++) {							
					if (ind_hasard == num_pris[j])
						non_pris = false;
				}
				non_pris = !non_pris;	
			}
			num_pris[i] = ind_hasard;
			//insertion of the drawn element in echantillon by increasing order
			int ind = 0;
			boolean ind_trouve = false;
			//finds the position of the drawn element
			for (int j=0; j<i; j++) {
				if (echantillon[j].getNote() > images.get(ind_hasard).getNote()) {
					ind = j;
					ind_trouve = true;
					break;
				}
			}
			//if the position hadn't been found, insert the element at the end 
			if (!ind_trouve) {
				ind = i;
			}
			//if the array is empty, inserts the element at the beginning
			if (i==0)
				echantillon[i] = images.get(ind_hasard);
			else {
				Image var_a_ins = images.get(ind_hasard);
				for (int j=ind; j<i; j++) {
					Image tmp = echantillon[j];
					echantillon[j] = var_a_ins;
					var_a_ins = tmp;
				}
				echantillon[i] = var_a_ins;
			}
		}
		return echantillon;
	}
	
	//returns the crossover's results
	public Image[] croisement_population() {
		Image[] echant = echantillon();
		//chooses the best parents and gets the children
		Image[] tmp = Image.Recombiner(echant[0], echant[1]);
		//sorts by increasing order
		Image[] enfants = Tableaux.tri_fusion(tmp, 0, tmp.length-1);
		return enfants;
	}	
	
	//returns a mutated picture
	public Image mutation_population() throws CloneNotSupportedException {
		Image[] echant = echantillon();
		Image parent_mutant = echant[echant.length -1];
		Image mutant;
		mutant = Image.Muter(parent_mutant);
		return mutant;
	}

	//perfoms the crossover, the mutation (if accepted), and the removal of the worst elements
	public void boucle() throws CloneNotSupportedException {
		//crossover
		Image[] crossover = croisement_population();
		double mut = Math.random();
		//mutation
		if (mut < proba_mutation) {
			Image mutant = mutation_population();
			//assembly of the children from the crossover and the mutation
			Image[] tab_inser = new Image[crossover.length + 1];
			int i=0;
			while ((i<crossover.length) && (crossover[i].getNote() < mutant.getNote())) {
				tab_inser[i] = crossover[i];
				i++;
			}
			tab_inser[i++] = mutant;
			for (;i<tab_inser.length; i++)
				tab_inser[i] = crossover[i-1];
			insertion(tab_inser);
		}
		else {
			insertion(crossover);
		}
		suppression_mauvais();
	}
	
	public String toString() {
		String re = "";
		for (int i=0; i<images.size(); i++) {
			re = re + images.get(i).getNote() + ";";
		}
		return re;
	}	
}
