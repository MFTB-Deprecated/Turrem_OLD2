package net.turrem.app.mod.registry;

import java.util.HashMap;

import net.turrem.app.IdentifiableFeature;

public class FeatureRegistry<T extends IdentifiableFeature> implements IInstanceRegistry<T>
{
	private HashMap<String, T> items = new HashMap<String, T>();
	
	public T getItem(String id)
	{
		return this.items.get(id);
	}
	
	@Override
	public void register(T instance)
	{
		
	}
}
