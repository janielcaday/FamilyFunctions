package com.example.familfunctions_team14;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class ChoreAdapter extends RecyclerView.Adapter<ChoreAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Chores> mChores;

    public ChoreAdapter(Context context, List<Chores> chores){
        mContext = context;
        mChores = chores;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Chores choreCurrent = mChores.get(position);

        holder.textViewTitle.setText(choreCurrent.getTitle());
        holder.textViewDescription.setText("Description: \n"+ choreCurrent.getDescription());
        holder.textViewPoints.setText("Points: " + choreCurrent.getPointsWorth());
        holder.textViewAssignedTo.setText("| Assigned to: " + choreCurrent.getAssignedTo());


        Picasso.get()
                .load(choreCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.parentStatus || RegisterActivity.parentStatus){
                    Intent toVerification = new Intent(mContext, ParentVerificationActivity.class);
                    toVerification.putExtra("choreCurrentTitle", choreCurrent.getTitle());
                    toVerification.putExtra("choreCurrentDescription", choreCurrent.getDescription());
                    toVerification.putExtra("choreCurrentPointsWorth", choreCurrent.getPointsWorth());
                    toVerification.putExtra("choreCurrentAssignedTo", choreCurrent.getAssignedTo());
                    toVerification.putExtra("choreCurrentImageUrl", choreCurrent.getImageUrl());
                    toVerification.putExtra("choreCurrentId", choreCurrent.getId());
                    toVerification.putExtra("choreCurrentStatus", choreCurrent.getStatus());

                    mContext.startActivity(toVerification);
                } else {
                    Intent toSubmission = new Intent(mContext, ChildSubmissionActivity.class);
                    toSubmission.putExtra("choreCurrentTitle", choreCurrent.getTitle());
                    toSubmission.putExtra("choreCurrentDescription", choreCurrent.getDescription());
                    toSubmission.putExtra("choreCurrentPointsWorth", choreCurrent.getPointsWorth());
                    toSubmission.putExtra("choreCurrentAssignedTo", choreCurrent.getAssignedTo());
                    toSubmission.putExtra("choreCurrentImageUrl", choreCurrent.getImageUrl());
                    toSubmission.putExtra("choreCurrentId", choreCurrent.getId());
                    toSubmission.putExtra("choreCurrentStatus", choreCurrent.getStatus());
                    mContext.startActivity(toSubmission);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mChores.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewTitle;
        public TextView textViewDescription;
        public TextView textViewPoints;
        public TextView textViewAssignedTo;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPoints = itemView.findViewById(R.id.text_view_points);
            textViewAssignedTo = itemView.findViewById(R.id.text_view_assignedTo);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }


}
