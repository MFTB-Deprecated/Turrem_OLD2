package net.turrem.app.client.font;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.PrimitiveIterator.OfInt;

import net.turrem.app.client.asset.AssetLoader;
import net.turrem.app.client.asset.GameAsset;
import net.turrem.app.utils.graphics.GLUtils;
import net.turrem.app.utils.graphics.ImgUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

public class Font
{
	final TIntObjectHashMap<Glyph> glyphs = new TIntObjectHashMap<>();
	public final int size;
	public final int lineHeight;
	private int[] textures;
	private GameAsset[] texturePaths;
	private boolean loaded = false;
	
	Font(int size, int lineHeight, GameAsset[] texturePaths)
	{
		this.size = size;
		this.lineHeight = lineHeight;
		this.texturePaths = texturePaths;
	}
	
	public void load(AssetLoader render)
	{
		if (this.loaded)
		{
			return;
		}
		try
		{
			this.textures = new int[this.texturePaths.length];
			for (int i = 0; i < this.textures.length; i++)
			{
				this.textures[i] = this.loadTexture(this.texturePaths[i], render);
			}
		}
		catch (IllegalArgumentException | IOException e)
		{
			return;
		}
	}
	
	float getScale(float size)
	{
		return size / this.size;
	}
	
	public void unload()
	{
		if (!this.loaded)
		{
			return;
		}
		for (int id : this.textures)
		{
			GL11.glDeleteTextures(id);
		}
		this.loaded = false;
	}
	
	private int loadTexture(GameAsset path, AssetLoader render) throws IOException
	{
		BufferedImage img = render.loadTexture(path);
		if (img != null)
		{
			int texId = GL11.glGenTextures();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL31.GL_TEXTURE_RECTANGLE, texId);
			
			ByteBuffer bytes = ImgUtils.imageToBuffer(img, ImgUtils.EnumPixelByte.ALPHA);
			
			GL11.glTexImage2D(GL31.GL_TEXTURE_RECTANGLE, 0, GL11.GL_RED, img.getWidth(), img.getHeight(), 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, bytes);
			GLUtils.glTexParameter(GL31.GL_TEXTURE_RECTANGLE, GL33.GL_TEXTURE_SWIZZLE_RGBA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_RED);
			GL11.glTexParameteri(GL31.GL_TEXTURE_RECTANGLE, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL31.GL_TEXTURE_RECTANGLE, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			
			return texId;
		}
		else
		{
			throw new IllegalArgumentException("Texture file does not exist: " + path);
		}
	}
	
	public void addGlyph(Glyph glyph, int codepoint)
	{
		this.glyphs.put(codepoint, glyph);
	}
	
	public Glyph getGlyph(int codepoint)
	{
		return this.glyphs.get(codepoint);
	}
	
	float segmentLength(final String text, final float scale, final boolean kerning)
	{
		float length = 0.0F;
		OfInt codes = text.codePoints().iterator();
		Glyph prev = null;
		while (codes.hasNext())
		{
			int code = codes.next();
			if (kerning && prev != null)
			{
				length += prev.kerning.get(code) * scale;
			}
			Glyph g = this.getGlyph(code);
			length += g.xadvance * scale;
			prev = g;
		}
		return length;
	}
	
	int segmentClip(final String text, final float scale, float length, final boolean kerning)
	{
		float unleng = length / scale;
		OfInt codes = text.codePoints().iterator();
		Glyph prev = null;
		int i = 0;
		while (codes.hasNext())
		{
			int code = codes.next();
			if (kerning && prev != null)
			{
				unleng -= prev.kerning.get(code);
			}
			Glyph g = this.getGlyph(code);
			unleng -= g.xadvance;
			if (unleng <= 0)
			{
				return text.offsetByCodePoints(0, i);
			}
			i++;
			prev = g;
		}
		return text.length();
	}
	
	void renderSegment(final String text, final float scale, float x, float y, final boolean kerning)
	{
		int texture = -1;
		OfInt codes = text.codePoints().iterator();
		Glyph prev = null;
		boolean started = false;
		while (codes.hasNext())
		{
			int code = codes.next();
			if (kerning && prev != null)
			{
				x += prev.kerning.get(code) * scale;
			}
			Glyph g = this.getGlyph(code);
			if (texture != (g.page & 0xFF))
			{
				texture = g.page & 0xFF;
				if (started)
				{
					GL11.glEnd();
				}
				this.bindTexture(this.textures[g.page & 0xFF]);
				GL11.glBegin(GL11.GL_QUADS);
				started = true;
			}
			g.render(x, y, scale);
			x += g.xadvance * scale;
			prev = g;
		}
		if (started)
		{
			GL11.glEnd();
		}
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private void bindTexture(int id)
	{
		GL11.glEnable(GL31.GL_TEXTURE_RECTANGLE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL31.GL_TEXTURE_RECTANGLE, id);
	}
}
