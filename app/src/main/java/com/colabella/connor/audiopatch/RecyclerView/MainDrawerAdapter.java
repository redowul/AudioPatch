package com.colabella.connor.audiopatch.RecyclerView;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.colabella.connor.audiopatch.Fragments.AboutFragment;
import com.colabella.connor.audiopatch.Fragments.SettingsFragment;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;

import java.util.ArrayList;
import java.util.List;

class MenuItem {

    private String itemTitle;
    private boolean selected;
    private boolean circleEnabled;

    MenuItem(String itemTitle, boolean selected, boolean circleEnabled) {
        this.itemTitle = itemTitle;
        this.selected = selected;
        this.circleEnabled = circleEnabled;
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

    boolean isCircleEnabled() {
        return circleEnabled;
    }
}

public class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.ViewHolder> {

    private static List<MenuItem> menuItems = new ArrayList<>();

    public void addItem(String itemTitle, boolean isSelected, boolean circleEnabled) {
        MenuItem item = new MenuItem(itemTitle, isSelected, circleEnabled);
        menuItems.add(item);
    }

    public void setSelectedMenuItem(int selectedAudioPos) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_drawer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(menuItems.get(position).getItemTitle());

        switch (position) {
            case 0: {
                holder.itemIcon.setImageResource(R.drawable.home_36dp);
            }
            break;
            case 1: {
                holder.itemIcon.setImageResource(R.drawable.settings_36dp);
            }
            break;
            case 2: {
                holder.itemIcon.setImageResource(R.drawable.info_outline_36dp);
            }
            break;
        }

        // isSelected functionality
        MainActivity mainActivity = new MainActivity();
        if (menuItems.get(position).isSelected()) {
            holder.itemView.setBackgroundResource(R.color.recyclerViewDark);
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.colorPrimary));
        }
        else {
            holder.itemView.setBackgroundResource(R.color.recyclerViewPrimary);
            holder.itemTitle.setTextColor(mainActivity.getInstance().getResources().getColor(R.color.textColor));
            holder.itemIcon.setColorFilter(mainActivity.getInstance().getResources().getColor(R.color.iconColor));
        }

        holder.itemView.setOnClickListener(v -> {
            if (menuItems != null) {
                if (menuItems.size() > 0) {
                    setSelectedMenuItem(position);
                    Button addAudioButton = mainActivity.getInstance().findViewById(R.id. add_audio_button);
                    switch (position) {
                        case 0: { // return to Home Activity
                            mainActivity.getInstance().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            addAudioButton.setVisibility(View.VISIBLE);
                        }
                        break;
                        case 1: { // open Settings fragment
                            mainActivity.getInstance().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            mainActivity.getInstance().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new SettingsFragment(), "SettingsFragment").addToBackStack("open_settings").commit();
                            addAudioButton.setVisibility(View.GONE);
                        }
                        break;
                        case 2: { // open About fragment
                            mainActivity.getInstance().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            mainActivity.getInstance().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new AboutFragment(), "AboutFragment").addToBackStack("open_about").commit();
                            addAudioButton.setVisibility(View.GONE);
                        }
                        break;
                    }
                    DrawerLayout drawer = mainActivity.getInstance().findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
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
        public void onClick(View view) { }
    }
}