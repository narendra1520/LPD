package com.example.narendra.lpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
 * Created by Narendra on 10-01-2018.
 */

public class request_adpater2 extends RecyclerView.Adapter<request_adpater2.requestViewHolder>{
    Context context;
    List<request_data> request_datas;

    private static int currentPosition = 0;
    private static int state=0;

    public request_adpater2 (Context context, List<request_data> request_datas) {
        this.context = context;
        this.request_datas = request_datas;
    }

    @Override
    public request_adpater2.requestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.request_layout_provider,null,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
        return new request_adpater2.requestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(request_adpater2.requestViewHolder holder, final int position) {
        final request_data request_data=request_datas.get(position);

        holder.laymid.setVisibility(View.GONE);
        holder.ex.setImageResource(R.drawable.ex_more);

        holder.mcat.setText(request_data.getMaincat());
        holder.scat.setText(request_data.getSubcat());
        holder.source.setText(request_data.getSrcaddr());
        holder.destination.setText(request_data.getDesaddr());
        holder.date.setText(request_data.getDate());
        holder.state.setText(request_data.getStatus());
        holder.time.setText(request_data.getTime());
        holder.cont.setText(request_data.getCont());
        holder.cost.setText(String.valueOf(request_data.getCost()));
        holder.sender.setText("From: "+request_data.getSender());
        holder.receiver.setText("To: "+request_data.getReceiver());
        holder.btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder intru = new AlertDialog.Builder(context);
                intru.setTitle("Request");
                intru.setMessage("You Want to Accept ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle b=new Bundle();
                        b.putDouble("srclat",request_data.getSrclat());b.putDouble("srclong",request_data.getSrclong());
                        b.putDouble("deslat",request_data.getDeslat());b.putDouble("deslong",request_data.getDeslong());
                        b.putString("send",request_data.getSender());b.putString("recei",request_data.getReceiver());
                        b.putString("cont",request_data.getCont());
                        b.putInt("reqid",request_data.getReqid());
                        Fragment fm=new request_route();
                        fm.setArguments(b);
                        AppCompatActivity activity = (AppCompatActivity)v.getContext();
                        android.support.v4.app.FragmentManager mn = activity.getSupportFragmentManager();
                        mn.beginTransaction().replace(R.id.include2,fm,"req_frag_Route").addToBackStack(null).commit();
                    }
                }).setNegativeButton("Cancel",null);
                AlertDialog alert = new Functions().funAlertShow(intru);
            }
        });
        holder.btndenied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppCompatActivity activity = (AppCompatActivity)v.getContext();
                AlertDialog.Builder intru = new AlertDialog.Builder(context);
                intru.setTitle("Request");
                intru.setMessage("Are You Sure to Denied Request ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SetRequest(activity.getResources().getString(R.string.req_Canceled),request_data.getReqid(),(Activity) context);
                        android.support.v4.app.FragmentManager mn = activity.getSupportFragmentManager();
                        mn.beginTransaction().replace(R.id.include2,new request_view_provider()).commit();
                    }
                }).setNegativeButton("Cancel",null);
                AlertDialog alert = new Functions().funAlertShow(intru);
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
        TextView btnaccept,btndenied,time,cont,mcat,scat;
        LinearLayout laymid,layfirst;
        ImageView ex;

        public requestViewHolder(View itemView) {
            super(itemView);

            date= (TextView) itemView.findViewById(R.id.txtprodate);
            state= (TextView) itemView.findViewById(R.id.txtprostate);
            cost= (TextView) itemView.findViewById(R.id.txtprocost);
            sender= (TextView) itemView.findViewById(R.id.txtprosender);
            receiver= (TextView) itemView.findViewById(R.id.txtprorecei);
            source= (TextView) itemView.findViewById(R.id.edprosrc);
            source.setKeyListener(null);
            destination= (TextView) itemView.findViewById(R.id.edprodes);
            destination.setKeyListener(null);
            btndenied= (TextView) itemView.findViewById(R.id.btndenied);
            btnaccept= (TextView) itemView.findViewById(R.id.btnaccept);
            time= (TextView) itemView.findViewById(R.id.txtprotime);
            cont= (TextView) itemView.findViewById(R.id.txtprocon);

            layfirst= (LinearLayout) itemView.findViewById(R.id.lay_first);
            laymid= (LinearLayout) itemView.findViewById(R.id.lay_mid);
            ex= (ImageView) itemView.findViewById(R.id.ex_arrow);
            mcat= (TextView) itemView.findViewById(R.id.txtpromcat);
            scat= (TextView) itemView.findViewById(R.id.txtproscat);
        }
    }
}
