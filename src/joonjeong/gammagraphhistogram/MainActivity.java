package joonjeong.gammagraphhistogram;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	private GammaGraphView gammaGraphView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("GammaGraph", "Activity Created");
		GammaGraphInfo gammaGraphInfo = new GammaGraphInfo(new int[320 * 120]);
		this.gammaGraphView = new GammaGraphView(this, gammaGraphInfo);
		this.setContentView(this.gammaGraphView);
	}
}
