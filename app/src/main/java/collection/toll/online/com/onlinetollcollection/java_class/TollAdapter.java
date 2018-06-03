package collection.toll.online.com.onlinetollcollection.java_class;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import collection.toll.online.com.onlinetollcollection.NotificationActivity;
import collection.toll.online.com.onlinetollcollection.QRcodeActivity;
import collection.toll.online.com.onlinetollcollection.R;
import collection.toll.online.com.onlinetollcollection.database.Toll;

/**
 * Created by opulent on 13/1/17.
 */



public class TollAdapter extends RecyclerView.Adapter<TollAdapter.MyViewHolder> {

    private List<Toll> TollList;
    Context c;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tollName, status, amount,date;
RelativeLayout r;
        public MyViewHolder(View view) {
            super(view);
            tollName = (TextView) view.findViewById(R.id.tollName);
            status = (TextView) view.findViewById(R.id.status);
            amount = (TextView) view.findViewById(R.id.amount);
            amount = (TextView) view.findViewById(R.id.amount);
            date = (TextView) view.findViewById(R.id.date);
            r= (RelativeLayout) view.findViewById(R.id.r);
        }
    }


    public TollAdapter(List<Toll> TollList, Context c) {
        this.TollList = TollList;
        this.c=c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_toll, parent, false);

itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        final Toll toll = TollList.get(position);
        if(toll.getStatus().equals("0")){
            holder.status.setText("Single");

        }else{
            holder.status.setText("Return");
        }
        holder.tollName.setText(toll.getName());
        holder.amount.setText(toll.getAmount());
        holder.date.setText(toll.getDate());
holder.r.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        JSONObject j=new JSONObject();
        try {
            j.put("id",toll.getId());
            j.put("status",toll.getStatus());
            j.put("amount",toll.getAmount());
            j.put("tollId",toll.getTollId());
            j.put("tollName",toll.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent i=new Intent(c.getApplicationContext(),QRcodeActivity.class);
        i.putExtra("j",j.toString());
        c.startActivity(i);
        }
});
    }

    @Override
    public int getItemCount() {
        return TollList.size();
    }
}