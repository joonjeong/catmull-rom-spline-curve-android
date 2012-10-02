package joonjeong.gammagraphhistogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GammaGraphView extends SurfaceView implements
		SurfaceHolder.Callback {

	private final GammaGraphInfo gammaGraphInfo;
	private List<RectF> boundLines;
	
	
	public GammaGraphView(Context context, GammaGraphInfo gammaGraphInfo) {
		super(context);
		this.gammaGraphInfo = gammaGraphInfo;
		
		getHolder().addCallback(this);
		setWillNotDraw(false);
		Log.d("GammaGraph", "constructor");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d("GammaGraph", "surfaceCreated Start");
		float graphSideZoom = 0;
		float side = 0;
		switch (getResources().getConfiguration().orientation) {
		case Configuration.ORIENTATION_LANDSCAPE:
			graphSideZoom = this.getHeight() / 255.0f;
			side = Math.round(graphSideZoom) * 255;
			if (side > this.getHeight()) {
				side = this.getHeight() - Math.abs(this.getHeight() - side);
				graphSideZoom = graphSideZoom * side / this.getHeight();
			}
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			graphSideZoom = this.getWidth() / 255.0f;
			side = Math.round(graphSideZoom) * 255;
			if (side > this.getWidth()) {
				side = this.getWidth() - Math.abs(this.getWidth() - side);
				graphSideZoom = graphSideZoom * side / this.getWidth();
			}
			break;
		}
		RectF graphRect = new RectF(0, 0, side, side);
		RectF marginRect = new RectF(30, 30, 0, 0);
		RectF drawRect = GammaGraphUtils.createBaseRect(graphRect, marginRect);
		this.gammaGraphInfo.baseRect.left = drawRect.left;
		this.gammaGraphInfo.baseRect.top = drawRect.top;
		this.gammaGraphInfo.baseRect.right = drawRect.right;
		this.gammaGraphInfo.baseRect.bottom = drawRect.bottom;
		
		this.boundLines = GammaGraphUtils.createBoundLines(drawRect, graphSideZoom);

		if(this.gammaGraphInfo.knots.size() == 0) {
			this.gammaGraphInfo.knots.clear();
			this.gammaGraphInfo.knots.add(new PointF(drawRect.left, drawRect.bottom));
			this.gammaGraphInfo.knots.add(new PointF(drawRect.right, drawRect.top));
		}
		
		Random rand = new Random();
		for (int i = 0; i < gammaGraphInfo.imageData.length; i++) {
			gammaGraphInfo.imageData[i] = rand.nextInt(Integer.MAX_VALUE)
					% Integer.MAX_VALUE;
		}
		Log.d("GammaGraph", "surfaceCreated End");
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d("GammaGraph", "surfaceChanged Start");
		Log.d("GammaGraph", "surfaceChanged End");
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("GammaGraph", "surfaceDestroyed Start");
		Log.d("GammaGraph", "surfaceDestroyed End");
	}

	private List<PointF> tmpKnots = new ArrayList<PointF>();
	private boolean isMoving = false;
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d("GammaGraph", "onDraw Start");
		GammaGraphUtils.drawBackground(canvas, Color.WHITE, this.getWidth(),
				this.getHeight());
		GammaGraphUtils.drawGammaHistogram(canvas, this.gammaGraphInfo);
		if(isMoving) {
			Collections.sort(this.tmpKnots, pointfComparator);
			GammaGraphUtils.drawGammaGraph(canvas, this.tmpKnots,Color.RED, 70);
			GammaGraphUtils.drawKnots(canvas, this.tmpKnots, Color.RED);
		}
		Collections.sort(this.gammaGraphInfo.knots, pointfComparator);
		GammaGraphUtils.drawGammaGraph(canvas, this.gammaGraphInfo.knots, Color.BLACK, 100);
		GammaGraphUtils.drawGraphBase(canvas, this.gammaGraphInfo, this.boundLines);
		GammaGraphUtils.drawKnots(canvas, this.gammaGraphInfo.knots, Color.BLACK);
		
		Log.d("GammaGraph", "onDraw End");
	}
	
	private Comparator<PointF> pointfComparator = new Comparator<PointF>() {
		public int compare(PointF a, PointF b) {
			return (int) (a.x - b.x);
		}
	};
	private PointF movingKnot;
	private PointF selectedKnot;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("GammaGraph", "onTouchEvent Start");
		Log.d("GammaGraph", "(" + event.getX() + "," + event.getY() + ")");
		
		Canvas canvas = getHolder().lockCanvas();
		try {
			float x = event.getX();
			float y = event.getY();
			if (this.gammaGraphInfo.baseRect.left < x && x < this.gammaGraphInfo.baseRect.right) {
				if (this.gammaGraphInfo.baseRect.top < y && y < this.gammaGraphInfo.baseRect.bottom) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						movingKnot = new PointF(x, y);
						isMoving = true;
						this.tmpKnots.clear();
						for(PointF knot : gammaGraphInfo.knots) {
							this.tmpKnots.add(knot);
						}
						selectedKnot = GammaGraphUtils.selectKnot(movingKnot, this.gammaGraphInfo.knots);
						if(selectedKnot == null) {
							Log.d("GammaGraphMovingTest", "new point selected");
							this.tmpKnots.add(this.movingKnot);
						} else {
							Log.d("GammaGraphMovingTest", "current point selected");
							if(selectedKnot.x == gammaGraphInfo.baseRect.left) {
								Log.d("GammaGraphMovingTest", "first");
								this.movingKnot.x = gammaGraphInfo.baseRect.left;
							} else if(selectedKnot.x == gammaGraphInfo.baseRect.right) {
								Log.d("GammaGraphMovingTest", "last");
								this.movingKnot.x = gammaGraphInfo.baseRect.right;
							}
							this.tmpKnots.remove(selectedKnot);
							this.tmpKnots.add(this.movingKnot);
						}
						invalidate();
						break;
					case MotionEvent.ACTION_MOVE:
						this.tmpKnots.remove(this.movingKnot);
						this.movingKnot = new PointF(x, y);
						this.tmpKnots.add(this.movingKnot);
						
						invalidate();
						break;
					case MotionEvent.ACTION_UP:
						isMoving = false;
						this.gammaGraphInfo.knots.add(new PointF(x, y));
						if(selectedKnot != null) {
							this.gammaGraphInfo.knots.remove(selectedKnot);
						}
						invalidate();
						break;
					}

				}
			}
		} finally {
			getHolder().unlockCanvasAndPost(canvas);
		}

		Log.d("GammaGraph", "onTouchEvent End");
		return true;
	}
}
