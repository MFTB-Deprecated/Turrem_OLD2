package net.turrem.app.entity;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.turrem.app.EnumSide;
import net.turrem.app.mod.ModInstance;
import net.turrem.app.mod.registry.ClassWithFactoryRegistry;

public class EntityMetaRegistry extends ClassWithFactoryRegistry
{
	private final static List<Class<?>[]> valadParameters = new ArrayList<Class<?>[]>();
	
	public HashMap<String, SoftEntityMeta> softMetas = new HashMap<String, SoftEntityMeta>();
	public HashMap<String, SolidEntityMeta> solidMetas = new HashMap<String, SolidEntityMeta>();
	public HashMap<String, AmbientEntityMeta> ambientMetas = new HashMap<String, AmbientEntityMeta>();
	
	public final EnumSide side;
	
	public EntityMetaRegistry(EnumSide side)
	{
		super(EntityMeta.class);
		this.side = side;
	}
	
	@Override
	protected List<Class<?>[]> getPossibleFactoryParameters()
	{
		return EntityMetaRegistry.valadParameters;
	}
	
	@Override
	protected Object[] getArgs(int argsType, Annotation annotation, ModInstance mod)
	{
		RegisterEntityMeta reg = (RegisterEntityMeta) annotation;
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
		EntityMeta meta = (EntityMeta) item;
		meta.mod = mod;
		meta.side = this.side;
		if (meta instanceof SoftEntityMeta)
		{
			if (this.softMetas.put(meta.getId(), (SoftEntityMeta) meta) != null)
			{
				System.out.printf("A soft entity meta with id %s was already registered, it will be overridden.%n", meta.getId());
			}
		}
		else if (meta instanceof SolidEntityMeta)
		{
			if (this.solidMetas.put(meta.getId(), (SolidEntityMeta) meta) != null)
			{
				System.out.printf("A solid entity meta with id %s was already registered, it will be overridden.%n", meta.getId());
			}
		}
		else if (meta instanceof AmbientEntityMeta)
		{
			if (this.ambientMetas.put(meta.getId(), (AmbientEntityMeta) meta) != null)
			{
				System.out.printf("An ambient entity meta with id %s was already registered, it will be overridden.%n", meta.getId());
			}
		}
	}
	
	static
	{
		EntityMetaRegistry.valadParameters.add(new Class<?>[] {});
		EntityMetaRegistry.valadParameters.add(new Class<?>[] { EnumSide.class });
		EntityMetaRegistry.valadParameters.add(new Class<?>[] { EnumSide.class, String.class });
		EntityMetaRegistry.valadParameters.add(new Class<?>[] { String.class });
	}
}