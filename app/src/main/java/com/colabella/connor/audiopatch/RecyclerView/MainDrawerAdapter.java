package com.colabella.connor.audiopatch.RecyclerView;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;

import java.util.ArrayList;
import java.util.List;

class MenuItem {

    private String itemTitle;
    private boolean selected;

    MenuItem(String itemTitle, boolean selected) {
        this.itemTitle = itemTitle;
        this.selected = selected;
    }

    String getItemTitle() {
        return itemTitle;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }
}

public class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.ViewHolder> {

    private static List<MenuItem> menuItems = new ArrayList<>();

    public void addItem(String itemTitle, boolean isSelected) {
        MenuItem item = new MenuItem(itemTitle, isSelected);
        menuItems.add(item);
    }

    private void setSelectedMenuItem(int selectedAudioPos) {
        for (int i = 0; i < getItemCount(); i++) {
            menuItems.get(i).setSelected(false);
            if (i == selectedAudioPos) {
                menuItems.get(i).setSelected(true);
            }
            notifyItemChanged(i);
        }
        notifyDataSetChanged();
    }

    @Override
    public MainDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_drawer_item, parent, false);
        return new MainDrawerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainDrawerAdapter.ViewHolder holder, int position) {
        holder.itemTitle.setText(menuItems.get(position).getItemTitle());

        switch (menuItems.get(position).getItemTitle()) {
            case "Home": {
                holder.itemIcon.setImageResource(R.drawable.home_36dp);
            }
            break;
            case "Settings": {
                holder.itemIcon.setImageResource(R.drawable.settings_36dp);
            }
            break;
            case "About": {
                holder.itemIcon.setImageResource(R.drawable.info_outline_36dp);
            }
            break;
        }

        // isSelected functionality
        MainActivity mainActivity = new MainActivity();
        if (menuItems.get(position).isSelected()) { // If isSelected returns true, highlight the item.
            holder.itemView.setBackgroundResource(R.color.recyclerViewDark);
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.itemView.setBackgroundResource(R.color.recyclerViewPrimary);
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.textColor));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.iconColor));
        }

        holder.itemView.setOnClickListener(v -> {
            if (menuItems != null) {
                if (menuItems.size() > 0) {
                    setSelectedMenuItem(position);
                    switch (menuItems.get(position).getItemTitle()) {
                        case "Home": {

                        }
                        break;
                        case "Settings": {
                            System.out.println(menuItems.get(position).getItemTitle());
                        }
                        break;
                        case "About": {
                            MainActivity mainActivity1 = new MainActivity();
                            DrawerLayout drawer = mainActivity1.getInstance().findViewById(R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
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

        private ViewHolder(View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.menu_item_title);
            itemIcon = itemView.findViewById(R.id.menu_item_icon);

            View menuItemPanel = itemView.findViewById(R.id.menu_item_panel);
            menuItemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}