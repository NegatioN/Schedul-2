package main.schedul.joakim.logic;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

import eu.inmite.android.lib.dialogs.BaseDialogFragment;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import main.schedul.joakim.schedul2.R;

/**
 * Created by NegatioN on 25.09.2014.
 */
public class MinHourDialog extends SimpleDialogFragment{

    public static String TAG = "minHour";

    public static void show(FragmentActivity activity){
        new MinHourDialog().show(activity.getSupportFragmentManager(), TAG);
    }

    @Override
    public BaseDialogFragment.Builder build(BaseDialogFragment.Builder builder) {
        builder.setTitle(R.string.minhour_title);
        builder.setView(LayoutInflater.from(getActivity()).inflate(R.layout.min_hour_frag_layout, null));
        builder.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISimpleDialogListener listener = getDialogListener();
                if (listener != null) {
                    listener.onPositiveButtonClicked(0);
                }
                dismiss();
            }
        });
        return builder;
    }
}
