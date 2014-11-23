package main.schedul.joakim.schedul2;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by NegatioN on 23.11.2014.
 */
public class SettingsLoadActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsActivity()).commit();
    }
}
