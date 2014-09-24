package main.schedul.joakim.schedul2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.logic.ChainListAdapter;


public class Schedul extends FragmentActivity {

    private ArrayList<Chain> chains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedul);

        chains = new ArrayList<Chain>();

        testData(chains);

        ListView lvChains = (ListView) findViewById(R.id.lvChains);
        lvChains.setAdapter(new ChainListAdapter(this,chains));


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

    private void testData(ArrayList<Chain> chains) {
        Chain testchain = new Chain("Fotball", 2, "Masse bull");
        chains.add(testchain);
    }

}
