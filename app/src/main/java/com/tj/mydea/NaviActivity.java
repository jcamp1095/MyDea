package com.tj.mydea;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.Objects;


public class NaviActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String user_id = "";
    private String user_name = "";
    private String email = "";
    //String imageURI = "";

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            TextView nav_username = (TextView) findViewById(R.id.nav_username);
            TextView nav_email = (TextView) findViewById(R.id.nav_email);
            nav_username.setText(user_name);
            nav_email.setText(email);

        }
    };
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if (b != null) {
            user_id = (String) b.get("user_id");
            user_name = (String) b.get("user_name");
            email = (String) b.get("email");
           // imageURI = (String) b.get("imageURI");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHandler.postDelayed(mUpdateUITimerTask, 2 * 1000);
        DiscoverFragment DiscoverFragment = new DiscoverFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.layout_for_fragments, DiscoverFragment).addToBackStack("discover").commit();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }*/


    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_discover) {
            DiscoverFragment DiscoverFragment = new DiscoverFragment();
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                if (Objects.equals(manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName(), "discover")) {
                    manager.popBackStackImmediate();
                }
            }
            manager.beginTransaction().replace(R.id.layout_for_fragments, DiscoverFragment).addToBackStack("discover").commit();
        } else if (id == R.id.nav_shareidea) {
            InputFragment InputFragment = new InputFragment();
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                if (Objects.equals(manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName(), "shareidea")) {
                    manager.popBackStackImmediate();
                }
            }
            manager.beginTransaction().replace(R.id.layout_for_fragments, InputFragment).addToBackStack("shareidea").commit();
        } else if (id == R.id.nav_myidea) {
            MyIdeaFragment MyIdeaFragment = new MyIdeaFragment();
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                if (Objects.equals(manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName(), "myidea")) {
                    manager.popBackStackImmediate();
                }
            }
            manager.beginTransaction().replace(R.id.layout_for_fragments, MyIdeaFragment).addToBackStack("myidea").commit();
        } else if (id == R.id.nav_messages) {
            Intent intent = new Intent(NaviActivity.this, MessageFragment.class);
            startActivity(intent);
        } else if (id == R.id.nav_login) {
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(NaviActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String get_user_id() {
        return user_id;
    }

    public String get_user_name() {
        return user_name;
    }
}
