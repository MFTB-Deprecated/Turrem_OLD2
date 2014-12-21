package net.turrem.utils.img;

public class ColorUtils
{
	public static enum EnumLuminanceSum
	{
		CIE1931(0.2126F, 0.7152F, 0.0722F),
		REC709(0.2126F, 0.7152F, 0.0722F),
		REC601(0.299F, 0.587F, 0.114F);
		
		private final float redWeight;
		private final float greenWeight;
		private final float blueWeight;
		
		EnumLuminanceSum(float redWeight, float greenWeight, float blueWeight)
		{
			this.redWeight = redWeight;
			this.greenWeight = greenWeight;
			this.blueWeight = blueWeight;
		}
		
		public float compute(float red, float green, float blue)
		{
			return red * this.redWeight + green * this.greenWeight + blue * this.blueWeight;
		}
	}
}
