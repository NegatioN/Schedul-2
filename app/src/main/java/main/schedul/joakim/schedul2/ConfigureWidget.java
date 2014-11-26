package main.schedul.joakim.schedul2;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;

/**
 * Created by NegatioN on 26.11.2014.
 */
public class ConfigureWidget extends Activity {

    private ConfigureWidget config;
    private int widgetID;
    private int chainID = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_widget_layout);
        setResult(RESULT_CANCELED);
        config = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(config);

        RemoteViews views = new RemoteViews(config.getPackageName(),R.layout.widget_layout);
        //config spinner for choosing the user to get a chain from.
        final Spinner spinner = (Spinner) findViewById(R.id.spinner_users);

        final DBHelper db = new DBHelper(getApplicationContext());
        List<User> users = db.getUsers();

        final ArrayList<String> entries = new ArrayList<String>();
        final ArrayList<Integer> entryValues = new ArrayList<Integer>();

        //add user name and db PK for lookup in onCreate of Schedul.class
        for(User user : users){
            entryValues.add(user.getId());
            entries.add(user.getName());
            Log.d("Settings.makeUserList", "Added user: " + user.getName());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, entries); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        //define spinner for display of chains for selected user.
        final Spinner chainSpinner = (Spinner) findViewById(R.id.spinner_chains);

        final ArrayList<String> entriesChains = new ArrayList<String>();
        final ArrayList<Integer> entryValuesChains = new ArrayList<Integer>();

        Button getUserButton = (Button) findViewById(R.id.b_getuser);
        getUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the arraylists already are populated. Clear them
                entriesChains.clear();
                entryValuesChains.clear();
                //get selected userId and querys db.
                int selectedUserId = entryValues.get(spinner.getSelectedItemPosition());

                List<Chain> chains = db.getChains(selectedUserId);
                for(Chain chain : chains){
                    entryValuesChains.add(chain.getId());
                    entriesChains.add(chain.getName());
                    Log.d("Settings.makeUserList", "Added user: " + chain.getName());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(config, android.R.layout.simple_spinner_item, entriesChains); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                chainSpinner.setAdapter(spinnerArrayAdapter);

            }
        });


        Button createButton = (Button) findViewById(R.id.b_createwidget);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets the selected spinner-value from our chain-spinner, and find the db primary key for this item.
                chainID = entryValuesChains.get(chainSpinner.getSelectedItemPosition());

                if(chainID != -1) {

                    //TODO send chain-id as extra, update view with info from chain

                    //end this onclick
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });

    }
}
