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
		float ux = this.glyphx & 0xFF;
		float uy = this.glyphy & 0xFF;
		float uw = this.width & 0xFF;
		float uh = this.height & 0xFF;
		float w = uw * scale;
		float h = uh * scale;
		x += this.xoffset * scale;
		//y += this.yoffset * scale;
		ux /= 256.0F;
		uy /= 256.0F;
		uw /= 256.0F;
		uh /= 256.0F;
		GL11.glTexCoord2f(ux, uy);
		GL11.glVertex2f(x, y + h);
		GL11.glTexCoord2f(ux + uw, uy);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(ux + uw, uy + uh);
		GL11.glVertex2f(x + w, y);
		GL11.glTexCoord2f(ux, uy + uh);
		GL11.glVertex2f(x, y);
	}
}
