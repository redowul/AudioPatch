package com.colabella.connor.audiopatch.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colabella.connor.audiopatch.R;

public class MainDrawerAdapter extends RecyclerView.Adapter<MainDrawerAdapter.ViewHolder> {

    private final String[] menuItems = { "Host", "Join", "Home", "Settings", "About" };

    @Override
    public MainDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_drawer_item, parent, false);
        return new MainDrawerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainDrawerAdapter.ViewHolder holder, int position) {
        holder.item.setText(menuItems[position]);
    }

    @Override
    public int getItemCount() {
        return menuItems.length;
    }

    /******** View Holder Class*/
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView item;

        private ViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.menu_item_title);

            View menuItemPanel = itemView.findViewById(R.id.menu_item_panel);
            menuItemPanel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println(menuItems[getAdapterPosition()]);
        }
    }
}