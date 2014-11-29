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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.Chain;


public class CreateChain extends FragmentActivity {

    //TODO change color of line under EditText or under Spinner. change width of spinner

    private Spinner spinner;
    private EditText editText;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chain);

        //define views
        spinner = (Spinner) findViewById(R.id.daysSpinner);

        final int textcolor = getResources().getColor(R.color.darkgrayText);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(textcolor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        editText = (EditText) findViewById(R.id.etName);


        createButton = (Button) findViewById(R.id.btnCreate);

        final CreateChain context = this;

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gets user input and creates a new chain
                int days = spinner.getSelectedItemPosition() + 1;
                Log.d("CreateChain", "How often to chain: " + days + " days.");
                String name = editText.getText().toString();

                boolean emptyText = isEmpty(editText);
                boolean tooLongText = isTooLong(editText);
                //has user entered a name yet?
                if(!emptyText && !tooLongText) {
                    createChain(name, name, 2, days);

                    //after creating chain, go back to main screen.
                    finish();
                }else{
                    //SimpleDialogFragment.createBuilder(context, getSupportFragmentManager()).setMessage(R.string.create_error_message).show();
                    if(emptyText) {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setMessage("Vennligst skriv inn et navn for lenken din.");
                        dialogBuilder.setNeutralButton("Lukk", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }
                    else{
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                        dialogBuilder.setMessage("Vennligst bruk et kortere navn på lenken");
                        dialogBuilder.setNeutralButton("Lukk", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }
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
            Intent i = new Intent(this, SettingsLoadActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createChain(String name, String description, int priority, int mustChainDays){
        Chain newChain = new Chain(name, priority, description, mustChainDays);
        Schedul.CURRENTUSER.getUserChains().add(newChain);

        DBHelper db = new DBHelper(this.getApplicationContext());
        Log.d("createChain", "db started. On user: " + Schedul.CURRENTUSER.getId() );
        //add chain to db with user-id
        db.addChain(Schedul.CURRENTUSER.getId(), newChain);
    }

    //checks if our edittext is empty
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    //checks if string is too long to fit well on screen
    private boolean isTooLong(EditText etText){
        if (etText.getText().toString().trim().length() > 18) {
            return true;
        }else{
            return false;
        }
    }




}

