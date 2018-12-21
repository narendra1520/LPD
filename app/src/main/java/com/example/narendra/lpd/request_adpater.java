package com.example.narendra.lpd;

/**
 * Created by Narendra on 25-12-2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.List;

public class request_adpater extends RecyclerView.Adapter<request_adpater.requestViewHolder>{

    Context context;
    List<request_data> request_datas;

    private static int currentPosition = 0;
    private static int state=0;

    public request_adpater(Context context, List<request_data> request_datas) {
        this.context = context;
        this.request_datas = request_datas;
    }

    @Override
    public requestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.request_layout_rec,null,false);
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

        holder.source.setText(request_data.getSrcaddr());
        holder.destination.setText(request_data.getDesaddr());
        holder.date.setText(request_data.getDate());
        holder.state.setText(request_data.getStatus());
        holder.time.setText(request_data.getTime());
        holder.con.setText(request_data.getCont());
        holder.cost.setText(String.valueOf(request_data.getCost()));
        holder.sender.setText("From: "+request_data.getSender());
        holder.receiver.setText("To: "+request_data.getReceiver());
        holder.btntrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(request_data.getStatus().equals("InTransit")){
                    Bundle b=new Bundle();
                    b.putDouble("srclat",request_data.getSrclat());b.putDouble("srclong",request_data.getSrclong());
                    b.putDouble("deslat",request_data.getDeslat());b.putDouble("deslong",request_data.getDeslong());
                    b.putInt("reqid",request_data.getReqid());
                    android.support.v4.app.Fragment fm=new Track_package();
                    fm.setArguments(b);
                    AppCompatActivity activity = (AppCompatActivity)v.getContext();
                    android.support.v4.app.FragmentManager mn = activity.getSupportFragmentManager();
                    mn.beginTransaction().replace(R.id.include,fm).addToBackStack(null).commit();
                }else {
                    Toast.makeText(context,"You Can't Track Pending Request", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(request_data.getStatus().equalsIgnoreCase("Pending")){
                    AlertDialog.Builder intru = new AlertDialog.Builder(context);
                    intru.setTitle("Request");
                    intru.setMessage("You Want to Cancel ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new SetRequest("Canceled",request_data.getReqid(),(Activity) context);
                            AppCompatActivity activity = (AppCompatActivity)v.getContext();
                            android.support.v4.app.FragmentManager mn = activity.getSupportFragmentManager();
                            android.support.v4.app.Fragment fr=mn.findFragmentByTag("Req_View");
                            mn.beginTransaction().detach(fr).attach(fr).commit();
                        }
                    }).setNegativeButton("No",null);
                    AlertDialog alert = new Functions().funAlertShow(intru);
                }else {
                    Toast.makeText(context, "You Can't Cancel InTransit Request", Toast.LENGTH_SHORT).show();
                }
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

    class requestViewHolder extends RecyclerView.ViewHolder{

        TextView date,state,cost,sender,receiver,btncancel,btntrack,time,con,source,destination,mcat,scat;
        LinearLayout laymid,layfirst;
        ImageView ex;

        public requestViewHolder(View itemView) {
            super(itemView);

            date= (TextView) itemView.findViewById(R.id.txtrecdate);
            state= (TextView) itemView.findViewById(R.id.txtrecstate);
            cost= (TextView) itemView.findViewById(R.id.txtreccost);
            sender= (TextView) itemView.findViewById(R.id.txtrecsender);
            receiver= (TextView) itemView.findViewById(R.id.txtrecrecei);
            source= (TextView) itemView.findViewById(R.id.edrecsrc);
            destination= (TextView) itemView.findViewById(R.id.edrecdes);
            btncancel= (TextView) itemView.findViewById(R.id.btncancel);
            btntrack= (TextView) itemView.findViewById(R.id.btntrack);
            time= (TextView) itemView.findViewById(R.id.txtrectime);
            con= (TextView) itemView.findViewById(R.id.txtreccon);

            layfirst= (LinearLayout) itemView.findViewById(R.id.lay_first);
            laymid= (LinearLayout) itemView.findViewById(R.id.lay_mid);
            ex= (ImageView) itemView.findViewById(R.id.ex_arrow);
            mcat= (TextView) itemView.findViewById(R.id.txtrecmcat);
            scat= (TextView) itemView.findViewById(R.id.txtrecscat);
        }
    }
}

