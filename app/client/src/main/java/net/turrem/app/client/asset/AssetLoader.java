package net.turrem.app.client.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
