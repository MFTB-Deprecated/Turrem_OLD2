package net.turrem.app.client.render.shader;

import com.google.common.collect.ImmutableSet;

import net.turrem.app.client.asset.Asset;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.fbo.EnumDrawBufferLocs;
import net.turrem.app.utils.stores.BasicIcon;
import net.turrem.app.utils.stores.IStore;

public class ProgramIcon extends BasicIcon<Program>
{
	public final ImmutableSet<ShaderIcon> shaders;
	public final EnumDrawBufferLocs[] locs;
	
	public ProgramIcon(Asset both, EnumDrawBufferLocs... locs)
	{
		this(both, both, locs);
	}
	
	public ProgramIcon(Asset vert, Asset frag, EnumDrawBufferLocs... locs)
	{
		this(new ShaderIcon(vert, ShaderType.VERTEX), new ShaderIcon(frag, ShaderType.FRAGMENT), locs);
	}
	
	public ProgramIcon(ShaderIcon vert, ShaderIcon frag, EnumDrawBufferLocs... locs)
	{
		this(new ShaderIcon[] { vert, frag }, locs);
	}
	
	public ProgramIcon(ShaderIcon[] shaders, EnumDrawBufferLocs... locs)
	{
		this.shaders = ImmutableSet.copyOf(shaders);
		this.locs = locs;
	}
	
	@Override
	public String toString()
	{
		String out = "{ ";
		boolean started = false;
		for (ShaderIcon shad : this.shaders)
		{
			if (started)
			{
				out += ", ";
			}
			started = true;
			out += shad;
		}
		out += " } to ";
		if (this.locs.length > 1)
		{
			out += "{ ";
		}
		started = false;
		for (EnumDrawBufferLocs loc : this.locs)
		{
			if (started)
			{
				out += ", ";
			}
			started = true;
			out += loc;
		}
		if (this.locs.length > 1)
		{
			out += " }";
		}
		return out;
	}
	
	@Override
	public int hashCode()
	{
		int hash = 0;
		for (ShaderIcon shad : this.shaders)
		{
			hash ^= shad.hashCode();
		}
		int lochash = 0;
		for (EnumDrawBufferLocs loc : this.locs)
		{
			lochash |= 1 << loc.ordinal();
		}
		return hash + lochash;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ProgramIcon)
		{
			ProgramIcon pi = (ProgramIcon) obj;
			if (pi.shaders.size() != this.shaders.size())
			{
				return false;
			}
			return pi.locs == this.locs && pi.shaders.containsAll(this.shaders);
		}
		return false;
	}
	
	@Override
	protected IStore<Program> getStore()
	{
		return RenderEngine.programs;
	}
}
