package net.turrem.app.client.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.turrem.app.Config;
import net.turrem.app.client.game.ClientGame;
import net.turrem.app.client.network.client.ClientPacket;
import net.turrem.app.client.network.client.ClientPacketKeepAlive;
import net.turrem.app.client.network.server.NullPacket;
import net.turrem.app.client.network.server.ServerPacket;
import net.turrem.app.client.network.server.ServerPacketManager;
import net.turrem.app.network.NetworkConnection;

public class GameConnection
{
	public static int serverLimitPerTick = 1000;
	
	public NetworkConnection network;
	
	public ClientGame theGame;
	
	public Queue<ClientPacket> outgoing;
	public Queue<ServerPacket> incoming;
	
	protected volatile DataInputStream input;
	protected volatile DataOutputStream output;
	
	private boolean isRunning = false;
	
	private int outTimer = 0;
	
	private Thread readThread;
	private Thread writeThread;
	
	private int currentWriteCount = 0;
	
	public GameConnection(NetworkConnection network, ClientGame game)
	{
		this.isRunning = true;
		this.network = network;
		this.theGame = game;
		this.outgoing = new ConcurrentLinkedQueue<ClientPacket>();
		this.incoming = new ConcurrentLinkedQueue<ServerPacket>();
		
		this.input = this.network.getInput();
		this.output = this.network.getOutput();
		
		this.readThread = new ConnectionReaderThread(this);
		this.writeThread = new ConnectionWriterThread(this);
		this.readThread.start();
		this.writeThread.start();
	}
	
	public void addToSendQueue(ClientPacket packet)
	{
		if (this.isRunning)
		{
			this.outgoing.add(packet);
			if (this.sendQueueSize() > Config.connectionOutQueueOverflow)
			{
				this.shutdown("Out queue overflow");
			}
		}
	}
	
	public int sendQueueSize()
	{
		return this.outgoing.size();
	}
	
	public int recieveQueueSize()
	{
		return this.incoming.size();
	}
	
	private boolean readPacket()
	{
		if (!this.isRunning)
		{
			return false;
		}
		ServerPacket pak;
		try
		{
			pak = ServerPacketManager.readSinglePacket(this.input);
		}
		catch (IOException e)
		{
			this.shutdown("IOException during read", e);
			return false;
		}
		if (pak != null)
		{
			if (pak instanceof NullPacket)
			{
				System.err.printf("Warning! %d byte Null Packet with id: %d%n", ((NullPacket) pak).length, pak.getPacketType());
			}
			else
			{
				this.incoming.add(pak);
			}
		}
		else
		{
			System.err.println("Warning! Null Packet");
		}
		return true;
	}
	
	public static boolean readPacket(GameConnection connection)
	{
		return connection.readPacket();
	}
	
	private boolean writePacket()
	{
		if (!this.isRunning)
		{
			return false;
		}
		ClientPacket pak = this.outgoing.poll();
		if (pak != null)
		{
			try
			{
				pak.write(this.output);
				this.currentWriteCount++;
				return true;
			}
			catch (IOException e)
			{
				this.shutdown("IOException during write", e);
				return false;
			}
		}
		return false;
	}
	
	public static boolean writePacket(GameConnection connection)
	{
		return connection.writePacket();
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	public void flushWrite()
	{
		if (this.isRunning)
		{
			if (this.currentWriteCount == 0)
			{
				ClientPacketKeepAlive alive = new ClientPacketKeepAlive();
				try
				{
					alive.write(this.output);
				}
				catch (IOException e)
				{
					this.shutdown("Failed write keep alive", e);
				}
			}
			this.currentWriteCount = 0;
			try
			{
				this.output.flush();
			}
			catch (IOException e)
			{
				this.shutdown("Failed to flush write", e);
			}
		}
	}
	
	public void processPackets()
	{
		if (this.incoming.isEmpty())
		{
			if (this.outTimer++ > Config.connectionTimeoutLimit)
			{
				this.shutdown("Timeout");
			}
		}
		else
		{
			this.outTimer = 0;
		}
		if (this.recieveQueueSize() > Config.connectionInQueueOverflow)
		{
			this.shutdown("In queue overflow");
		}
		int i = serverLimitPerTick;
		while (i-- > 0 && this.isRunning)
		{
			ServerPacket pak = this.incoming.poll();
			if (pak != null)
			{
				this.theGame.processPacket(pak);
			}
			else
			{
				break;
			}
		}
	}
}
