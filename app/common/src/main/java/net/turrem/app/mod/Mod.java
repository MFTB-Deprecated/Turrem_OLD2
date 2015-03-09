package net.turrem.app.mod;

public class Mod
{
	public static final Mod APP = new Mod("app", "$APP_VERSION$");
	public static final Mod CORE = new Mod("core", "$CORE_VERSION$");
	
	public final String identifier;
	public final String version;
	private final int hash;
	
	public Mod(String id)
	{
		this(id, null);
	}
	
	public Mod(String id, String version)
	{
		this.identifier = id;
		this.version = version;
		this.hash = this.identifier.hashCode();
	}
	
	public boolean isApp()
	{
		return this.identifier.equals("app");
	}
	
	@Override
	public int hashCode()
	{
		return this.hash;
	}
	
	public ModInstance get()
	{
		return ModLoader.instance().getMod(this);
	}
	
	@Override
	public boolean equals(Object x)
	{
		if (x == this)
		{
			return true;
		}
		if (x instanceof Mod)
		{
			Mod mod = (Mod) x;
			if (this.hashCode() != mod.hashCode())
			{
				return false;
			}
			boolean flag = mod.identifier.equals(this.identifier);
			if (mod.version != null && this.version != null)
			{
				flag &= mod.version.equals(this.version);
			}
			return flag;
		}
		return false;
	}
	
	public static Mod getMod(String identifier)
	{
		String vers = null;
		int split = identifier.indexOf('#');
		if (split != -1)
		{
			vers = identifier.substring(split + 1);
			identifier = identifier.substring(0, split);
		}
		return new Mod(identifier, vers);
	}
}
