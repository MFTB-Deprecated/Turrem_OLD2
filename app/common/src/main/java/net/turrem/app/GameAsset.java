package net.turrem.app;

import net.turrem.app.mod.Mod;

public class GameAsset
{
	public static enum EnumAssetLocation
	{
		MOD_JAR,
		MOD_ASSETS,
		MOD_DIR,
		GAME_DIR;
	}
	
	public final Mod mod;
	public final String file;
	public final EnumAssetLocation location;
	
	public GameAsset(Mod mod, String file)
	{
		this.mod = mod;
		this.file = file;
		this.location = EnumAssetLocation.MOD_ASSETS;
	}
	
	public GameAsset(Mod mod, String file, EnumAssetLocation location)
	{
		this.mod = mod;
		this.file = file;
		this.location = location;
	}
	
	public static GameAsset fromRaw(String raw)
	{
		int split = raw.indexOf(':');
		if (raw.startsWith("@"))
		{
			return new GameAsset(null, raw.substring(1), EnumAssetLocation.GAME_DIR);
		}
		Mod mod = Mod.getMod(raw.substring(0, split));
		String dat = raw.substring(split + 1);
		EnumAssetLocation loc = EnumAssetLocation.MOD_ASSETS;
		if (dat.startsWith("$"))
		{
			loc = EnumAssetLocation.MOD_JAR;
			dat = dat.substring(1);
		}
		else if (dat.startsWith("@"))
		{
			loc = EnumAssetLocation.MOD_DIR;
			dat = dat.substring(1);
		}
		return new GameAsset(mod, dat, loc);
	}
}
