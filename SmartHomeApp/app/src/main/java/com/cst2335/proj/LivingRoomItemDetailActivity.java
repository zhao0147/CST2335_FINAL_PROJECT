package com.cst2335.proj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * An activity representing a single LivingRoomItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LivingRoomItemListActivity}.
 */
public class LivingRoomItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livingroomitem_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(LivingRoomItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(LivingRoomItemDetailFragment.ARG_ITEM_ID));
            LivingRoomItemDetailFragment fragment = new LivingRoomItemDetailFragment();
            fragment.setArguments(arguments);//pass variable arguments to fragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.livingroomitem_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//the menu item in App Bar
        int id = item.getItemId();
        if (id == android.R.id.home) {//if select home menu in App Bar
            navigateUpTo(new Intent(this, LivingRoomItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
