package main.schedul.joakim.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
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
        Resources resources = context.getResources();
        final Drawable drawable =resources.getDrawable(R.drawable.alt_row_drawable);
        Drawable topDrawable = resources.getDrawable(R.drawable.alt_row_drawable);



        final Chain selectedChain = chains.get(position);
        Log.d("Inflate.GetView", "Chainview at pos: " + position + "created");

        //get all views for the row in our list
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.row_layout,parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.tvName);
        TextView xp = (TextView) rowView.findViewById(R.id.tvTotalXp);
        TextView hours = (TextView) rowView.findViewById(R.id.tvTotalHours);

        //gets the color of the chain in regards to how long it is from timing out it's combo.
        int progressColor = selectedChain.getDisplayColor();

        //define information in the views
        name.setText(selectedChain.getName());
        name.setTextColor(progressColor);
        xp.setText(EXPERIENCE + selectedChain.getCurrentExperience());
        hours.setText(HOURS + selectedChain.getTotalHours());

        //sets the color of the view based on how far it is from expiring
        //mutates the drawable so we can still share bitmap-resource, but color-states are not shared.
        drawable.setColorFilter(progressColor, PorterDuff.Mode.MULTIPLY);

        //define layerdrawable
        Drawable[] drawables = {drawable,topDrawable};
        final LayerDrawable layerDrawable = new LayerDrawable(drawables);


        //set insets for the drawables
        //index, left, top, right, bottom pixel inserts. Creates the illusion of shadow in the progressColor on our row.
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, 0, 0, 0, 4);



        rowView.setBackground(layerDrawable);
     //   rowView.setBackgroundColor(selectedChain.getDisplayColor());

        final Drawable onPressedDrawable = resources.getDrawable(R.drawable.alt_row_drawable_pressed);

        rowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.setBackground(onPressedDrawable);
                return false;
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMinuteDialog(selectedChain, view, layerDrawable);
            }
        });

        //lets a user edit the information of the selected chain.
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                displayEditDialog(selectedChain, view, layerDrawable);
                view.setBackground(layerDrawable);
                return true;
            }
        });

        return rowView;
    }

    public ArrayList<Chain> getChains(){
        return this.chains;
    }


    //displays our dialog with minute/hour input for the current chain
    private void displayMinuteDialog(Chain selectedChain, View view, LayerDrawable layerDrawable){
        showTimePicker(selectedChain, view, layerDrawable);

       // this.notifyDataSetChanged();

    }
    //displays a dialog that lets us edit our currently selected chain
    private void displayEditDialog(final Chain selectedChain, final View parentView, final LayerDrawable layerDrawable){

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
                    updateUserText(Schedul.CURRENTUSER, context);
                    parentView.setBackground(layerDrawable);
                }
            }
        });
        dialog.setNegativeButton("Tilbake", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                parentView.setBackground(layerDrawable);
            }

        });
        dialog.show();

        parentView.setBackground(layerDrawable);
    }


    private void showTimePicker(final Chain chain, final View parentView, final LayerDrawable layerDrawable){
        final ChainListAdapter cla = this;

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.min_hour_frag_layout, null);

        //NumberPickers get defined
        final NumberPicker hours = (NumberPicker)view.findViewById(R.id.npHour);
        final NumberPicker minutes = (NumberPicker) view.findViewById(R.id.npMinute);

        setupTimePicker(hours,minutes,view);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle("Legg til tid brukt på lenken").setView(view);

        dialog.setPositiveButton("Legg til", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int hoursSelected = hours.getValue();
                int minutesSelected = minutes.getValue();

                //not both counters at zero
                if((hoursSelected + minutesSelected) > 0) {
                    int totalMins = 60 * hoursSelected + minutesSelected;

                    if(!chain.getExperience().daySpent(Schedul.CURRENTUSER.getUserChains(), totalMins)){
                        chain.doTask(totalMins, Schedul.CURRENTUSER, context);
                        updateUserText(Schedul.CURRENTUSER, context);
                    }else{

                        dialogInterface.dismiss();
                        new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.time_error)).setMessage(context.getResources().getString(R.string.time_warning)).setNeutralButton("Tilbake", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        }).show();
                    }
                }


                Log.d("chain.onclick", hoursSelected + " : " + minutesSelected);

                DBHelper db = new DBHelper(context.getApplicationContext());
                db.updateChain(chain);

                //must be called here to make views update correctly.
                cla.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        //exit button added
        dialog.setNegativeButton("Tilbake", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();

        parentView.setBackground(layerDrawable);
    }


    //checks if our edittext is empty
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
    private void updateUserText(User user, Context context){
        TextView tvXp = (TextView) ((Activity)context).findViewById(R.id.tv_userXP);
        TextView tvLvl = (TextView)((Activity)context). findViewById(R.id.tv_userLvl);

        Log.d("ChainAdapter", "Username: " + user.getName());

        tvXp.setText("XP: " + user.getLevel().getLevelXp());
        tvLvl.setText("Lvl: " + user.getLevel().getLevel());
    }

    //defines our custom time-picker
    private void setupTimePicker(NumberPicker hp, NumberPicker mp, View inflatedView){
        hp = (NumberPicker) inflatedView.findViewById(R.id.npHour);
        mp = (NumberPicker) inflatedView.findViewById(R.id.npMinute);


        //TODO define color of numberpicker numbers-text
        hp.setMinValue(0);
        hp.setMaxValue(23);
        mp.setMinValue(0);
        mp.setMaxValue(59);

        hp.setFocusable(true);
        hp.setFocusableInTouchMode(true);
        mp.setFocusable(true);
        mp.setFocusableInTouchMode(true);
    }


}
