package net.turrem.utils.img;

import java.io.DataInputStream;
import java.io.IOException;

import java.awt.image.BufferedImage;

public class ByteImage extends ByteRaster
{
	public ByteImage(int width, int height)
	{
		super(4, width, height);
	}
	
	public ByteImage(int width, int height, DataInputStream in) throws IOException
	{
		super(4, width, height, in);
	}
	
	public ByteImage(BufferedImage image)
	{
		super(4, image.getWidth(), image.getHeight());
		int[] pixels = new int[this.width * this.height];
		image.getRGB(0, 0, this.width, this.height, pixels, 0, this.width);
		for (int y = 0; y < this.height; y++)
		{
			for (int x = 0; x < this.width; x++)
			{
				int pixel = pixels[y * this.width + x];
				this.buffer.put((byte) ((pixel >> 16) & 0xFF));
				this.buffer.put((byte) ((pixel >> 8) & 0xFF));
				this.buffer.put((byte) (pixel & 0xFF));
				this.buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		this.buffer.flip();
	}
	
	public int getRGBA(int x, int y)
	{
		return this.buffer.getInt((y * this.width + x) * 4);
	}
	
	public int getRGB(int x, int y)
	{
		return this.getRGBA(x, y) >>> 8;
	}
	
	public int getARGB(int x, int y)
	{
		int i = (y * this.width + x) * 4;
		int color = 0;
		color |= this.buffer.get(i + 3);
		color <<= 8;
		color |= this.buffer.get(i + 0);
		color <<= 8;
		color |= this.buffer.get(i + 1);
		color <<= 8;
		color |= this.buffer.get(i + 2);
		return color;
	}
}
