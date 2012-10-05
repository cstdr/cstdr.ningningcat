package cstdr.ningningcat;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MoreActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.more);
	}
}
