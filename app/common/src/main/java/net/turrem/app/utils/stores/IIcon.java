package net.turrem.app.utils.stores;

public interface IIcon<V>
{
	public void aquire();
	
	public boolean hasReceived();
	
	public void receive(V object, boolean success);
}
