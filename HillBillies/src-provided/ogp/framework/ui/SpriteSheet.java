package ogp.framework.ui;

import java.util.Map;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SpriteSheet {

	private final Image image;
	private final int spriteWidth, spriteHeight, hGap, vGap;
	private final int nbSpritesX;
	private final int nbSpritesY;
	private final int nbSprites;

	private final Rectangle2D[] viewports;

	private boolean suppressGapsAtBounds = true;

	public SpriteSheet(Image image, int spriteWidth, int spriteHeight, int hGap, int vGap) {
		this.image = image;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		this.hGap = hGap;
		this.vGap = vGap;
		double imgWidthAdj = image.getWidth();
		double imgHeightAdj = image.getHeight();
		if (suppressGapsAtBounds) {
			imgWidthAdj += 2 * hGap;
			imgHeightAdj += 2 * vGap;
		}
		this.nbSpritesX = (int) (imgWidthAdj / (spriteWidth + hGap));
		this.nbSpritesY = (int) (imgHeightAdj / (spriteHeight + vGap));
		this.nbSprites = nbSpritesX * nbSpritesY;
		this.viewports = new Rectangle2D[nbSprites];
		for (int x = 0; x < nbSpritesX; x++) {
			for (int y = 0; y < nbSpritesY; y++) {
				int i = getIndex(x, y);
				viewports[i] = new Rectangle2D(x * (spriteWidth + hGap), y * (spriteHeight + vGap), spriteWidth,
						spriteHeight);
			}
		}
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public int getHGap() {
		return hGap;
	}

	public int getVGap() {
		return vGap;
	}

	private int getIndex(int spriteX, int spriteY) {
		return spriteX + spriteY * nbSpritesX;
	}

	public Rectangle2D getViewport(int tileIndex) {
		return viewports[tileIndex];
	}

	public ImageView createImageView(int tileIndex) {
		ImageView result = new ImageView(image);
		result.setViewport(getViewport(tileIndex));
		return result;
	}

	public int getNbSprites() {
		return nbSpritesX * nbSpritesY;
	}

	@Override
	public String toString() {
		return "Spritesheet [nb tiles: " + nbSpritesX + "x" + nbSpritesY + " | tile size: " + spriteWidth + "x"
				+ spriteHeight + " | gaps H:" + hGap + " V:" + vGap + "]";
	}

	public Image getImage() {
		return image;
	}

	public SpriteSheet replaceColors(Map<Color, Color> colorMap) {
		PixelReader reader = image.getPixelReader();
		int w = (int)image.getWidth();
		int h = (int)image.getHeight();
		WritableImage replacedImage = new WritableImage(w, h);
		PixelWriter writer = replacedImage.getPixelWriter();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color orig = reader.getColor(x, y);
				Color newColor = colorMap.getOrDefault(orig, orig);
				writer.setColor(x, y, newColor);
			}
		}
		return new SpriteSheet(replacedImage, spriteWidth, spriteHeight, hGap, vGap);
	}
}
