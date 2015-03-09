package net.turrem.app.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import net.turrem.app.client.render.fbo.DiffuseFBO;
import net.turrem.app.client.render.shader.ShaderUniform;

public abstract class RenderScreen
{
	public final RenderEngine engine;
	public DiffuseFBO target;
	
	public RenderScreen(RenderEngine engine, DiffuseFBO target)
	{
		this.engine = engine;
		this.target = target;
	}
	
	public abstract void render();
	
	protected void renderLayer(IScreenLayer layer)
	{
		ShaderUniform un = new ShaderUniform();
		this.target.bind();
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		layer.render(this.engine, this.target);
		this.target.unbind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.target.getDiffuse());
		this.engine.blit.bind();
		un.setInt(0);
		un.upload(this.engine.blit, "samp_diffuse");
		GL11.glEnable(GL11.GL_BLEND);
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		this.target.drawQuad();
		GL11.glDisable(GL11.GL_BLEND);
		this.engine.blit.unbind();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public abstract void end();
}
