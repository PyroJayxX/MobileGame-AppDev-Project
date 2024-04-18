package com.example.mobilegameappdevproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LB_RecyclerViewAdapter extends RecyclerView.Adapter<LB_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<LeaderboardModel> LeaderboardModel;
    public LB_RecyclerViewAdapter(Context context, ArrayList<LeaderboardModel> arrLeaderboardModel){
        this.context = context;
        this.LeaderboardModel = arrLeaderboardModel;
    }

    @NonNull
    @Override
    public LB_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.leaderboard_row, parent, false);

        return new LB_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LB_RecyclerViewAdapter.MyViewHolder holder, int position) {
    holder.tvRank.setText(LeaderboardModel.get(position).getTxtRank());
    holder.tvRankName.setText(LeaderboardModel.get(position).getTxtRankName());
    holder.tvRankScore.setText(LeaderboardModel.get(position).getTxtRankScore());
    }

    @Override
    public int getItemCount() {
        return LeaderboardModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvRank, tvRankName, tvRankScore;
         public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.txtRank);
            tvRankName = itemView.findViewById(R.id.txtRankNames);
            tvRankScore = itemView.findViewById(R.id.txtRankScores);
        }
    }
}
