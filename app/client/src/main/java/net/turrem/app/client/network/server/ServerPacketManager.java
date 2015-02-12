package net.turrem.app.client.network.server;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.turrem.app.client.game.world.ClientWorld;
import net.turrem.app.utils.CallList;

public class ServerPacketManager
{
	private static Map<Class<? extends ServerPacket>, Byte> packetClassMap;
	
	private static CallList[] packetProcessCalls;
	
	public static ServerPacket readPacket(byte packetType, int length, DataInput data) throws IOException
	{
		switch (packetType & 0xFF)
		{
			case 0x32:
				return ServerPacketPing.create(data, packetType);
			case 0xA0:
				return ServerPacketChat.create(data, length, packetType);
			case 0xFD:
				return ServerPacketKeepAlive.create(packetType);
			case 0xFE:
				return ServerPacketCustomNBT.create(data, length, packetType);
			case 0xFF:
				return ServerPacketCustom.create(data, length, packetType);
			default:
				return new NullPacket(packetType, length, data);
		}
	}
	
	public static Class<? extends ServerPacket> getPacket(byte packetType)
	{
		switch (packetType & 0xFF)
		{
			case 0x32:
				return ServerPacketPing.class;
			case 0xA0:
				return ServerPacketChat.class;
			case 0xFD:
				return ServerPacketKeepAlive.class;
			case 0xFE:
				return ServerPacketCustomNBT.class;
			case 0xFF:
				return ServerPacketCustom.class;
			default:
				return null;
		}
	}
	
	public static ServerPacket readSinglePacket(DataInputStream stream) throws IOException
	{
		byte type = (byte) stream.read();
		int length = (stream.read() << 8) | (stream.read() << 0);
		if (length == 0xFFFF)
		{
			length = (stream.read() << 24) | (stream.read() << 16) | (stream.read() << 8) | (stream.read() << 0);
		}
		byte[] packet = new byte[length];
		stream.readFully(packet);
		DataInput input = new DataInputStream(new ByteArrayInputStream(packet));
		return ServerPacketManager.readPacket(type, length, input);
	}
	
	public static boolean addProcessCall(Method call, byte type)
	{
		if (ServerPacketManager.getPacket(type) == null)
		{
			return false;
		}
		Class<?>[] pars = call.getParameterTypes();
		if (!Modifier.isStatic(call.getModifiers()))
		{
			return false;
		}
		if (pars.length == 2)
		{
			if (pars[0].isAssignableFrom(ServerPacket.class) && pars[1].isAssignableFrom(ClientWorld.class))
			{
				ServerPacketManager.packetProcessCalls[type & 0xFF].addCall(call);
				return true;
			}
		}
		return false;
	}
	
	public static boolean addProcessCall(Method call, Class<? extends ServerPacket> type)
	{
		Byte typeByte = ServerPacketManager.packetClassMap.get(type);
		if (typeByte != null)
		{
			return ServerPacketManager.addProcessCall(call, typeByte);
		}
		return false;
	}
	
	public static void processPacket(ServerPacket pak, ClientWorld world)
	{
		CallList list = ServerPacketManager.packetProcessCalls[pak.getPacketType() & 0xFF];
		Iterator<Method> mtds = list.getCalls();
		while (mtds.hasNext())
		{
			Method mtd = mtds.next();
			try
			{
				mtd.invoke(null, pak, world);
			}
			catch (IllegalAccessException | IllegalArgumentException e)
			{
				
			}
			catch (InvocationTargetException e)
			{
				e.getCause().printStackTrace();
			}
		}
	}
	
	static
	{
		ServerPacketManager.packetClassMap = new HashMap<Class<? extends ServerPacket>, Byte>();
		for (int i = 0; i <= 0xFF; i++)
		{
			Class<? extends ServerPacket> pak = ServerPacketManager.getPacket((byte) i);
			if (pak != null)
			{
				ServerPacketManager.packetClassMap.put(pak, (byte) i);
			}
		}
		
		ServerPacketManager.packetProcessCalls = new CallList[256];
		for (int i = 0; i < ServerPacketManager.packetProcessCalls.length; i++)
		{
			ServerPacketManager.packetProcessCalls[i] = new CallList();
		}
	}
}
