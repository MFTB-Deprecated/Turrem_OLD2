package net.turrem.app.client.asset;

import net.turrem.app.FeatureName;
import net.turrem.app.mod.Mod;

public class Asset extends FeatureName
{
	public Asset(Mod mod, String identifier)
	{
		super(mod, identifier);
	}
	
	public GameAsset getAsset(String extension)
	{
		return new GameAsset(this.mod, this.identifier.replaceAll("\\.", "/") + "." + extension, GameAsset.EnumAssetLocation.ASSETS);
	}
}
