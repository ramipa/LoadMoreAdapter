package com.github.rubensousa.loadmoreadapter.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.rubensousa.loadmoreadapter.LoadMoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoadMoreAdapter.OnLoadMoreListener{

    CustomAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressView = findViewById(R.id.progress_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomAdapter(this, null);

        if (savedInstanceState == null) {
            mAdapter.addData(getData(0));
        } else {
            mAdapter.restoreState(savedInstanceState);
            if (mAdapter.isLoading()) {
                onLoadMore(mAdapter.getItemCount() - 1);
            }
        }

        mAdapter.setup(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }

    @Override
    public void onLoadMore(final int offset) {

        progressView.setVisibility(View.VISIBLE);

        mRecyclerView.postDelayed(() -> {
            if (mAdapter != null) {
                // If loading fails, call setLoading(false) to cancel loading more
                mAdapter.addData(getData(offset));
                progressView.setVisibility(View.GONE);
            }
        }, 1500);
    }

    private List<String> getData(int offset) {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            data.add((offset + i) + "");
        }

        return data;
    }
}
