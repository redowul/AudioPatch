package com.colabella.connor.audiopatch.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.NearbyConnections.NearbyConnections;
import com.colabella.connor.audiopatch.R;

import java.util.ArrayList;
import java.util.List;

public class MainDrawerSecondaryAdapter extends RecyclerView.Adapter<MainDrawerSecondaryAdapter.ViewHolder> {

    private static List<MenuItem> menuItems = new ArrayList<>();
    private boolean isAdvertising, isDiscovering;

    public void addItem(String itemTitle, boolean isSelected) {
        MenuItem item = new MenuItem(itemTitle, isSelected);
        menuItems.add(item);
    }

    private void setSelectedMenuItem(int selectedAudioPos) {
        int wasSelectedInt = -1;
        for (int i = 0; i < getItemCount(); i++) {
            if (menuItems.get(i).isSelected()) {
                wasSelectedInt = i;
            }
            menuItems.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                if (i != wasSelectedInt) {
                    menuItems.get(i).setSelected(true);
                }
            }
            notifyItemChanged(i);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_drawer_secondary_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(menuItems.get(position).getItemTitle());

        switch (position) {
            case 0: {
                holder.itemIcon.setImageResource(R.drawable.bluetooth_connected_36dp);
            }
            break;
            case 1: {
                holder.itemIcon.setImageResource(R.drawable.bluetooth_searching_36dp);
            }
            break;
        }

        // isSelected functionality
        MainActivity mainActivity = new MainActivity();
        if (menuItems.get(position).isSelected()) {
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
            holder.circle.setBackgroundResource(R.drawable.circle);
            holder.itemBackgroundCircle.setBackgroundResource(R.drawable.circle);
            holder.itemBackground.setVisibility(View.VISIBLE);

        } else {
            holder.itemView.setBackgroundResource(R.color.recyclerViewPrimary);
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.textColor));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.iconColor));
            holder.circle.setBackground(null);
            holder.itemBackgroundCircle.setBackground(null);
            holder.itemBackground.setVisibility(View.INVISIBLE);

        }

        holder.itemView.setOnClickListener(v -> {
            if (menuItems != null) {
                if (menuItems.size() > 0) {
                    Context context = mainActivity.getInstance();
                    setSelectedMenuItem(position);
                    NearbyConnections nearbyConnections = new NearbyConnections();
                    switch (position) {
                        case 0: { // Host
                            if(isDiscovering) {
                                nearbyConnections.stopDiscovery(context); // halt discovery if it's occurring
                                isDiscovering = false;
                            }
                            if(!isAdvertising) {
                                nearbyConnections.startAdvertising(); // begin advertising
                                isAdvertising = true;
                            }
                            else {
                                nearbyConnections.stopAdvertising(context);
                                isAdvertising = false; // already advertising, so halt advertising
                            }
                        }
                        break;
                        case 1: { // Join
                            if(isAdvertising) {
                                nearbyConnections.stopAdvertising(context);
                                isAdvertising = false;
                            }
                            if(!isDiscovering) {
                                nearbyConnections.startDiscovery();
                                isDiscovering = true;
                            }
                            else {
                                nearbyConnections.stopDiscovery(context);
                                isDiscovering = false;
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /******** View Holder Class*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTitle;
        private ImageView itemIcon;
        private ImageView circle, itemBackgroundCircle;
        private RelativeLayout itemBackground;

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.menu_item_title);
            itemIcon = itemView.findViewById(R.id.menu_item_icon);
            circle = itemView.findViewById(R.id.menu_item_circle);
            itemBackgroundCircle = itemView.findViewById(R.id.menu_item_background_circle);
            itemBackground = itemView.findViewById(R.id.menu_item_background);

            View menuItemPanel = itemView.findViewById(R.id.menu_item_panel);
            menuItemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}