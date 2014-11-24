package main.schedul.joakim.logic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import eu.inmite.android.lib.dialogs.BaseDialogFragment;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import main.schedul.joakim.Databases.DBHelper;
import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;
import main.schedul.joakim.schedul2.R;

/**
 * Created by NegatioN on 25.09.2014.
 */
public class MinHourDialog extends SimpleDialogFragment{

    public static String TAG = "minHour";
    private static Chain chain;
    private static User staticuser;
    private static FragmentActivity fa;
    private static ChainListAdapter cla;

    public static void show(FragmentActivity activity, Chain selectedChain, User user, ChainListAdapter adapter){
        fa = activity;
        staticuser = user;
        chain = selectedChain;
        new MinHourDialog().show(activity.getSupportFragmentManager(), TAG);
        cla = adapter;
    }



    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        builder.setTitle(R.string.minhour_title);
        View view = (LayoutInflater.from(getActivity()).inflate(R.layout.min_hour_frag_layout, null));


        //NumberPickers get defined
        final NumberPicker hours = (NumberPicker)view.findViewById(R.id.npHour);
        final NumberPicker minutes = (NumberPicker) view.findViewById(R.id.npMinute);

        setupTimePicker(hours,minutes,view);

        builder.setView(view);

        final MinHourDialog dialog = this;

        //finish input button
        builder.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISimpleDialogListener listener = getDialogListener();
                if (listener != null) {
                    listener.onPositiveButtonClicked(0);
                }
                int hoursSelected = hours.getValue();
                int minutesSelected = minutes.getValue();


                //not both counters at zero
                if((hoursSelected + minutesSelected) > 0) {
                    int totalMins = 60 * hoursSelected + minutesSelected;

                    if(!chain.getExperience().daySpent(staticuser.getUserChains(), totalMins)){
                        chain.doTask(totalMins, staticuser);
                        updateUserText(staticuser, fa);
                    }else{

                        dismiss();
                        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.time_error)).setMessage(getResources().getString(R.string.time_warning)).setNeutralButton("Tilbake", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismiss();
                            }
                        }).show();
                    }
                }


                Log.d("chain.onclick", hoursSelected + " : " + minutesSelected);

                DBHelper db = new DBHelper(getActivity().getApplicationContext());
                db.updateChain(chain);

                //must be called here to make views update correctly.
                cla.notifyDataSetChanged();
                dismiss();

            }
        });

        //exit button added
        builder.setNegativeButton("Cancel", new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return builder;
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

    private void updateUserText(User user, Context context){
        TextView tvXp = (TextView) ((Activity)context).findViewById(R.id.tv_userXP);
        TextView tvLvl = (TextView)((Activity)context). findViewById(R.id.tv_userLvl);

        Log.d("ChainAdapter", "Username: " + user.getName());

        tvXp.setText("XP: " + user.getLevel().getLevelXp());
        tvLvl.setText("Lvl: " + user.getLevel().getLevel());
    }
}
