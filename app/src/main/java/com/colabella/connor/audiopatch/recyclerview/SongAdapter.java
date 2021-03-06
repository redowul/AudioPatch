package com.colabella.connor.audiopatch.recyclerview;

import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private static List<Audio> dataSet = new ArrayList<>();

    public SongAdapter() { }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_recyclerview_layout, parent,false);
        return new ViewHolder(view);
    }

    void setSelectedIndex(int selectedAudioPos, FloatingActionButton confirmationButton) {
        boolean wasSelected = false;
        for (int i = 0; i < getItemCount(); i++) {
            if( dataSet.get(i).isSelected() && i == selectedAudioPos) {
                wasSelected = true;
            }
            dataSet.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                if(!wasSelected) {
                    dataSet.get(i).setSelected(true);
                }
                else {
                    confirmationButton.hide();
                }
            }
        }
        notifyDataSetChanged();
    }

    public void deselectAll() {
        for (int i = 0; i < getItemCount(); i++) {
            dataSet.get(i).setSelected(false);
        }
        notifyDataSetChanged();
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(dataSet != null) {
            String itemTitle = dataSet.get(position).getTitle();
            holder.itemTitle.setText(itemTitle);

            String artist = dataSet.get(position).getArtist();
            holder.itemArtist.setText(artist);

            String duration = dataSet.get(position).getDuration();
            holder.itemDuration.setText(duration);

            Bitmap albumArt = dataSet.get(position).getAlbumArt();

            if (albumArt != null) {
                holder.albumArt.setImageBitmap(albumArt);
            }
            else {
                holder.albumArt.setImageResource(R.drawable.audiopatch_logo_square);
            }

            MainActivity mainActivity = new MainActivity();
            if(dataSet.get(position).isSelected()) {
                int selectedColor = mainActivity.getInstance().getResources().getColor(R.color.colorPrimaryAccent);

                holder.itemTitle.setTextColor(selectedColor);
                holder.itemArtist.setTextColor(selectedColor);
                holder.itemDuration.setTextColor(selectedColor);
            }
            else {
                int selectedColor = mainActivity.getInstance().getResources().getColor(R.color.textColor);

                holder.itemTitle.setTextColor(selectedColor);
                holder.itemArtist.setTextColor(selectedColor);
                holder.itemDuration.setTextColor(selectedColor);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FloatingActionButton confirmationButton = v.getRootView().findViewById(R.id.confirmation_button);
                    if(confirmationButton != null) {
                        confirmationButton.show();
                    }

                    Audio item = dataSet.get(position);
                    item.setSubmitter(SingletonController.getInstance().getUsername());

                    setSelectedIndex(position, confirmationButton);
                    SingletonController.getInstance().setSelectedAudio(item);
                }
            });
        }
    }

    public void updateDataSet(List<Audio> audioList) { dataSet = audioList; }

    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemArtist;
        TextView itemDuration;
        ImageView albumArt;
        View itemPanel;

        ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            albumArt = itemView.findViewById(R.id.item_album_art);
            itemPanel = itemView.findViewById(R.id.item_panel);
        }
    }
}