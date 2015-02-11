package net.turrem.app.client.asset;

import java.io.File;
import java.io.InputStream;

import net.turrem.app.mod.Mod;

public class GameAsset
{
	static AssetLoader loader;
	
	public static enum EnumAssetLocation
	{
		/** {@code client.jar} file **/
		JAR,
		/** {@code assets} directory **/
		ASSETS,
		/** main directory **/
		BIN;
	}
	
	private final boolean isModApp;
	/**
	 * The mod that this asset belongs to. If this asset does not belong to a
	 * specific mod, then this is equal to {@link Mod#APP}.
	 */
	public final Mod mod;
	/**
	 * The file name and directory of this asset within {@link #location}.
	 */
	public final String file;
	/**
	 * The location of an asset within the game's file system.
	 */
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
		this.file = file.replaceAll("\\\\", "/");
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
	
	/**
	 * Gets the file or directory represented by this asset. Some assets may be
	 * stored in archives (such as JAR or ZIP files) in which case the archive
	 * will be returned instead.
	 * 
	 * @return A file that contains this asset.
	 */
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
	
	public GameAsset[] getAssets()
	{
		if (!this.isDirectory())
		{
			return null;
		}
		if (this.isPacked())
		{
			return null;
		}
		File[] files = this.getFile().listFiles();
		GameAsset[] assets = new GameAsset[files.length];
		for (int i = 0; i < files.length; i++)
		{
			String name = files[i].getName();
			if (files[i].isDirectory())
			{
				name += "/";
			}
			assets[i] = new GameAsset(this, name);
		}
		return assets;
	}
	
	public String pathFromGameDir()
	{
		String path = "/";
		if (!this.isModApp)
		{
			path += "mods/" + this.mod.identifier + "/";
		}
		if (this.location == EnumAssetLocation.JAR)
		{
			if (this.isModApp)
			{
				path += "jars/";
			}
			path += "client.jar/";
		}
		else
		{
			if (this.location == EnumAssetLocation.ASSETS)
			{
				path += "assets/";
			}
		}
		return path + this.file;
	}
	
	/**
	 * Uses the {@link AssetLoader} to create an {@link InputStream} for this
	 * asset.
	 * 
	 * @return An {@link InputStream} for this asset or null if this asset is a
	 *         directory or could not be found.
	 * @see AssetLoader#getInput(GameAsset)
	 */
	public InputStream getInput()
	{
		return loader.getInput(this);
	}
	
	@Override
	public String toString()
	{
		return GameAsset.toRaw(this);
	}
	
	@Override
	public int hashCode()
	{
		return this.pathFromGameDir().hashCode();
	}
	
	@Override
	public boolean equals(Object x)
	{
		if (x == this)
		{
			return true;
		}
		if (x instanceof GameAsset)
		{
			GameAsset asset = (GameAsset) x;
			if (asset.mod.equals(this.mod) && asset.file.equals(this.file) && asset.location == this.location)
			{
				return true;
			}
			return this.pathFromGameDir().equals(asset.pathFromGameDir());
		}
		return false;
	}
	
	/**
	 * Parses a textual representation / identifier of an asset. Every id has a
	 * component that is the name (directory and file name) of an asset within a
	 * location. While the basic syntax of a id is constant, different forms
	 * reference different locations:
	 * <dl>
	 * <dt><code>[mod]:[name]</code></dt>
	 * <dd>This specifies that the asset is within a mod's {@code assets}
	 * directory</dd>
	 * <dt><code>[mod]:^[name]</code></dt>
	 * <dd>This specifies that the asset is within a mod's main directory (
	 * {@code /mods/[mod]/})</dd>
	 * <dt><code>[mod]:&[name]</code></dt>
	 * <dd>This specifies that the asset is within a mod's {@code client.jar}.</dd>
	 * <dt><code>[name]</code></dt>
	 * <dd>This specifies that the asset is within the game's {@code assets}
	 * directory</dd>
	 * <dt><code>^[name]</code></dt>
	 * <dd>This specifies that the asset is within the game directory</dd>
	 * <dt><code>&[name]</code></dt>
	 * <dd>This specifies that the asset is within the game's {@code client.jar}
	 * .</dd>
	 * </dl>
	 * 
	 * @param raw The textual id of the asset.
	 * @return The asset represented by the raw id.
	 */
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
	
	/**
	 * @see #fromRaw(String)
	 * @param asset The asset
	 * @return A textual representation / identifier of the asset
	 */
	public static String toRaw(GameAsset asset)
	{
		String raw = asset.file;
		if (asset.location == EnumAssetLocation.BIN)
		{
			raw = "^" + raw;
		}
		else if (asset.location == EnumAssetLocation.JAR)
		{
			raw = "&" + raw;
		}
		if (!Mod.APP.equals(asset.mod))
		{
			raw = asset.mod.identifier + ":" + raw;
		}
		return raw;
	}
}
