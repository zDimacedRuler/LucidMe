package com.example.amankumar.lucidme.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Utils;

public class ChooseWebsiteActivity extends AppCompatActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_website);
        toolbar= (Toolbar) findViewById(R.id.CW_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Explore");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!Utils.haveNetworkConnection(this)){
            Snackbar.make(toolbar,"No network connection",Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    public void wold_handler(View view) {
        Intent intent=new Intent(this,ExploreActivity.class);
        intent.putExtra("site","http://www.world-of-lucid-dreaming.com/articles.html");
        startActivity(intent);
    }

    public void lw_handler(View view) {
        Intent intent=new Intent(this,ExploreActivity.class);
        intent.putExtra("site","http://lucid.wikia.com/wiki/Lucid_dream");
        startActivity(intent);
    }

    public void li_handler(View view) {
        Intent intent=new Intent(this,ExploreActivity.class);
        intent.putExtra("site","http://www.lucidity.com/LucidDreamingFAQ2.html");
        startActivity(intent);
    }

    public void htl_handler(View view) {
        Intent intent=new Intent(this,ExploreActivity.class);
        intent.putExtra("site","http://howtolucid.com/");
        startActivity(intent);
    }
}
