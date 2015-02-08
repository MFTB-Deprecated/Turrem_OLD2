package net.turrem.app.client.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GeometryFBO
{
	private int width;
	private int height;
	private FrameBufferObject fbo;
	private boolean created = false;
	
	public GeometryFBO(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.fbo = new FrameBufferObject(3);
	}
	
	public void create()
	{
		if (this.created)
		{
			return;
		}
		
		this.fbo.create();
		this.fbo.bind();
		int tex;
		
		tex = this.getDiffuse();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, FrameBufferObject.glColorAttachment(0), GL11.GL_TEXTURE_2D, tex, 0);
		
		tex = this.getNormal();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_FLOAT, (FloatBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, FrameBufferObject.glColorAttachment(1), GL11.GL_TEXTURE_2D, tex, 0);
		
		tex = this.getDepth();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F, this.width, this.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (FloatBuffer) null);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, tex, 0);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		this.fbo.unbind();
		this.created = true;
	}
	
	public int getDiffuse()
	{
		return this.fbo.getTexture(0);
	}
	
	public int getNormal()
	{
		return this.fbo.getTexture(1);
	}
	
	public int getDepth()
	{
		return this.fbo.getTexture(2);
	}
	
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
