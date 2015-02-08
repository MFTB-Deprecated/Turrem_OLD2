package net.turrem.app.client.render;

import net.turrem.app.client.render.fbo.DiffuseFBO;

public interface IScreenLayer
{
	public void render(RenderEngine engine, DiffuseFBO target);
	
	public boolean isOutdated();
	
	public boolean hasContent();
	
	public boolean pauseRequested();
}
