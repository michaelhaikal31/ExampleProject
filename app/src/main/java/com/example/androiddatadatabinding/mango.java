package com.example.androiddatadatabinding;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

public class mango extends AbstractItem<mango, mango.ViewHolder> {
    private String name, description, imageUrl;

    public mango() {
    }

    public mango(String name, String description, String imageUrl) {

        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.movies_row;
    }

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);
        holder.name.setText(name);
        holder.description.setText(description);
        holder.imageUrl.setText(imageUrl);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, imageUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textView);
            description = (TextView) itemView.findViewById(R.id.textView2);
            imageUrl = (TextView) itemView.findViewById(R.id.textView3);

        }
    }
}
