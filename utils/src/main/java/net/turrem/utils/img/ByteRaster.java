package net.turrem.utils.img;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteRaster
{
	public final int bytesPerPixel;
	public final int width;
	public final int height;
	protected ByteBuffer buffer;
	
	public ByteRaster(int bytesPerPixel, int width, int height)
	{
		this.bytesPerPixel = bytesPerPixel;
		this.width = width;
		this.height = height;
		
		this.buffer = ByteBuffer.allocate(this.width * this.height * this.bytesPerPixel).order(ByteOrder.nativeOrder());
	}
	
	public ByteRaster(int bytesPerPixel, int width, int height, DataInputStream in) throws IOException
	{
		this.bytesPerPixel = bytesPerPixel;
		this.width = width;
		this.height = height;
		
		this.buffer = ByteBuffer.allocate(this.width * this.height * this.bytesPerPixel).order(ByteOrder.nativeOrder());
		
		in.readFully(this.buffer.array());
	}
	
	public void write(DataOutputStream out) throws IOException
	{
		out.write(this.buffer.array());
	}
	
	public ByteBuffer getBuffer()
	{
		return this.buffer.duplicate();
	}
	
	public ByteBuffer getReadBuffer()
	{
		return this.buffer.asReadOnlyBuffer();
	}
}
