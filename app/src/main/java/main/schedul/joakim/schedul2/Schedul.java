package main.schedul.joakim.schedul2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.User;
import main.schedul.joakim.logic.ChainListAdapter;


public class Schedul extends FragmentActivity {

    public static User CURRENTUSER;
    private DBHelper db = new DBHelper(this);
    public static final String CURRENT_DAY_PREF = "CURRENT_DAY";


    //TODO add all strings to strings.xml
    //TODO add additional settings?

    //called on every screen-update
    @Override
    protected void onResume() {
        super.onResume();

        //only useful for the first start of app
        if(CURRENTUSER == null){
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int dbPrimaryKey = Integer.parseInt(preferences.getString(SettingsActivity.LIST_PREF_KEY, "-1"));


        if(CURRENTUSER.getId() != dbPrimaryKey ){
            CURRENTUSER = db.getEntireUser(dbPrimaryKey);
            Log.d("Schedul.init", "Name: " + CURRENTUSER.getName());

            updateUserText();

        }
        if(CURRENTUSER != null) {

            //has it been more than one day since this user connected to the app?
            String compareDay = preferences.getString(CURRENT_DAY_PREF+CURRENTUSER.getId(), "0000.00.00");

            if(isNewDay(compareDay, preferences)) {
                Log.d("Schedul.isNewday", "Newday == true");
                CURRENTUSER.setNewday(this);
            }

            ListView lvChains = (ListView) findViewById(R.id.lvChains);
            Log.d("Schdul.onResume", "Right before chainAdapter oncreate onpause");
            lvChains.setAdapter(new ChainListAdapter(this, CURRENTUSER.getUserChains(), CURRENTUSER));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedul);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int dbPrimaryKey = Integer.parseInt(preferences.getString(SettingsActivity.LIST_PREF_KEY, "-1"));

        //if we didnt find a selected user-preference, create a new one.
        if (dbPrimaryKey == -1) {
            showUserCreateFragment();
        } else {
            CURRENTUSER = db.getEntireUser(dbPrimaryKey);
            Log.d("Schedul.init", "Name: " + CURRENTUSER.getName());
            updateUserText();
        }


        //TODO add achievements from user



    }

    //saves our user-data every time the application gets interrupted.
    @Override
    protected void onPause() {
        super.onPause();
        if(CURRENTUSER != null)
            db.updateUser(CURRENTUSER);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedul, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsLoadActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_newchain){
            Intent i = new Intent(this, CreateChain.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    private void showUserCreateFragment(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Ingen brukere finnes");
        alert.setMessage("Hva heter du?");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                CURRENTUSER = new User(value);
                int id = db.addUser(CURRENTUSER);
                CURRENTUSER = db.getEntireUser(id);
                updateUserText();
                /*
                //creates the listview after user-creation
                ListView lvChains = (ListView) findViewById(R.id.lvChains);
                lvChains.setAdapter(new ChainListAdapter(getApplicationContext(), CURRENTUSER.getUserChains(), CURRENTUSER));
*/
                //save newly created user as current preference.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString(SettingsActivity.LIST_PREF_KEY, String.valueOf(id)).commit();


            }
        });

        alert.setNegativeButton("Avslutt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                finish();
            }
        });

        alert.show();
    }


    //updates the three textfields that relate to username, xp and level
    private void updateUserText(){
        TextView tvUsername = (TextView) findViewById(R.id.tv_username);
        TextView tvXp = (TextView) findViewById(R.id.tv_userXP);
        TextView tvLvl = (TextView) findViewById(R.id.tv_userLvl);

        tvUsername.setText(CURRENTUSER.getName());
        tvXp.setText("XP: " + CURRENTUSER.getLevel().getLevelXp());
        tvLvl.setText("Lvl: " + CURRENTUSER.getLevel().getLevel());

    }

    //method to refresh "current day in prefs" if more than one day has passed since user logged on/refreshed screen.
    private boolean isNewDay(String compareday, SharedPreferences preferences){
        Time t = new Time();
        t.setToNow();

        int compareYear = Integer.parseInt(compareday.substring(0,4));
        int compareMonth = Integer.parseInt(compareday.substring(5,7));
        int compareDay = Integer.parseInt(compareday.substring(8,10));

        Log.d("Schedul.isNewDay", "Today: " + t.year + "." + t.month + "." + t.monthDay + "\nPreference-date: " + compareYear + "." + compareMonth + "." + compareDay);

        if(compareYear < t.year){
            updatePreferenceDate(t, preferences);
            return true;
        }else if( compareYear == t.year){
            if(compareMonth < t.month){
                updatePreferenceDate(t, preferences);
                return true;
            }else if(compareMonth == t.month){
                if(compareDay < t.monthDay) {
                    updatePreferenceDate(t, preferences);
                    return true;
                }
            }
        }

        return false;
    }

    //updates the preferences. Takes into consideration single-digit months and days.
    private void updatePreferenceDate(Time time, SharedPreferences preferences){
        String monthString = String.valueOf(time.month);
        if(monthString.length() == 1)
            monthString = "0"+monthString;
        String dayString = String.valueOf(time.monthDay);
        if(dayString.length() == 1)
            dayString = "0"+dayString;
        preferences.edit().putString(CURRENT_DAY_PREF+CURRENTUSER.getId(), time.year + "." + monthString + "." + dayString).commit();
    }

}
