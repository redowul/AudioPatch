package com.colabella.connor.audiopatch.recyclerview;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;

import java.util.ArrayList;
import java.util.List;

public class AlbumListViewAdapter extends RecyclerView.Adapter<AlbumListViewAdapter.ViewHolder> {
    private static List<Audio> dataSet = new ArrayList<>();
    private boolean selectedYet = false; // Used as a "buffer" of sorts to avoid shuttering the songAdapter every time an item is selected

    public AlbumListViewAdapter(List<Audio> selectedAlbum) {
        if(dataSet != null) {
            if (dataSet.size() == 0) {
                dataSet = selectedAlbum;
            }
        }
    }

    public void setDataSet(List<Audio> selectedAlbum) { // Sets RecyclerView data
        dataSet = selectedAlbum;
    }

    private void setSelectedIndex(int selectedAudioPos, FloatingActionButton confirmationButton) {
        boolean wasSelected = false;
        for (int i = 0; i < getItemCount(); i++) {
            if( dataSet.get(i).isSelected() && i == selectedAudioPos) {
                wasSelected = true;
            }
            dataSet.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                if(!wasSelected) { // the item was not selected, so now we're selecting it
                    if(!selectedYet) {
                        SingletonController.getInstance().getSongAdapter().deselectAll();
                        selectedYet = true;
                    }
                    dataSet.get(i).setSelected(true);
                    Audio item = dataSet.get(selectedAudioPos);
                    SingletonController.getInstance().setSelectedAudio(item);
                    confirmationButton.show();
                }
                else { // the item was selected, so we're deselecting it
                    confirmationButton.hide();
                    SingletonController.getInstance().getSongAdapter().deselectAll();
                }
            }
        }
        SingletonController.getInstance().getSongAdapter().notifyDataSetChanged();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_recyclerview_song_layout, parent, false);
        return new AlbumListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = dataSet.get(position).getTitle();
        holder.itemTitle.setText(title);
        String artist = dataSet.get(position).getArtist();
        holder.itemArtist.setText(artist);
        String duration = dataSet.get(position).getDuration();
        holder.itemDuration.setText(duration);
        int number = position + 1;
        String itemNumber;
        if(number <= 99) {
           itemNumber = Integer.toString(position + 1);
        }
        else {
            itemNumber = "99+"; // Needed because the design can't fit any song positions higher than 99 without pushing other text off the screen
        }
        holder.itemNumber.setText(itemNumber);

        MainActivity mainActivity = new MainActivity();
        if(dataSet.get(position).isSelected()) {
            int selectedColor = mainActivity.getInstance().getResources().getColor(R.color.colorPrimaryAccent);

            holder.itemTitle.setTextColor(selectedColor);
            holder.itemArtist.setTextColor(selectedColor);
            holder.itemDuration.setTextColor(selectedColor);
            holder.itemNumber.setTextColor(selectedColor);
        }
        else {
            int selectedColor = mainActivity.getInstance().getResources().getColor(R.color.textColor);

            holder.itemTitle.setTextColor(selectedColor);
            holder.itemArtist.setTextColor(selectedColor);
            holder.itemDuration.setTextColor(selectedColor);
            holder.itemNumber.setTextColor(selectedColor);
        }
    }

    @Override
    public int getItemCount() {
        if(dataSet != null) { return dataSet.size(); }
        return 0;
    }

    /******** View Holder Class*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTitle;
        private TextView itemArtist;
        private TextView itemDuration;
        private TextView itemNumber;

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemArtist = itemView.findViewById(R.id.item_artist);
            itemDuration = itemView.findViewById(R.id.item_duration);
            itemNumber = itemView.findViewById(R.id.item_number);
            View itemPanel = itemView.findViewById(R.id.item_panel);
            itemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FloatingActionButton confirmationButton = view.getRootView().findViewById(R.id.confirmation_button);
            setSelectedIndex(this.getAdapterPosition(), confirmationButton);
        }
    }
}
