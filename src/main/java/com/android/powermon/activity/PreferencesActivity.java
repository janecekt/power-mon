package com.android.powermon.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.android.powermon.R;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
