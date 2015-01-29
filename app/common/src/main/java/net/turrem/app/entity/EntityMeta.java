package net.turrem.app.entity;

import net.turrem.app.EnumSide;
import net.turrem.app.mod.Mod;

abstract class EntityMeta
{
	EnumSide side = null;
	private final String id;
	Mod mod = null;
	
	public EntityMeta(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return this.mod.identifier + ":" + this.id;
	}
	
	public String getInternalId()
	{
		return this.id;
	}
	
	public Mod getMod()
	{
		return this.mod;
	}
	
	public EnumSide getSide()
	{
		return this.side;
	}
}
