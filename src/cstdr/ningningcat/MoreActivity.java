package cstdr.ningningcat;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * TODO settings next version
 * @author cstdingran@gmail.com
 */
public class MoreActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.more);
    }
}
