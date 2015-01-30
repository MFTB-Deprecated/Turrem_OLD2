package net.turrem.app.tile;

import net.turrem.app.FeatureName;
import net.turrem.app.mod.Mod;

public abstract class Tile extends FeatureName
{
	public Tile(String id, Mod mod)
	{
		super(mod, id);
	}
}
