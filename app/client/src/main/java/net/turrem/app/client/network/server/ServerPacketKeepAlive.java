package net.turrem.app.client.network.server;

public class ServerPacketKeepAlive extends ServerPacket
{
	private ServerPacketKeepAlive(byte type)
	{
		super(type);
	}
	
	public static ServerPacketKeepAlive create(byte type)
	{
		return new ServerPacketKeepAlive(type);
	}
}
