package net.turrem.app;

import net.turrem.app.mod.ModInstance;

public class IdentifiableModFeature
{
	private final String id;
	private final ModInstance mod;
	
	public IdentifiableModFeature(String id, ModInstance mod)
	{
		this.id = mod.identifier + ":" + id;
		this.mod = mod;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public ModInstance getMod()
	{
		return this.mod;
	}
	
	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}
}
