package com.example.narendra.lpd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Narendra on 03-02-2018.
 */

public class history_adpater extends RecyclerView.Adapter<history_adpater.requestViewHolder>{

    Context context;
    List<request_data> request_datas;

    private static int currentPosition = 0;
    private static int state=0;

    public history_adpater(Context context, List<request_data> request_datas) {
        this.context = context;
        this.request_datas = request_datas;
    }

    @Override
    public requestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.history_view,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
        return new requestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(requestViewHolder holder, final int position) {
        final request_data request_data=request_datas.get(position);

        holder.laymid.setVisibility(View.GONE);
        holder.ex.setImageResource(R.drawable.ex_more);

        holder.mcat.setText(request_data.getMaincat());
        holder.scat.setText(request_data.getSubcat());
        Float am=request_data.getAm();
        if(am==-1){
            holder.payed.setText("null");
        }else {
            holder.payed.setText(am.toString());
        }
        holder.source.setText(request_data.getSrcaddr());
        holder.destination.setText(request_data.getDesaddr());
        holder.date.setText(request_data.getDate());
        holder.time.setText(request_data.getTime());
        holder.cont.setText(request_data.getCont());
        holder.state.setText(request_data.getStatus());
        if(request_data.getStatus().equals("Canceled")){
            holder.cost.setText("null");
        }else{
            holder.cost.setText(String.valueOf(request_data.getCost()));
        }
        holder.sender.setText("From: "+request_data.getSender());
        holder.receiver.setText("To: "+request_data.getReceiver());
        holder.btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putInt("reqid",request_data.getReqid());
                Fragment fm=new Review();
                fm.setArguments(b);
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                android.support.v4.app.FragmentManager mn = activity.getSupportFragmentManager();
                mn.beginTransaction().replace(R.id.include,fm,"frag_Review").addToBackStack(null).commit();
            }
        });

        if (currentPosition == position && state==1) {
            //creating an animation
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

            //toggling visibility
            holder.laymid.setVisibility(View.VISIBLE);

            //adding sliding effect
            holder.laymid.startAnimation(slideDown);
            holder.ex.setImageResource(R.drawable.ex_less);
        }

        holder.layfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state==0) {
                    state=1;
                    //getting the position of the item to expand it
                    currentPosition = position;

                    //reloding the list
                    notifyDataSetChanged();
                }else {
                    state=0;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return request_datas.size();
    }

    class requestViewHolder extends RecyclerView.ViewHolder {

        TextView date,state,cost,sender,receiver,source,destination;
        TextView btnreview,time,cont,mcat,scat,payed;
        LinearLayout laymid,layfirst;
        ImageView ex;

        public requestViewHolder(View itemView) {
            super(itemView);
            date= (TextView) itemView.findViewById(R.id.txthidate);
            state= (TextView) itemView.findViewById(R.id.txthistate);
            cost= (TextView) itemView.findViewById(R.id.txthicost);
            sender= (TextView) itemView.findViewById(R.id.txthisender);
            receiver= (TextView) itemView.findViewById(R.id.txthirecei);
            source= (TextView) itemView.findViewById(R.id.edhisrc);
            destination= (TextView) itemView.findViewById(R.id.edhides);
            btnreview= (TextView) itemView.findViewById(R.id.btnreview);
            time= (TextView) itemView.findViewById(R.id.txthitime);
            cont= (TextView) itemView.findViewById(R.id.txthicont);

            payed= (TextView) itemView.findViewById(R.id.txthispayed);
            layfirst= (LinearLayout) itemView.findViewById(R.id.lay_first);
            laymid= (LinearLayout) itemView.findViewById(R.id.lay_mid);
            ex= (ImageView) itemView.findViewById(R.id.ex_arrow);
            mcat= (TextView) itemView.findViewById(R.id.txthismcat);
            scat= (TextView) itemView.findViewById(R.id.txthisscat);
        }
    }
}
