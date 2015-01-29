package net.turrem.app.mod;

import net.turrem.app.EnumSide;

public class ModInstance
{
	Mod mod;
	String name;
	boolean hasInfo = false;
	boolean hasAssets = false;
	boolean hasJar[] = { false, false };
	
	public Mod mod()
	{
		return this.mod;
	}
	
	public String name()
	{
		return this.name;
	}
	
	public boolean hasInfo()
	{
		return this.hasInfo;
	}
	
	public boolean hasAssets()
	{
		return this.hasAssets;
	}
	
	public boolean hasJar(EnumSide side)
	{
		if (side == EnumSide.CLIENT)
		{
			return this.hasJar[0];
		}
		else if (side == EnumSide.SERVER)
		{
			return this.hasJar[1];
		}
		return false;
	}
}
