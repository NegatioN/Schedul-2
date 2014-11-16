package main.schedul.joakim.logic;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

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
    private static ChainListAdapter cListAdapter;

    public static void show(FragmentActivity activity, Chain selectedChain, User user, ChainListAdapter cla){
        staticuser = user;
        chain = selectedChain;
        new MinHourDialog().show(activity.getSupportFragmentManager(), TAG);
        cListAdapter = cla;

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
                    chain.doTask((60 * hoursSelected + minutesSelected), staticuser);
                    cListAdapter.notifyDataSetChanged();
                }

                Log.d("chain.onclick", hoursSelected + " : " + minutesSelected);
                Log.d("chain.onclick", chain.getCurrentExperience()+"");

                Log.d("user.chain.onclick", staticuser.getUserChains().get(0).getCurrentExperience()+"");
                DBHelper db = new DBHelper(getActivity().getApplicationContext());
                db.updateChain(chain);
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
}
