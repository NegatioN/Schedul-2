package main.schedul.joakim.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.schedul2.R;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class ChainListAdapter extends ArrayAdapter<Chain>{

    private static final String EXPERIENCE = "Experience: ";
    private static final String HOURS = "Hours: ";

    private Context context;
    private ArrayList<Chain> chains;


    public ChainListAdapter(Context context, ArrayList<Chain> chains) {
        super(context, R.layout.row_layout, chains);
        this.context = context;
        this.chains = chains;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Chain selectedChain = chains.get(position);


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
        hours.setText(HOURS + selectedChain.getTotalHours() );




        return rowView;
    }

    public ArrayList<Chain> getChains(){
        return this.chains;
    }


}
