package joonjeong.gammagraphhistogram;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PointF;
import android.graphics.RectF;


public class GammaGraphInfo {
	public final int[] imageData;
	public final List<PointF> knots;
	public final RectF baseRect;
	public final int[] gammatable;
	
	public GammaGraphInfo(int[] imageData) {
		this.imageData = imageData;
		this.knots = new ArrayList<PointF>();
		this.baseRect = new RectF();
		this.gammatable = new int[256];
	}
}
