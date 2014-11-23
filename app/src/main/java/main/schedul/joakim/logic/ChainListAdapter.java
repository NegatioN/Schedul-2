package main.schedul.joakim.logic;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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

    private static final String EXPERIENCE = "Experience: ";
    private static final String HOURS = "Hours: ";

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

        Drawable drawable = context.getResources().getDrawable(R.drawable.row_drawable);

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
                displayEditDialog();
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
    //TODO implement onTouch /hold listener for reset of previously entered info today, or name-change.
    //displays a dialog that lets us edit our currently selected chain
    private void displayEditDialog(){

        //this.notifyDataSetChanged();
    }




}
