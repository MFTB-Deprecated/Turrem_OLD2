package net.turrem.app.client.render.fbo;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class WastefulGeometryFBO extends GeometryFBO
{
	public WastefulGeometryFBO(int width, int height)
	{
		super(width, height, 1);
	}
	
	@Override
	protected void createAttachment(int attachment, int texture)
	{
		if (attachment == 3)
		{
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_FLOAT, (FloatBuffer) null);
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL11.GL_TEXTURE_2D, texture, 0);
		}
		else
		{
			super.createAttachment(attachment, texture);
		}
	}
	
	public int getPosition()
	{
		return this.getTexture(3);
	}
}
