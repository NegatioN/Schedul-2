package main.schedul.joakim.schedul2;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import main.schedul.joakim.information.Chain;


public class CreateChain extends FragmentActivity {

    //TODO create nicer layout for activity
    //TODO add created chains to main activity. Add to database, and call updateDB on return?
    private Spinner spinner;
    private EditText editText;
    private Button createButton;
    private TypedArray spinnerValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chain);

        //define views
        spinner = (Spinner) findViewById(R.id.daysSpinner);
        editText = (EditText) findViewById(R.id.etName);
        createButton = (Button) findViewById(R.id.btnCreate);

        spinnerValues = getResources().obtainTypedArray(R.array.day_numbers_values);
        final CreateChain context = this;

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets user input and creates a new chain
                int days = spinnerValues.getIndex(spinner.getSelectedItemPosition());
                String name = editText.getText().toString();

                //has user entered a name yet?
                if(!isEmpty(editText)) {
                    createChain(name, name, 2, days);

                    //after creating chain, go back to main screen.
                    finish();
                }else{
                    SimpleDialogFragment.createBuilder(context, getSupportFragmentManager()).setMessage(R.string.create_error_message).show();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_chain, menu);
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
        return super.onOptionsItemSelected(item);
    }

    private void createChain(String name, String description, int priority, int mustChainDays){
        Chain newChain = new Chain(name, priority, description, mustChainDays);
        Schedul.CURRENTUSER.getUserChains().add(newChain);
    }

    //checks if our edittext is empty
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

}
