package net.turrem.app.client.network.server;

public abstract class ServerPacket
{
	private final byte packetType;
	
	public ServerPacket(byte type)
	{
		this.packetType = type;
	}
	
	public byte getPacketType()
	{
		return this.packetType;
	}
}
