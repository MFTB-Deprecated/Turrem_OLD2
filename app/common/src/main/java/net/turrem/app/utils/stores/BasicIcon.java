package net.turrem.app.utils.stores;

public abstract class BasicIcon<V> implements IIcon<V>
{
	public V object = null;
	private boolean requested = false;
	private boolean received = false;
	
	protected abstract IStore<V> getStore();
	
	@Override
	public void aquire()
	{
		if (!this.requested && !this.received)
		{
			IStore<V> store = this.getStore();
			if (store != null)
			{
				store.aquire(this);
				this.requested = true;
			}
		}
	}
	
	public void reaquire()
	{
		this.object = null;
		this.requested = false;
		this.received = false;
		this.aquire();
	}
	
	@Override
	public boolean hasReceived()
	{
		return this.received;
	}
	
	@Override
	public void receive(V object, boolean success)
	{
		this.received = success;
		this.object = object;
	}
}
