package net.turrem.app;

import net.turrem.app.mod.Mod;

public class IdentifiableFeature
{
	public final Mod mod;
	public final String identifier;
	
	public IdentifiableFeature(Mod mod, String identifier)
	{
		this.mod = mod;
		this.identifier = identifier;
	}
	
	public String getRaw()
	{
		return this.mod.identifier + ":" + this.identifier;
	}
	
	@Override
	public String toString()
	{
		return this.getRaw();
	}
	
	public static IdentifiableFeature fromRaw(String raw)
	{
		int split = raw.indexOf(':');
		Mod mod = Mod.getMod(raw.substring(0, split));
		String id = raw.substring(split + 1);
		return new IdentifiableFeature(mod, id);
	}
}
