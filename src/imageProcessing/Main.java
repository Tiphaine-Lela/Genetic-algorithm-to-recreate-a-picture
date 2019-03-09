package imageProcessing;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage myStage) throws CloneNotSupportedException{
		String targetImage = "monaLisa-100.jpg";
		Color[][] target=null;
		int maxX=0;
    	int maxY=0;
    	int t = 0;
    	Group tmp = new Group();
		try {
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
        	ConvexPolygon.max_X= maxX;
        	ConvexPolygon.max_Y= maxY;
        	target = new Color[maxX][maxY];
        	for (int i=0;i<maxX;i++){
        		for (int j=0;j<maxY;j++){
        			int argb = bi.getRGB(i, j);
        			int b = (argb)&0xFF;
        			int g = (argb>>8)&0xFF;
        			int r = (argb>>16)&0xFF;
        			int a = (argb>>24)&0xFF;
        			target[i][j] = Color.rgb(r,g,b);
        		}
        	}
        	//initialization of an output file
        	FileOutputStream fos = null;
        	try {
    			fos = new FileOutputStream(new File("images/trace.txt"));
        	}
        	catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
        	Tableaux tab = new Tableaux (600, bi, 0.15, 10,target);
        	tab.init_population();
        	//merge sort
        	tab.tri_fusion(0, 599);
        	do {
	        	tab.boucle();
	        	WritableImage wimg = new WritableImage(bi.getWidth(),bi.getHeight());
	    		tab.get_meilleure_im().img.snapshot(null,wimg);
	    		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null); 
	    		try {
	    			if (t%1000==0) {
	    				//backup of the picture and writing in the output file
		    			ImageIO.write(renderedImage, "png", new File("images/test"+t+".png"));
		    			String s = "";
		    			s = s + t + " : " + tab.get_meilleure_im().getNote() + "\n";
		    			byte[] buf =s.getBytes();
		    			fos.write(buf);
	    			}
	    		} 
	    		catch (IOException e1) {
	    			e1.printStackTrace();
	    		}
	    		String note = String.format("%.4f",tab.get_meilleure_im().getNote());
	    		if (t%100==0)
	    		System.out.println(note);
	    		t++;
	    		tmp = tab.get_meilleure_im().img;
        	}
        	//final test
        	while (tab.get_meilleure_im().getNote()>=10);
        	try {
    			if (fos!= null)
    				fos.close();
    		}
    		catch(IOException e) {
    			e.printStackTrace();
    		}
        	
		}
		catch(IOException e){
	        System.err.println(e);
	        System.exit(9);
	    }
		
	}

}
