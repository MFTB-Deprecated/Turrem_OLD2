package net.turrem.app.client.asset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.turrem.app.client.Turrem;

public class AssetLoader
{
	private static AssetLoader instance;
	
	private Turrem theTurrem;
	
	public final File gameDir;
	
	public AssetLoader(File gameDir, Turrem turrem)
	{
		if (instance != null)
		{
			throw new IllegalStateException("An AssetLoader has already been created for this context!");
		}
		this.theTurrem = turrem;
		this.gameDir = gameDir;
		GameAsset.loader = this;
		instance = this;
	}
	
	public static AssetLoader instance()
	{
		return instance;
	}
	
	public BufferedImage readImage(GameAsset asset) throws IOException
	{
		return ImageIO.read(this.getInput(asset));
	}
	
	public BufferedImage readImage(Asset asset) throws IOException
	{
		return ImageIO.read(this.getInput(asset.getAsset("png")));
	}
	
	public OutputStream getOutput(GameAsset asset)
	{
		return this.getOutput(asset, true);
	}
	
	public OutputStream getOutput(GameAsset asset, boolean createIfMissing)
	{
		return this.getOutput(asset, createIfMissing, false);
	}
	
	public OutputStream getOutput(GameAsset asset, boolean createIfMissing, boolean append)
	{
		if (asset.isDirectory() || asset.isPacked())
		{
			return null;
		}
		else
		{
			try
			{
				File f = asset.getFile();
				if (!f.exists() && createIfMissing)
				{
					f.createNewFile();
				}
				return new FileOutputStream(f, append);
			}
			catch (IOException e)
			{
				return null;
			}
		}
	}
	
	public InputStream getInput(GameAsset asset)
	{
		if (asset.isDirectory())
		{
			return null;
		}
		if (asset.isPacked())
		{
			ClassLoader loader = this.theTurrem.modLoader.getClassLoader(asset.mod);
			if (loader == null)
			{
				return null;
			}
			return loader.getResourceAsStream(asset.file);
		}
		else
		{
			try
			{
				return new FileInputStream(asset.getFile());
			}
			catch (FileNotFoundException e)
			{
				return null;
			}
		}
	}
}
