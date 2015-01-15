package net.turrem.app.client.font;

import org.lwjgl.opengl.GL11;

public class Font
{
	final GlyphPage[] pages = new GlyphPage[512];
	
	void addGlyph(Glyph glyph, char codepoint)
	{
		int page = codepoint >> 7;
		if (this.pages[page] == null)
		{
			this.pages[page] = new GlyphPage();
		}
		this.pages[page].page[codepoint & 0x7F] = glyph;
	}
	
	public Glyph getGlyph(char codepoint)
	{
		int page = codepoint >> 7;
		if (this.pages[page] == null)
		{
			return null;
		}
		return this.pages[page].page[codepoint & 0x7F];
	}
	
	String renderSegment(String text, float scale, float x, float y, float clipWidth, int[] textures)
	{
		float width = 0.0F;
		int i;
		int texture = -1;
		for (i = 0; i < text.length(); i++)
		{
			if (width > clipWidth)
			{
				break;
			}
			Glyph g = this.getGlyph(text.charAt(i));
			if (texture != (g.page & 0xFF))
			{
				texture = g.page & 0xFF;
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[g.page & 0xFF]);
			}
			width += g.render(x + width, y, scale);
		}
		return text.substring(i);
	}
}
