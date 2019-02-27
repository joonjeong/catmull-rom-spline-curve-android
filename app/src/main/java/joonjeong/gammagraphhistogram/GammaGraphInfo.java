package joonjeong.gammagraphhistogram;

import android.graphics.*;

import java.util.*;

public class GammaGraphInfo {
	public final int[] imageData;
	public final List<PointF> knots;
	public final RectF baseRect;
	public final int[] gammatable;

	public GammaGraphInfo(int[] imageData) {
		this.imageData = imageData;
		this.knots = new ArrayList<>();
		this.baseRect = new RectF();
		this.gammatable = new int[256];
	}
}
