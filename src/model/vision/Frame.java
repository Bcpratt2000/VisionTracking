package model.vision;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.awt.geom.*;

/**
 * A class that represents a simple picture. A simple picture may have an
 * associated file name and a title. A simple picture has pixels, width, and
 * height. A simple picture uses a BufferedImage to hold the pixels. You can
 * show a simple picture in a PictureFrame (a JFrame). You can also explore a
 * simple picture.
 * 
 * @author Barb Ericson ericson@cc.gatech.edu
 */
public class Frame extends Thread{

	/*
	 * buffered image to hold pixels for the simple picture
	 */
//	protected BufferedImage bufferedImage;

	protected Pixel[][] pixels;

	/////////////////////// Constructors /////////////////////////

	/**
	 * A Constructor that takes a file name and uses the file to create a picture
	 * 
	 * @param fileName
	 *            the file name to use in creating the picture
	 */
	public Frame(String fileName) {
		 long startTime = System.currentTimeMillis();

		load(fileName);
		 System.out.println("loaded File\n" + (System.currentTimeMillis()-startTime));

	}

	public Frame(Pixel[][] pixels) {
//		long startTime = System.currentTimeMillis();
		this.pixels = new Pixel[pixels.length][pixels[0].length];
		// deep copy, slow as hell. required as not to use a refrence to pixels
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				this.pixels[row][col] = new Pixel(pixels[row][col].getRGB());
			}
		}
		
//		makeBufferedImage();
		
//		System.out.println(startTime-System.currentTimeMillis());
		
	}

	////////////////////////// Methods //////////////////////////////////
	
	public void run(){
	}
	



	/**
	 * Method to get the width of the picture in pixels
	 * 
	 * @return the width of the picture in pixels
	 */
	public int getWidth() {
		return pixels[0].length;
	}

	/**
	 * Method to get the height of the picture in pixels
	 * 
	 * @return the height of the picture in pixels
	 */
	public int getHeight() {
		return pixels.length;
	}


	/**
	 * Method to return the pixel value as an int for the given x and y location
	 * 
	 * @param x
	 *            the x coordinate of the pixel
	 * @param y
	 *            the y coordinate of the pixel
	 * @return the pixel value as an integer (alpha, red, green, blue)
	 */
	public int getPixelRGB(int x, int y) {
		return pixels[y][x].getRGB();
	}

	/**
	 * Method to get a pixel object for the given x and y location
	 * 
	 * @param x
	 *            the x location of the pixel in the picture
	 * @param y
	 *            the y location of the pixel in the picture
	 * @return a Pixel object for this location
	 */
	public Pixel getPixel(int x, int y) {
		// create the pixel object for this picture and the given x and y location
		return pixels[y][x];
	}

	/**
	 * Method to get a two-dimensional array of Pixels for this simple picture
	 * 
	 * @return a two-dimensional array of Pixel objects in row-major order.
	 */
	public Pixel[][] getPixels2D() {
		return pixels;
	}

	/**
	 * Method to force the picture to repaint itself. This is very useful after you
	 * have changed the pixels in a picture and you want to see the change.
	 */
	/**
	 * Method to load the picture from the passed file name
	 * 
	 * @param fileName
	 *            the file name to use to load the picture from
	 * @throws IOException
	 *             if the picture isn't found
	 */

	/**
	 * Method to read the contents of the picture from a filename without throwing
	 * errors
	 * 
	 * @param fileName
	 *            the name of the file to write the picture to
	 * @return true if success else false
	 */
	public BufferedImage load(String fileName) {
		BufferedImage img;
		try {

			img = ImageIO.read(new File(fileName));

		} catch (IOException e) {
			e.printStackTrace();
			try {
				TimeUnit.MILLISECONDS.sleep(25);
			} catch (InterruptedException e1) {
			}
			img = load(fileName);
		}
		this.pixels = new Pixel[img.getHeight()][img.getWidth()];

		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				pixels[row][col] = new Pixel(img.getRGB(col, row));
			}
		}
		return img;
	}

	///////////////////// My Code///////////////////////////////
	public void swapColor(ProcessableColor color1, ProcessableColor color2) {
		int color1Val, color2Val;
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				color1Val = pixels[row][col].getColor(color1);
				color2Val = pixels[row][col].getColor(color2);

				pixels[row][col].setColor(color1, color1Val);
				pixels[row][col].setColor(color2, color2Val);
			}
		}
	}

	public int getAverage(ProcessableColor color) {
		int retBuffer = 0;
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				retBuffer += pixels[row][col].getColor(color);
			}
		}
		return retBuffer / (pixels.length * pixels[0].length);
	}

	public void colorIsolate(ProcessableColor color, double thresholdCoeff, double requiredIntensity) {
		thresholdCoeff *= 1.5;
		requiredIntensity *= .7;
		ProcessableColor color1, color2;
		int threshold = (int) (thresholdCoeff * getAverage(color));
		switch (color) {
		case RED:
			color1 = ProcessableColor.BLUE;
			color2 = ProcessableColor.GREEN;
			requiredIntensity *= .5;
			thresholdCoeff*=3;
			break;
		case BLUE:
			color1 = ProcessableColor.RED;
			color2 = ProcessableColor.GREEN;
			break;
		case GREEN:
			color1 = ProcessableColor.BLUE;
			color2 = ProcessableColor.RED;
			break;
		case CYAN:
			color1 = ProcessableColor.MAGENTA;
			color2 = ProcessableColor.YELLOW;
			requiredIntensity *= .8;
			thresholdCoeff *= 2;
			break;
		case MAGENTA:
			color1 = ProcessableColor.YELLOW;
			color2 = ProcessableColor.CYAN;
			requiredIntensity *= .8;
			thresholdCoeff *= 2;
			break;
		case YELLOW:
			color1 = ProcessableColor.CYAN;
			color2 = ProcessableColor.MAGENTA;
			requiredIntensity *= .8;
			thresholdCoeff *= 2;
			break;
		default:
			color1 = color;
			color2 = color;
		}

		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				if (pixels[row][col].getColor(color) > threshold
						&& pixels[row][col].getColor(color1) < pixels[row][col].getColor(color) * requiredIntensity
						&& pixels[row][col].getColor(color2) < pixels[row][col].getColor(color) * requiredIntensity) {
					pixels[row][col].setColor(color);
				} else {
					pixels[row][col].setColor(Color.BLACK);
				}
			}
		}
	}

	public void cutoffBottom(int numOfPixels) {
		for (int row = pixels.length - numOfPixels; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				pixels[row][col].setColor(Color.BLACK);
			}
		}

	}

	public int[] getCOM() {
		double colTotal = 0, rowTotal = 0, massTotal = 0;
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				massTotal += (double) pixels[row][col].getAverage();
				rowTotal += (double) pixels[row][col].getAverage() * row;
				colTotal += (double) pixels[row][col].getAverage() * col;
			}
		}
		try {
			colTotal /= massTotal;
			rowTotal /= massTotal;
			return new int[] { (int) colTotal, (int) rowTotal };
		} catch (Exception e) {
			return new int[] { 0, 0 };
		}
	}

	/*
	 * gets the number of pixels that are not black
	 */
	public int getArea() {
		int area = 0;
		for (int row = 0; row < pixels.length; row++) {
			for (int col = 0; col < pixels[0].length; col++) {
				if (pixels[row][col].getAverage() != 0) {
					area++;
				}
			}
		}
		return area;
	}

	public void drawBox(int x, int y, Color color, int radius) {
		try {
			for (int row = y - radius; row < y + radius; row++) {
				for (int col = x - radius; col < x + radius; col++) {
					pixels[row][col].setColor(color);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			drawBox(x, y, color, radius - 1);
		}
	}

	public void drawCOM(Color color, double sizeCoeff) {
		int[] com = getCOM();
		drawBox(com[0], com[1], color, (int) (sizeCoeff * Math.sqrt(getArea()) / 2));
	}

	public void drawBoundingFrame() {

	}

} // end of SimplePicture class