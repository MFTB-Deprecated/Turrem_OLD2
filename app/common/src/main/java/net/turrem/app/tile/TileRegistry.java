package net.turrem.app.tile;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.turrem.app.EnumSide;
import net.turrem.app.mod.ModInstance;
import net.turrem.app.mod.registry.ClassWithFactoryRegistry;

public class TileRegistry extends ClassWithFactoryRegistry
{
	private final static List<Class<?>[]> valadParameters = new ArrayList<Class<?>[]>();
	
	public HashMap<String, BasicTile> tiles = new HashMap<String, BasicTile>();
	public HashMap<String, DynamicTile> dynamicTiles = new HashMap<String, DynamicTile>();
	
	public final EnumSide side;
	
	public TileRegistry(EnumSide side)
	{
		super(Tile.class);
		this.side = side;
	}
	
	@Override
	protected List<Class<?>[]> getPossibleFactoryParameters()
	{
		return TileRegistry.valadParameters;
	}
	
	@Override
	protected Object[] getArgs(int argsType, Annotation annotation, ModInstance mod)
	{
		RegisterTile reg = (RegisterTile) annotation;
		switch (argsType)
		{
			case 0:
				return new Object[] {};
			case 1:
				return new Object[] { this.side };
			case 2:
				return new Object[] { this.side, reg.id() };
			case 3:
				return new Object[] { reg.id() };
			default:
				return new Object[] {};
		}
	}
	
	@Override
	protected void addItem(Object item, ModInstance mod)
	{
		Tile tile = (Tile) item;
		tile.mod = mod;
		tile.side = this.side;
		if (tile instanceof BasicTile)
		{
			if (this.tiles.put(tile.getId(), (BasicTile) tile) != null)
			{
				System.out.printf("A basic tile id %s was already registered, it will be overridden.%n", tile.getId());
			}
		}
		else if (tile instanceof DynamicTile)
		{
			if (this.dynamicTiles.put(tile.getId(), (DynamicTile) tile) != null)
			{
				System.out.printf("A dynamic tile with id %s was already registered, it will be overridden.%n", tile.getId());
			}
		}
		else
		{
			System.out.printf("A tile with id %s did not extend BasicTile or DynamicTile.%n", tile.getId());
		}
	}
	
	static
	{
		TileRegistry.valadParameters.add(new Class<?>[] {});
		TileRegistry.valadParameters.add(new Class<?>[] { EnumSide.class });
		TileRegistry.valadParameters.add(new Class<?>[] { EnumSide.class, String.class });
		TileRegistry.valadParameters.add(new Class<?>[] { String.class });
	}
}
