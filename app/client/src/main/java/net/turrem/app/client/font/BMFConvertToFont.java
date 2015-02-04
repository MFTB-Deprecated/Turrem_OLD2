package net.turrem.app.client.font;

import net.turrem.app.client.asset.GameAsset;
import net.turrem.app.client.font.bmf.BMFBlockChars.BMFChar;
import net.turrem.app.client.font.bmf.BMFBlockKerning.BMFKerningPair;
import net.turrem.app.client.font.bmf.BMFFile;

public class BMFConvertToFont
{
	private BMFFile bmf;
	private GameAsset textureLocation;
	private Font font = null;
	
	public BMFConvertToFont(BMFFile bmf, GameAsset textureLocation)
	{
		this.bmf = bmf;
		this.textureLocation = textureLocation;
	}
	
	public Font getFont()
	{
		if (this.font == null)
		{
			this.convert();
		}
		return this.font;
	}
	
	private void convert()
	{
		GameAsset[] textures = new GameAsset[this.bmf.pages.pageNames.length];
		for (int i = 0; i < textures.length; i++)
		{
			textures[i] = new GameAsset(this.textureLocation, this.bmf.pages.pageNames[i]);
		}
		this.font = new Font(this.bmf.info.fontSize, this.bmf.common.lineHeight, textures);
		for (BMFChar c : this.bmf.chars.chars)
		{
			Glyph g = this.convertGlyph(c);
			this.font.addGlyph(g, c.id);
		}
		if (this.bmf.kerning != null)
		{
			for (BMFKerningPair p : this.bmf.kerning.pairs)
			{
				Glyph g = this.font.getGlyph(p.first);
				if (g != null)
				{
					g.kerning.put(p.second, (byte) p.amount);
				}
			}
		}
	}
	
	private Glyph convertGlyph(BMFChar c)
	{
		return new Glyph((byte) c.x, (byte) c.y, (byte) c.width, (byte) c.height, (byte) c.xoffset, (byte) c.yoffset, (byte) c.xadvance, c.page);
	}
}
