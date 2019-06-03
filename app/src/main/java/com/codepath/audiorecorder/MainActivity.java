package com.codepath.audiorecorder;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.audiorecorder.fragments.RecordFragment;
import com.codepath.audiorecorder.fragments.ListenFragment;

// Import recording relative libraries
/************************************************/
import android.view.MenuItem;

/************************************************/
// Import date relative libraries


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Main Activity";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.fragment_listen);

        final FragmentManager fragmentManager=getSupportFragmentManager();

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_compose:
                        fragment=new RecordFragment();
                        //    Toast.makeText(MainActivity.this, "Compose!", Toast.LENGTH_SHORT);
                        break;
                    case R.id.action_home:
                        fragment=new ListenFragment();
                        //    Toast.makeText(MainActivity.this, "Compose!", Toast.LENGTH_SHORT);
                        break;
                    default:
                        fragment=new RecordFragment();
                        //    Toast.makeText(MainActivity.this, "Profile!", Toast.LENGTH_SHORT);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.action_compose);

    }
}
