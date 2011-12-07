package com.android.powermon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.powermon.R;
import com.android.powermon.event.EventBroker;
import com.android.powermon.event.EventListener;
import com.android.powermon.event.StatusChangedEvent;
import com.android.powermon.util.AppStateBuilder;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


public class MainActivity extends RoboActivity implements EventListener<StatusChangedEvent> {
    private static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.statusText)
    public TextView statusTextView;

    @Inject
    public AppStateBuilder appStateBuilder;

    @Inject
    public EventBroker eventBroker;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Associate with the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        // Subscribe
        eventBroker.subscribe(StatusChangedEvent.class, this);
        Log.i(TAG, "MainView created !");
    }

    /**
     * This is called whenever the screen is brought to foreground.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Initialize status text from preferences
        statusTextView.setText(appStateBuilder.buildScreenState());
    }


    @Override
    public void onEvent(StatusChangedEvent event) {
        // Initialize status text from preferences
        statusTextView.setText(appStateBuilder.buildScreenState());
        Log.i(TAG, "Status in MainView updated !");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.itemPrefs:
                 startActivity(new Intent(this, PreferencesActivity.class));
                 break;
         }

        // True = menu event fully handeled
        return true;
    }
}
