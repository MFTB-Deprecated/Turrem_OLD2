package net.turrem.app.client.render.fbo;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class DiffuseFBO extends BaseFBO
{
	public DiffuseFBO(int width, int height)
	{
		super(width, height, 1);
	}
	
	protected DiffuseFBO(int width, int height, int size)
	{
		super(width, height, size + 1);
	}
	
	@Override
	protected void createAttachment(int attachment, int texture)
	{
		if (attachment == 0)
		{
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
		}
	}
	
	public int getDiffuse()
	{
		return this.getTexture(0);
	}
}
