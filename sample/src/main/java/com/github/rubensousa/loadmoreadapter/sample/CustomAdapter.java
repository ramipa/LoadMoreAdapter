package com.github.rubensousa.loadmoreadapter.sample;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.rubensousa.loadmoreadapter.LoadMoreAdapter;
import com.github.rubensousa.loadmoreadapter.ScrollListener;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends LoadMoreAdapter {

    public static final String STATE_DATA = "state_data";

    private ArrayList<String> mData = new ArrayList<>();

    public CustomAdapter(@NonNull OnLoadMoreListener loadMoreListener, @Nullable ScrollListener scrollListener) {
        super(loadMoreListener, scrollListener);
    }

    @Override
    public void restoreState(Bundle restoreState) {
        super.restoreState(restoreState);
        if (restoreState != null) {
            mData = restoreState.getStringArrayList(STATE_DATA);
        }
    }

    @Override
    public void saveState(Bundle outState) {
        super.saveState(outState);
        outState.putStringArrayList(STATE_DATA, mData);
    }

    @Override
    public List getItems() {
        return mData;
    }

    @Override
    public ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter, parent, false));
    }

    public void addData(List<String> data) {
        // Use this here to avoid forgetting about calling this
        setLoadingState(false);
        int previousSize = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(previousSize, data.size());
    }

    @Override
    public void onBindViewHolder(LoadMoreAdapter.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_NORMAL) {
            ((ViewHolder) holder).setData(mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends LoadMoreAdapter.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }

        public void setData(String data) {
            textView.setText(data);
        }
    }
}
