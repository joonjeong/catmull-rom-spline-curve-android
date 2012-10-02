package joonjeong.gammagraphhistogram;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class GammaGraphUtils {
	public static void drawBackground(Canvas canvas, int backgroundColor,
			int width, int height) {
		Log.d("GammaGraph", "function drawBackground");
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setAntiAlias(true);
		paint.setColor(backgroundColor);
		canvas.drawRect(0, 0, width, height, paint);
	}

	public static void drawGraphBase(Canvas canvas, GammaGraphInfo gammaGraphInfo,
			List<RectF> boundLines) {
		Log.d("GammaGraph", "function drawGraphBase");
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		
		RectF baseRect = gammaGraphInfo.baseRect;
		canvas.drawRect(baseRect, paint);

		paint = new Paint();
		paint.setColor(Color.BLACK);
		final int textPadding = 15;
		canvas.drawText("0", baseRect.left - textPadding, baseRect.bottom
				+ textPadding, paint);
		canvas.drawText("255", baseRect.left - textPadding - 10, baseRect.top,
				paint);
		canvas.drawText("(X)", baseRect.left - textPadding - 7, baseRect.top
				+ textPadding, paint);
		canvas.drawText("255", baseRect.right, baseRect.bottom + textPadding,
				paint);
		canvas.drawText("(Y)", baseRect.right + 3, baseRect.bottom
				+ textPadding * 2, paint);

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setAlpha(70);
		canvas.drawLine(baseRect.left, baseRect.bottom, baseRect.right,
				baseRect.top, paint);
		if (boundLines != null) {
			for (RectF boundLine : boundLines) {
				canvas.drawLine(boundLine.left, boundLine.top, boundLine.right,
						boundLine.bottom, paint);
			}
		}
	}

	public static void drawGammaHistogram(Canvas canvas, GammaGraphInfo gammaGraphInfo) {
		Log.d("GammaGraph", "function drawGammaHistogram");
		int[] histogramData = new int[256];
		for (int i = 0; i < gammaGraphInfo.imageData.length; i++) {
			int gamma = (gammaGraphInfo.imageData[i] >> 16) & 0xff;
			if (histogramData[gamma] < 255) {
				histogramData[gamma]++;
			}
		}

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(2f);
		paint.setAlpha(90);

		RectF baseRect = gammaGraphInfo.baseRect;
		final float drawRectSide = baseRect.right - baseRect.left;
		final float zoom = drawRectSide / 255.0f;
		for (int i = 0; i < histogramData.length; i++) {
			canvas.drawLine(baseRect.left + i * zoom, baseRect.bottom,
					baseRect.left + i * zoom, baseRect.bottom
							- histogramData[i] * zoom + 2, paint);
		}
	}

	public static void drawGammaGraph(Canvas canvas,
			List<PointF> knots, int color, int alpha) {
		Log.d("GammaGraph", "function drawGammaGraph");
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setAlpha(alpha);
		cubic_hermite_spline(canvas, knots, 0.0005f, paint);
	}
	
	private static PointF catmull_rom_spline_tangent(PointF p0, PointF p1) {
		return new PointF((p0.x - p1.x) / 2, (p0.y - p1.y) / 2);
	}

	private static void cubic_hermite_spline(Canvas canvas, List<PointF> knots, float delta, Paint paint) {		
		int n = knots.size();
		float px = 0;
		float py = 0;
		for (int i = 0; i < n; i++) {
			// interpolation
			for (float t = 0; t < 1; t += delta) {
				float h00 = (1 + 2 * t) * (1 - t) * (1 - t);
				float h10 = t * (t - 1) * (t - 1);
				float h01 = t * t * (3 - 2 * t);
				float h11 = t * t * (t - 1);

				if (i == 0) {
					PointF p0 = knots.get(i);
					PointF p1 = knots.get(i + 1);
					PointF p2 = null;
					if (n > 2) {
						p2 = knots.get(i + 2);
					} else {
						p2 = p1;
					}
					PointF m0 = catmull_rom_spline_tangent(p1, p0);
					PointF m1 = catmull_rom_spline_tangent(p2, p0);

					px = h00 * p0.x + h10 * m0.x + h01 * p1.x + h11
							* m1.x;
					py = h00 * p0.y + h10 * m0.y + h01 * p1.y + h11
							* m1.y;
					canvas.drawPoint(px, py, paint);
				} else if (i < n - 2) {
					PointF p0 = knots.get(i - 1);
					PointF p1 = knots.get(i);
					PointF p2 = knots.get(i + 1);
					PointF p3 = knots.get(i + 2);

					PointF m0 = catmull_rom_spline_tangent(p2, p0);
					PointF m1 = catmull_rom_spline_tangent(p3, p1);

					px = h00 * p1.x + h10 * m0.x + h01 * p2.x + h11
							* m1.x;
					py = h00 * p1.y + h10 * m0.y + h01 * p2.y + h11
							* m1.y;
					canvas.drawPoint(px, py, paint);
				} else if (i == n - 1) {
					if (n < 3) {
						continue;
					}
					PointF p0 = knots.get(i - 2);
					PointF p1 = knots.get(i - 1);
					PointF p2 = knots.get(i);

					PointF m0 = catmull_rom_spline_tangent(p2, p0);
					PointF m1 = catmull_rom_spline_tangent(p2, p1);

					px = h00 * p1.x + h10 * m0.x + h01 * p2.x + h11
							* m1.x;
					py = h00 * p1.y + h10 * m0.y + h01 * p2.y + h11
							* m1.y;
					canvas.drawPoint(px, py, paint);
				}
				/*
				RectF baseRect = knots.baseRect;
				float zoom = (baseRect.right - baseRect.left) / 255f;
				int gammaIndex = (int)((px - baseRect.left) / zoom);
				int gammaValue = (int)((baseRect.bottom - baseRect.top - py) / zoom);
				knots.gammatable[gammaIndex] = gammaValue;
				*/ 
			}
		}
	}

	public static RectF createBaseRect(RectF graphRect, RectF marginRect) {
		Log.d("GammaGraph", "function createDrawRect");
		return new RectF(graphRect.left + marginRect.left, graphRect.top
				+ marginRect.top, graphRect.right, graphRect.bottom);
	}

	public static List<RectF> createBoundLines(RectF baseRect,
			final float graphSideZoom) {
		Log.d("GammaGraph", "function createBoundLines");
		List<RectF> boundLines = new ArrayList<RectF>();
		final float drawRectSide = baseRect.right - baseRect.left;
		final float rectSideQuadDiv = drawRectSide / 4;
		for (int i = 1; i <= 3; i++) {
			final float linePos = i * rectSideQuadDiv;
			RectF boundLine1 = new RectF(baseRect.left + linePos,
					baseRect.top + 1, baseRect.left + linePos,
					baseRect.bottom - 1);
			RectF boundLine2 = new RectF(baseRect.left + 1, baseRect.top
					+ linePos, baseRect.right - 1, baseRect.top + linePos);
			boundLines.add(boundLine1);
			boundLines.add(boundLine2);
		}
		return boundLines;
	}

	public static void drawKnots(Canvas canvas, List<PointF> knots, int color) {
		Log.d("GammaGraph", "function drawKnots");
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStrokeWidth(7);
		for (PointF knot : knots) {
			canvas.drawPoint(knot.x, knot.y, paint);
		}
	}

	private static boolean isOnCircle(PointF target, PointF base, float d) {
		float dx = target.x - base.x;
		float dy = target.y - base.y;
		return Math.sqrt(dx * dx + dy * dy) < d;
	}
	
	public static PointF selectKnot(PointF touchPoint, List<PointF> knots) {
		for(PointF knot : knots) {
			if(isOnCircle(touchPoint, knot, 20f)) {
				return knot;
			}
		}
		return null;
	}
}
