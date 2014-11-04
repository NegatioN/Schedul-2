package main.schedul.joakim.schedul2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;
import main.schedul.joakim.logic.ChainListAdapter;


public class Schedul extends FragmentActivity {

    private ArrayList<Chain> chains;
    public static User CURRENTUSER;


    //TODO add user-stats and name in actionbar, or find a solution for placement
    //TODO add save variables for screen tilt alertdialog.
    //TODO create database for chains and users
    //TODO Create your user on first start. If user in db, only create in settings if(want)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedul);

        chains = new ArrayList<Chain>();


        CURRENTUSER = new User("Joakim Rishaug");
        testData(chains, CURRENTUSER);

        ListView lvChains = (ListView) findViewById(R.id.lvChains);
        lvChains.setAdapter(new ChainListAdapter(this,chains, CURRENTUSER));


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

    private void testData(ArrayList<Chain> chains, User user) {
        Chain testchain = new Chain("Fotball", 2, "Masse bull", 1);
        chains.add(testchain);
        testchain.addChainToUser(user);
    }

}
