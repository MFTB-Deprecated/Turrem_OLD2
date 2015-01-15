package net.turrem.app.client.font;

import org.lwjgl.opengl.GL11;

public class Glyph
{
	public final float glyphx;
	public final float glyphy;
	public final float width;
	public final float height;
	public final byte xoffset;
	public final byte yoffset;
	public final byte xadvance;
	public final byte page;
	
	public Glyph(float x, float y, float width, float height, byte xoffset, byte yoffset, byte xadvance, byte page)
	{
		super();
		this.glyphx = x;
		this.glyphy = y;
		this.width = width;
		this.height = height;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.xadvance = xadvance;
		this.page = page;
	}
	
	public float render(float x, float y, float scale)
	{
		float w = this.width * scale;
		float h = this.height * scale;
		x += this.xoffset * scale;
		y += this.yoffset * scale;
		GL11.glTexCoord2f(this.glyphx, this.glyphy);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(this.glyphx + this.width, this.glyphy);
		GL11.glVertex2f(x + w, y);
		GL11.glTexCoord2f(this.glyphx + this.width, this.glyphy + this.height);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(this.glyphx, this.glyphy + this.height);
		GL11.glVertex2f(x, y + h);
		return this.xadvance * scale;
	}
}
