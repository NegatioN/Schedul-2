package main.schedul.joakim.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.information.User;
import main.schedul.joakim.schedul2.R;
import main.schedul.joakim.schedul2.Schedul;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class ChainListAdapter extends ArrayAdapter<Chain>{

    private static final String EXPERIENCE = "Experience: ";
    private static final String HOURS = "Hours: ";

    private Context context;
    private ArrayList<Chain> chains;
    private User user;

    public ChainListAdapter(Context context, ArrayList<Chain> chains, User user) {
        super(context, R.layout.row_layout, chains);
        this.context = context;
        this.chains = chains;
        this.user = user;
    }

    //TODO make chains get created with correct color depending on how long it's been since they were used/updated/chained

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chain selectedChain = chains.get(position);


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


        //TODO make listitem color change if getting close to not being chained. or not chained.

        //TODO implement onTouch /hold listener for reset of previously entered info today, or name-change.


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMinuteDialog(selectedChain, user);
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
        this.notifyDataSetChanged();

    }


}
