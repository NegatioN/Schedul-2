package main.schedul.joakim.schedul2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.User;

/**
 * Created by NegatioN on 23.11.2014.
 * Class mainly used to create our select users preference.
 */
public class SettingsActivity extends PreferenceFragment {
    public static final String LIST_PREF_KEY = "USER_LIST_PREF";

//TODO fix layout of settings

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.settings_layout);

        Preference button = (Preference)findPreference("button");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                showUserCreateFragment();
                return true;
            }
        });
    }
    //dynamically sets the list of users in the database to be selected by clicking the preference-list.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListPreference list = new ListPreference(getActivity());
        list.setKey(LIST_PREF_KEY);


        DBHelper db = new DBHelper(getActivity());
        List<User> users = db.getUsers();

        //don't add a list of selectable users if the database is empty. should never trigger.
        if(users.isEmpty())
            return;

        final ArrayList<String> entries = new ArrayList<String>();
        final ArrayList<String> entryValues = new ArrayList<String>();



        //add user name and db PK for lookup in onCreate of Schedul.class
        for(User user : users){
            entryValues.add(String.valueOf(user.getId()));
            entries.add(user.getName());
            Log.d("Settings.makeUserList", "Added user: " + user.getName());
        }

        //get preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userpref = preferences.getString(LIST_PREF_KEY, null);

        if(userpref != null){
            list.setTitle(R.string.list_pref_choose_user);
            //sets header of preference to display current chosen name.
            String selectedUserString = getString(R.string.selected_user_summary);
            //itereates over entryvalues that are unique and finds the selected user-name
            String name = entries.get(entryValues.indexOf(userpref));
            list.setSummary(selectedUserString + name);
        }else{
            //if no user selected, set default text.
            list.setTitle(R.string.list_pref_choose_user);
        }

        CharSequence[] entrySeq = entries.toArray(new CharSequence[entries.size()]);
        CharSequence[] entryValSeq = entryValues.toArray(new CharSequence[entryValues.size()]);

        //set both lists of PKs and values to the listpreference, default value 0.
        list.setEntries(entrySeq);
        list.setEntryValues(entryValSeq);

        final PreferenceFragment frag = this;

        this.getPreferenceScreen().addPreference(list);

        //add a back-button preference.
        Preference backButton = new Preference(getActivity());
        backButton.setSummary(getResources().getString(R.string.go_back));
        backButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().finish();
                return false;
            }
        });
        this.getPreferenceScreen().addPreference(backButton);

        if(Schedul.CURRENTUSER != null)
            db.updateUser(Schedul.CURRENTUSER);
    }

    private void showUserCreateFragment(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Lag en ny bruker");
        alert.setMessage("Hva heter du?");

// Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                DBHelper db = new DBHelper(getActivity());
                String value = input.getText().toString();
                User user = new User(value);
                int id = db.addUser(user);
                if(Schedul.CURRENTUSER != null)
                    db.updateUser(Schedul.CURRENTUSER);

                //save newly created user as current preference.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                prefs.edit().putString(SettingsActivity.LIST_PREF_KEY, String.valueOf(id)).commit();
                getActivity().finish();

            }
        });

        alert.setNegativeButton("Avslutt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
