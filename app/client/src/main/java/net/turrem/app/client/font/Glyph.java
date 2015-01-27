package net.turrem.app.client.font;

import gnu.trove.map.hash.TIntByteHashMap;

import org.lwjgl.opengl.GL11;

public class Glyph
{
	public final byte glyphx;
	public final byte glyphy;
	public final byte width;
	public final byte height;
	public final byte xoffset;
	public final byte yoffset;
	public final byte xadvance;
	public final byte page;
	final TIntByteHashMap kerning = new TIntByteHashMap(0);
	
	public Glyph(byte glyphx, byte glyphy, byte width, byte height, byte xoffset, byte yoffset, byte xadvance, byte page)
	{
		this.glyphx = glyphx;
		this.glyphy = glyphy;
		this.width = width;
		this.height = height;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.xadvance = xadvance;
		this.page = page;
	}
	
	public void render(float x, float y, float scale)
	{
		int ux = this.glyphx & 0xFF;
		int uy = this.glyphx & 0xFF;
		int uw = this.width & 0xFF;
		int uh = this.height & 0xFF;
		float w = uw * scale;
		float h = uh * scale;
		x += this.xoffset * scale;
		y += this.yoffset * scale;
		GL11.glTexCoord2f(ux, uy);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(ux + uw, uy);
		GL11.glVertex2f(x + w, y);
		GL11.glTexCoord2f(ux + uw, uy + uh);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(ux, uy + uh);
		GL11.glVertex2f(x, y + h);
	}
}
