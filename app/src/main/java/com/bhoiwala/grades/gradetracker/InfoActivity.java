package com.bhoiwala.grades.gradetracker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layer);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Grade Tracker");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#005af7")));
        ab.setDisplayHomeAsUpEnabled(true);
        TextView about = (TextView)findViewById(R.id.aboutTv);
        about.setText("This app is designed to help you calculate your grades in your " +
                "classes by keeping track of each individual assignment. ");

        TextView tutorial = (TextView)findViewById(R.id.howToUseTv);
        tutorial.setText(" - On the main page, add your courses.\n\n" +
                         " - Then select each course and add categories and add their category weight.\n\n" +
                         " - Then select each category and add individual assignment grades.\n\n" +
                         " - That's it. Go back the the main page and you will see the final calculated grade for that course.\n\n" +
                         " - To edit or delete an item, simply touch+hold that item, and choose your option.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
