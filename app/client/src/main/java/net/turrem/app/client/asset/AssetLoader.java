package net.turrem.app.client.asset;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AssetLoader
{
	File bin;
	
	public AssetLoader(File theGameDir)
	{
		this.bin = theGameDir;
	}
	
	public File getFile(String name, String ext)
	{
		String mod = name.substring(0, name.indexOf(':'));
		String file = name.substring(name.indexOf(':') + 1);
		file = file.replaceAll("\\.", "/");
		String dir;
		if (mod.equals("app"))
		{
			dir = "client/resources/";
		}
		else
		{
			dir = "mods/" + mod + "/resources/";
		}
		dir += file;
		dir += "." + ext;
		return new File(this.bin, dir);
	}
	
	public BufferedImage loadTexture(String name) throws IOException
	{
		File file = this.getFile(name, "png");
		if (!file.exists())
		{
			return null;
		}
		return ImageIO.read(file);
	}
	
	public boolean doesTextureFileExist(String name)
	{
		return this.getFile(name, "png").exists();
	}
}
