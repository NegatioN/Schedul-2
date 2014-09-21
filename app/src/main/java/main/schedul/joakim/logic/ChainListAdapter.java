package main.schedul.joakim.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import main.schedul.joakim.information.Chain;
import main.schedul.joakim.schedul2.R;

/**
 * Created by NegatioN on 22.09.2014.
 */
public class ChainListAdapter extends ArrayAdapter<Chain>{

    private Context context;


    public ChainListAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    //TODO finish getview method. Get object(position), fill in info to textviews in rowlayout
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout,parent, false);



        return super.getView(position, convertView, parent);
    }
}
