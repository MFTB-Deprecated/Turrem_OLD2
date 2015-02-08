package net.turrem.app.client.render.fbo;

import org.lwjgl.opengl.GL11;

public abstract class BaseFBO
{
	public final int width;
	public final int height;
	private FrameBufferObject fbo;
	private boolean created = false;
	
	public BaseFBO(int width, int height, int size)
	{
		this.width = width;
		this.height = height;
		this.fbo = new FrameBufferObject(size);
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
		
		int texture;
		for (int i = 0; i < this.fbo.textureCount(); i++)
		{
			texture = this.fbo.getTexture(i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
			this.createAttachment(i, texture);
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		this.fbo.unbind();
		this.created = true;
	}
	
	protected abstract void createAttachment(int attachment, int texture);
	
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
