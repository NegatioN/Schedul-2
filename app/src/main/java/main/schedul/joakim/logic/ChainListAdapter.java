package main.schedul.joakim.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;
import main.schedul.joakim.schedul2.R;
import main.schedul.joakim.schedul2.Schedul;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class ChainListAdapter extends ArrayAdapter<Chain>{

    //TODO fix how each individual list-item looks like.
    //TODO let a chain be a "header-object" in list. Sub-goals should be smaller, and the ones we add time to. (also let add time to main?)

    private static final String EXPERIENCE = "XP: ";
    private static final String HOURS = "Timer: ";

    private Context context;
    private ArrayList<Chain> chains;
    private User user;

    public ChainListAdapter(Context context, List<Chain> chains, User user) {
        super(context, R.layout.row_layout, chains);
        this.context = context;

        this.chains = new ArrayList<Chain>();
        for(Chain chain : chains)
            this.chains.add(chain);
        this.user = user;

        Log.d("chainlist", "ChainListAdapter onCreate run");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Drawable drawable = context.getResources().getDrawable(R.drawable.schedul_row);

        final Chain selectedChain = chains.get(position);
        Log.d("Inflate.GetView", "Chainview at pos: " + position + "created");

        //get all views for the row in our list
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout,parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.tvName);
        TextView xp = (TextView) rowView.findViewById(R.id.tvTotalXp);
        TextView hours = (TextView) rowView.findViewById(R.id.tvTotalHours);


        //define information in the views
        name.setText(selectedChain.getName());
        xp.setText(EXPERIENCE + selectedChain.getCurrentExperience());
        hours.setText(HOURS + selectedChain.getTotalHours());

        //sets the color of the view based on how far it is from expiring
        //mutates the drawable so we can still share bitmap-resource, but color-states are not shared.
        drawable.setColorFilter(selectedChain.getDisplayColor(), PorterDuff.Mode.MULTIPLY);
        rowView.setBackground(drawable);
     //   rowView.setBackgroundColor(selectedChain.getDisplayColor());


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMinuteDialog(selectedChain, user);
            }
        });

        //lets a user edit the information of the selected chain.
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                displayEditDialog(selectedChain, user);
                return false;
            }
        });

        return rowView;
    }

    public ArrayList<Chain> getChains(){
        return this.chains;
    }


    //displays our dialog with minute/hour input for the current chain
    private void displayMinuteDialog(Chain selectedChain, User user){
        MinHourDialog.show((Schedul)context, selectedChain, user, this);

       // this.notifyDataSetChanged();

    }
    //displays a dialog that lets us edit our currently selected chain
    private void displayEditDialog(final Chain selectedChain, User user){

        //used for access in inner class
        final ChainListAdapter cla = this;


        //define the view-info in the alertdialog
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_chain_view, null);
        final EditText editName = (EditText) view.findViewById(R.id.et_editname);
        editName.setText(selectedChain.getName());

        final Spinner spinner = (Spinner) view.findViewById(R.id.daysEditSpinner);
        spinner.setSelection(selectedChain.getMustChainDays() - 1);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_reset);


        //define alertdialog options
       AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.edit_cain_title)).setView(view);
        dialog.setPositiveButton("Endre", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean etEmpty = isEmpty(editName);
                boolean mustChainDaysSame = spinner.getSelectedItemPosition() == (selectedChain.getMustChainDays() -1);
                if(!etEmpty || !mustChainDaysSame || checkBox.isChecked()) {

                    Chain chain = selectedChain;
                    if(!etEmpty)
                        chain.setName(editName.getText().toString());
                    if(!mustChainDaysSame)
                        chain.setMustChainDays(spinner.getSelectedItemPosition()+1);
                    if(checkBox.isChecked())
                        chain.resetToday();
                    DBHelper db = new DBHelper(context);
                    db.updateChain(chain);
                    cla.notifyDataSetChanged();
                }
            }
        });
        dialog.setNegativeButton("Tilbake", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
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
