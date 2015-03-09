package net.turrem.app.utils.stores;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;

public abstract class BasicStore<V> implements IStore<V>
{
	public class StoreAdd
	{
		public final BasicIcon<V> requester;
		
		private StoreAdd(BasicIcon<V> icon)
		{
			this.requester = icon;
		}
		
		public void give(V object)
		{
			BasicStore.this.give(this.requester, object, true);
		}
		
		public void fail(V object)
		{
			BasicStore.this.give(this.requester, object, false);
		}
		
		public void fail()
		{
			this.fail(null);
		}
	}
	
	private Object lock = new Object();
	
	protected HashMap<BasicIcon<V>, V> store = new HashMap<BasicIcon<V>, V>();
	private ArrayListMultimap<BasicIcon<V>, BasicIcon<V>> customers = ArrayListMultimap.create();
	
	private void give(BasicIcon<V> requester, V object, boolean success)
	{
		synchronized (this.lock)
		{
			List<BasicIcon<V>> requesters = this.customers.removeAll(requester);
			if (success)
			{
				this.store.put(requester, object);
			}
			for (BasicIcon<V> customer : requesters)
			{
				customer.receive(object, success);
			}
		}
	}
	
	@Override
	public void aquire(IIcon<V> icon)
	{
		if (icon instanceof BasicIcon)
		{
			BasicIcon<V> bicon = (BasicIcon<V>) icon;
			boolean isNew = false;
			synchronized (this.lock)
			{
				if (this.store.containsKey(bicon))
				{
					bicon.receive(this.store.get(bicon), true);
					return;
				}
				isNew = !this.customers.containsKey(bicon);
				this.customers.put(bicon, bicon);
			}
			if (isNew)
			{
				this.create(new StoreAdd(bicon));
			}
		}
		else
		{
			throw new IllegalArgumentException("ThreadableStores can not use IIcons that don't extend BasicIcon.");
		}
	}
	
	public abstract void create(StoreAdd add);
}
