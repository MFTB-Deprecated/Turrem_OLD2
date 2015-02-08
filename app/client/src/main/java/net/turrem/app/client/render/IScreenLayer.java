package net.turrem.app.client.render;

public interface IScreenLayer
{
	public void render(RenderEngine engine, FrameBufferObject target);
	
	public boolean isOutdated();
	
	public boolean hasContent();
	
	public boolean pauseRequested();
}
