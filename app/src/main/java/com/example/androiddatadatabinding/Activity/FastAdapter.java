package com.example.androiddatadatabinding.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.androiddatadatabinding.R;
import com.example.androiddatadatabinding.mango;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class FastAdapter extends AppCompatActivity implements ActionBar.OnNavigationListener {
    private RecyclerView recyclerView;
    Context context;
    private LayoutAnimationController controller;
    List<mango> mangos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_adapter);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }
        final String[] dropwnValues = getResources().getStringArray(R.array.dropdown_fastadapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, android.R.id.text1, dropwnValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setListNavigationCallbacks(adapter, this);
        prepareData();

        FastItemAdapter<mango> fastItemAdapter = new FastItemAdapter<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(FastAdapter.this,LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(fastItemAdapter);

        //runLayoutAnimation(recyclerView);

        fastItemAdapter.add(mangos);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView, LayoutAnimationController controller) {
        final Context context = recyclerView.getContext();
        // controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(controller);
//        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }

    private void prepareData() {
        mango mangog = new mango("Mad Max: Fury Road", "Action & Adventure", "2015");
        mangos.add(mangog);
        mangog = new mango("Inside Out", "Animation, Kids & Family", "2015");
        mangos.add(mangog);

        mango movie = new mango("Mad Max: Fury Road", "Action & Adventure", "2015");
        mangos.add(movie);

        movie = new mango("Inside Out", "Animation, Kids & Family", "2015");
        mangos.add(movie);

        movie = new mango("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        mangos.add(movie);

        movie = new mango("Shaun the Sheep", "Animation", "2015");
        mangos.add(movie);

        movie = new mango("The Martian", "Science Fiction & Fantasy", "2015");
        mangos.add(movie);

        movie = new mango("Mission: Impossible Rogue Nation", "Action", "2015");
        mangos.add(movie);

        movie = new mango("Up", "Animation", "2009");
        mangos.add(movie);

        movie = new mango("Star Trek", "Science Fiction", "2009");
        mangos.add(movie);

        movie = new mango("The LEGO Movie", "Animation", "2014");
        mangos.add(movie);

        movie = new mango("Iron Man", "Action & Adventure", "2008");
        mangos.add(movie);

        movie = new mango("Aliens", "Science Fiction", "1986");
        mangos.add(movie);

        movie = new mango("Chicken Run", "Animation", "2000");
        mangos.add(movie);

        movie = new mango("Back to the Future", "Science Fiction", "1985");
        mangos.add(movie);

        movie = new mango("Raiders of the Lost Ark", "Action & Adventure", "1981");
        mangos.add(movie);

        movie = new mango("Goldfinger", "Action & Adventure", "1965");
        mangos.add(movie);

        movie = new mango("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        mangos.add(movie);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        switch (i) {
            case 0:
                controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_animation_from_right);
                runLayoutAnimation(recyclerView, controller);
                break;
            case 1:
                controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_animation_fall_down);
                runLayoutAnimation(recyclerView, controller);
                break;
            default:
                controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_animation_from_bottom);
                runLayoutAnimation(recyclerView, controller);
                break;
        }
        Toast.makeText(getApplicationContext(), Integer.toString(i), Toast.LENGTH_LONG).show();
        return true;
    }
}
