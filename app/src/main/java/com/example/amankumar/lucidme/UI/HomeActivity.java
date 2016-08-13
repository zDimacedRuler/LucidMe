package com.example.amankumar.lucidme.UI;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amankumar.lucidme.Account.LoginActivity;
import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Services.MessageNotificationService;
import com.example.amankumar.lucidme.UI.Chat.ChatFragment;
import com.example.amankumar.lucidme.UI.Chat.FindChatActivity;
import com.example.amankumar.lucidme.UI.Search.SearchableActivity;
import com.example.amankumar.lucidme.Utils.Constants;
import com.example.amankumar.lucidme.Utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final int ADD_DREAM = 1;
    private static final int RESULT_DONE = 1;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ViewPager viewPager;
    TabLayout tabLayout;
    View headerView;
    ImageView imageView;
    TextView userNameText;
    SharedPreferences sp;
    FirebaseDatabase ref;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String currentUser;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton floatingActionButton;
    Bitmap profilePic;
    Boolean pinState;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.AddDreamButton);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0)
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.fab_plus));
                else if (position == 1)
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_chat_white_24dp));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUser = sp.getString(Constants.CURRENT_USER, "");
        ref = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        headerView = navigationView.getHeaderView(0);
        imageView = (ImageView) headerView.findViewById(R.id.nav_avatar);
        userNameText = (TextView) headerView.findViewById(R.id.nav_username);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    SharedPreferences.Editor spe = sp.edit();
                    spe.putString(Constants.CURRENT_USER, null).apply();
                    spe.putString(Constants.CURRENT_USER_NAME, null).apply();
                    takeUserToLoginScreenOnUnAuth();
                } else {
                    userRef = ref.getReference().child(Constants.LOCATION_USERS).child(currentUser);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child(Constants.LOCATION_USERNAME).getValue().toString();
                            SharedPreferences.Editor spe = sp.edit();
                            spe.putString(Constants.CURRENT_USER_NAME, name).apply();
                            userNameText.setText(name);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
        mAuth.addAuthStateListener(mAuthStateListener);
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        if (!isMyServiceRunning(MessageNotificationService.class)) {
            startNotificationService();
        }
    }

    private void startNotificationService() {
        Intent notificationIntent = new Intent(this, MessageNotificationService.class);
        startService(notificationIntent);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(JournalFragment.newInstance(), "Journal");
        adapter.addFragment(ChatFragment.newInstance(), "Chat");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }


    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_settings:
                                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_dream_signs:
                                Intent dreamSignsIntent = new Intent(HomeActivity.this, DreamSignActivity.class);
                                startActivity(dreamSignsIntent);
                                break;
                            case R.id.nav_explore:
                                Intent exploreIntent = new Intent(HomeActivity.this, ChooseWebsiteActivity.class);
                                startActivity(exploreIntent);
                                break;
                            case R.id.nav_analysis:
                                Intent analysisIntent = new Intent(HomeActivity.this, AnalysisActivity.class);
                                startActivity(analysisIntent);
                                break;
                            case R.id.nav_about:
                                Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
                                startActivity(aboutIntent);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pinState = sp.getBoolean(Constants.PIN_STATE, false);
        if (pinState)
            Utils.lockAppCheck(this);
        String fileName = currentUser + ".png";
        profilePic = getThumbnail(fileName);
        if (profilePic != null)
            imageView.setImageBitmap(profilePic);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pinState)
            Utils.lockAppStoreTime(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.JF_search:
                Intent intent = new Intent(this, SearchableActivity.class);
                startActivity(intent);
                break;
            case R.id.JF_settings:
            case R.id.CF_settings:
                Intent settingIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
        }
        return true;
    }

    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void AddDreamButtonHandler(View view) {
        int selectedTab = tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            Intent intent = new Intent(this, DreamActivity.class);
            intent.putExtra("caller", "Home");
            startActivityForResult(intent, ADD_DREAM);
        } else {
            Intent intent = new Intent(this, FindChatActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_DREAM) {
            if (resultCode == RESULT_DONE) {
                Snackbar.make(floatingActionButton, "Dream Added", Snackbar.LENGTH_SHORT).show();
            } else if (resultCode == 2) {
                Snackbar.make(floatingActionButton, "Unable to store empty Dream ", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        Snackbar.make(floatingActionButton, "Please click BACK again to exit", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    public Bitmap getThumbnail(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
        }
        return thumbnail;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
