package net.turrem.app.client.render.shader;

import net.turrem.app.client.asset.Asset;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.mod.Mod;
import net.turrem.app.utils.stores.BasicIcon;
import net.turrem.app.utils.stores.IStore;

public class ShaderIcon extends BasicIcon<Shader>
{
	public final Asset shader;
	public final ShaderType type;
	
	public ShaderIcon(Mod mod, String asset, ShaderType type)
	{
		this(new Asset(mod, asset), type);
	}
	
	public ShaderIcon(Asset shader, ShaderType type)
	{
		this.shader = shader;
		this.type = type;
	}
	
	@Override
	public String toString()
	{
		return this.type.name() + " " + this.shader;
	}
	
	@Override
	public int hashCode()
	{
		return this.shader.hashCode() ^ (this.type.ordinal() * 12119 + 7933);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ShaderIcon)
		{
			ShaderIcon s = (ShaderIcon) obj;
			return this.type == s.type && this.shader.equals(s.shader);
		}
		return false;
	}
	
	@Override
	protected IStore<Shader> getStore()
	{
		return RenderEngine.shaders;
	}
}
