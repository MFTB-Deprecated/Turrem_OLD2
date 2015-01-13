package net.turrem.app.mod.registry;

import java.util.HashMap;

import net.turrem.app.IdentifiableModFeature;

public class FeatureRegistry<T extends IdentifiableModFeature> implements IInstanceRegistry<T>
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
