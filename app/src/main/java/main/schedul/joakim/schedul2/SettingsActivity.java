package main.schedul.joakim.schedul2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.settings_layout);
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
            //sets header of preference to display current chosen name.
            String selectedUserString = getString(R.string.selected_user_summary);
            //itereates over entryvalues that are unique and finds the selected user-name
            String name = entries.get(entryValues.indexOf(userpref));
            list.setSummary(selectedUserString + name);
        }else{
            //if no user selected, set default text.
            list.setSummary(R.string.list_pref_choose_user);
        }

        CharSequence[] entrySeq = entries.toArray(new CharSequence[entries.size()]);
        CharSequence[] entryValSeq = entryValues.toArray(new CharSequence[entryValues.size()]);

        //set both lists of PKs and values to the listpreference, default value 0.
        list.setEntries(entrySeq);
        list.setEntryValues(entryValSeq);



        this.getPreferenceScreen().addPreference(list);
    }
}
