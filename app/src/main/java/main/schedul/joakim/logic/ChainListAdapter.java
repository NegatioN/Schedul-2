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
    private ViewGroup parentList;

    public ChainListAdapter(Context context, ArrayList<Chain> chains, User user) {
        super(context, R.layout.row_layout, chains);
        this.context = context;
        this.chains = chains;
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        parentList = parent;
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


        //TODO make onclicklistener that lets us open menu with input minutes, and makes unselectable until new day starts. interacts with chain-logic


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

    private void displayMinuteDialog(Chain selectedChain, User user){
        MinHourDialog.show((Schedul)context, selectedChain, user, this);
        this.notifyDataSetChanged();

    }


}
