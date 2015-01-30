package net.turrem.app.client.asset;

import java.io.File;
import java.io.InputStream;

import net.turrem.app.mod.Mod;

public class GameAsset
{
	static AssetLoader loader;
	
	public static enum EnumAssetLocation
	{
		JAR,
		ASSETS,
		BIN;
	}
	
	private final boolean isModApp;
	public final Mod mod;
	public final String file;
	public final EnumAssetLocation location;
	
	public GameAsset(GameAsset parent, String file)
	{
		this(parent.mod, parent.file + file, parent.location);
	}
	
	public GameAsset(Mod mod, String file)
	{
		this(mod, file, EnumAssetLocation.ASSETS);
	}
	
	public GameAsset(Mod mod, String file, EnumAssetLocation location)
	{
		this.mod = mod == null ? Mod.APP : mod;
		this.isModApp = Mod.APP.equals(this.mod);
		this.file = file;
		this.location = location;
	}
	
	public boolean isAppAsset()
	{
		return this.isModApp;
	}
	
	public boolean isPacked()
	{
		return this.location == EnumAssetLocation.JAR;
	}
	
	public boolean isDirectory()
	{
		return this.file.endsWith("/");
	}
	
	public File getFile()
	{
		File dir = loader.gameDir;
		if (!this.isModApp)
		{
			dir = new File(dir, "mods/" + this.mod.identifier + "/");
		}
		if (this.location == EnumAssetLocation.JAR)
		{
			if (this.isModApp)
			{
				dir = new File(dir, "jars/");
			}
			dir = new File(dir, "client.jar");
		}
		else
		{
			if (this.location == EnumAssetLocation.ASSETS)
			{
				dir = new File(dir, "assets/");
			}
			dir = new File(dir, this.file);
		}
		return dir;
	}
	
	public InputStream getInput()
	{
		return loader.getInput(this);
	}
	
	public static GameAsset fromRaw(String raw)
	{
		int split = raw.indexOf(':');
		String id = raw;
		Mod mod = Mod.APP;
		if (split != -1)
		{
			id = raw.substring(split + 1);
			mod = new Mod(raw.substring(0, split));
		}
		EnumAssetLocation loc = EnumAssetLocation.ASSETS;
		if (id.startsWith("^"))
		{
			loc = EnumAssetLocation.BIN;
			id = id.substring(1);
		}
		else if (id.startsWith("&"))
		{
			loc = EnumAssetLocation.JAR;
			id = id.substring(1);
		}
		return new GameAsset(mod, id, loc);
	}
}
