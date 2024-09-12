package com.example.neredeyimapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neredeyimapp.R;

public class postViewHolder extends RecyclerView.ViewHolder {
    public ImageView postViewHolderUserImage;
    public TextView postViewHolderUserName;
    public ImageView postViewHolderImage;
    public TextView postViewHolderDescription;
    public TextView postViewHolderLikeCount;
    public TextView postViewHolderTime;

    public postViewHolder(@NonNull View itemView) {
        super(itemView);
        postViewHolderUserImage = itemView.findViewById(R.id.postKullaniciImageView);
        postViewHolderUserName = itemView.findViewById(R.id.postKullaniciAdiTextView);
        postViewHolderImage = itemView.findViewById(R.id.postImageView);
        postViewHolderDescription = itemView.findViewById(R.id.postAciklamaTextView);
        postViewHolderLikeCount = itemView.findViewById(R.id.postBegeniSayisiTextView);
        postViewHolderTime = itemView.findViewById(R.id.postAtilmaTarihiTextView);
    }

    public ImageView getPostViewHolderUserImage() {
        return postViewHolderUserImage;
    }

    public void setPostViewHolderUserImage(ImageView postViewHolderUserImage) {
        this.postViewHolderUserImage = postViewHolderUserImage;
    }

    public TextView getPostViewHolderUserName() {
        return postViewHolderUserName;
    }

    public void setPostViewHolderUserName(TextView postViewHolderUserName) {
        this.postViewHolderUserName = postViewHolderUserName;
    }

    public ImageView getPostViewHolderImage() {
        return postViewHolderImage;
    }

    public void setPostViewHolderImage(ImageView postViewHolderImage) {
        this.postViewHolderImage = postViewHolderImage;
    }

    public TextView getPostViewHolderDescription() {
        return postViewHolderDescription;
    }

    public void setPostViewHolderDescription(TextView postViewHolderDescription) {
        this.postViewHolderDescription = postViewHolderDescription;
    }

    public TextView getPostViewHolderLikeCount() {
        return postViewHolderLikeCount;
    }

    public void setPostViewHolderLikeCount(TextView postViewHolderLikeCount) {
        this.postViewHolderLikeCount = postViewHolderLikeCount;
    }

    public TextView getPostViewHolderTime() {
        return postViewHolderTime;
    }

    public void setPostViewHolderTime(TextView postViewHolderTime) {
        this.postViewHolderTime = postViewHolderTime;
    }

}
