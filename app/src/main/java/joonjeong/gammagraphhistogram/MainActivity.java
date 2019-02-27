package joonjeong.gammagraphhistogram;

import android.os.*;
import android.util.*;

import androidx.appcompat.app.*;

public class MainActivity extends AppCompatActivity {
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
