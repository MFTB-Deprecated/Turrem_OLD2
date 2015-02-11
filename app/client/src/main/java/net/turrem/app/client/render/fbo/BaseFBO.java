package net.turrem.app.client.render.fbo;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map.Entry;

import net.turrem.app.utils.graphics.GLUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class BaseFBO
{
	public final int width;
	public final int height;
	private FrameBufferObject fbo;
	private boolean created = false;
	private int[] buffers;
	
	public BaseFBO(int width, int height, int size)
	{
		this.width = width;
		this.height = height;
		this.fbo = new FrameBufferObject(size);
		this.buffers = new int[EnumDrawBufferLocs.numberOfLocations];
	}
	
	public int getTexture(int attachment)
	{
		return this.fbo.getTexture(attachment);
	}
	
	public void create()
	{
		if (this.created)
		{
			return;
		}
		
		this.fbo.create();
		this.fbo.bind();
		
		EnumMap<EnumDrawBufferLocs, Integer> bufsmap = new EnumMap<>(EnumDrawBufferLocs.class);
		
		int texture;
		for (int i = 0; i < this.fbo.textureCount(); i++)
		{
			texture = this.fbo.getTexture(i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			this.createAttachment(i, texture, bufsmap);
		}
		
		Arrays.fill(this.buffers, GL11.GL_NONE);
		
		for (Entry<EnumDrawBufferLocs, Integer> buf : bufsmap.entrySet())
		{
			this.buffers[buf.getKey().location] = buf.getValue();
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		this.fbo.unbind();
		this.created = true;
	}
	
	public void setDrawBuffers()
	{
		if (this.created)
		{
			GL20.glDrawBuffers(GLUtils.bufferInts(this.buffers));
		}
	}
	
	protected abstract void createAttachment(int attachment, int texture, EnumMap<EnumDrawBufferLocs, Integer> locations);
	
	public void delete()
	{
		if (this.created)
		{
			this.fbo.delete();
			this.created = false;
		}
	}
	
	public void bind()
	{
		this.fbo.bind();
	}
	
	public void unbind()
	{
		this.fbo.unbind();
	}
}
