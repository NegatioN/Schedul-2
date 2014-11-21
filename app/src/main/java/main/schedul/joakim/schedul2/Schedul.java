package main.schedul.joakim.schedul2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.User;
import main.schedul.joakim.logic.ChainListAdapter;


public class Schedul extends FragmentActivity {

    public static User CURRENTUSER;
    private DBHelper db = new DBHelper(this);
    private boolean firstStart = true;
    private static final String FIRST_START = "_START_KEY";


    //TODO add user-stats and name in actionbar, or find a solution for placement
    //TODO add save variables for screen tilt alertdialog.
    //TODO Create your user on first start. If user in db, only create in settings if(want)
    //TODO make settings
    //TODO Create widgets for chains.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedul);


        if (savedInstanceState != null) {
            Log.d("onCreate", "First Start: " + savedInstanceState.getBoolean(FIRST_START));
        }else{


            if (db.getUsers().isEmpty()) {
                showUserCreateFragment();
                firstStart = false;
            } else {
                //TODO make user selectable via settings
                CURRENTUSER = db.getEntireUser(db.getLastInsertedUserId());
                Log.d("Schedul.init", "Name: " + CURRENTUSER.getName());
                updateUserText();
            }
            firstStart = false;
        }

        //TODO add achievements from user



        ListView lvChains = (ListView) findViewById(R.id.lvChains);
        // needs to be here for the first user-creation. Otherwise we'll get an error
        if(CURRENTUSER != null) {
            lvChains.setAdapter(new ChainListAdapter(this, CURRENTUSER.getUserChains(), CURRENTUSER));
        }

        Button newChainButton = (Button) findViewById(R.id.b_newChain);
        newChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CreateChain.class);
                startActivity(i);
            }
        });



    }

    //saves our user-data every time the application gets interrupted.
    @Override
    protected void onPause() {
        super.onPause();
        db.updateUser(CURRENTUSER);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIRST_START, firstStart);
        Log.d("onSave", "screen saved");
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
                db.addUser(CURRENTUSER);
                updateUserText();
                //creates the listview after user-creation
                ListView lvChains = (ListView) findViewById(R.id.lvChains);
                lvChains.setAdapter(new ChainListAdapter(getApplicationContext(), CURRENTUSER.getUserChains(), CURRENTUSER));
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
        tvXp.setText(CURRENTUSER.getLevel().getLevelXp() + "");
        tvLvl.setText(CURRENTUSER.getLevel().getLevel() + "");
    }

}
