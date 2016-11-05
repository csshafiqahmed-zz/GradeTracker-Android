package com.bhoiwala.grades.gradetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bhoiwala.grades.gradetracker.R;
import com.bhoiwala.grades.gradetracker.realm.Categories;
import com.bhoiwala.grades.gradetracker.realm.Individual;

import java.util.ArrayList;


public class CategoryAdapter extends ArrayAdapter<Categories> {


    public CategoryAdapter(Context context, ArrayList<Categories> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Categories categories = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.categories_custom_adapter, parent, false);
        }
        // Lookup view for data population
        TextView tvCategoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
        TextView tvWeight = (TextView) convertView.findViewById(R.id.tvWeight);
        TextView tvAverage = (TextView) convertView.findViewById(R.id.tvAverage);
        // Populate the data into the template view using the data object
        tvCategoryName.setText(categories.categoryName);
        String weight = categories.categoryWeight + "%";
        tvWeight.setText(weight);
        String average = "--";
        if(categories.categoryAverage != null && !categories.categoryAverage.equals("NaN") && !categories.categoryAverage.equals("0.00")) {
         average = categories.categoryAverage + "%"; //TODO calculate averages and set it to categories.average
        }
        tvAverage.setText(average); // TODO and then uncomment this one

//        tvAverage.setText("--"); //TODO delete this line after calculating averages

        // Return the completed view to render on screen
        return convertView;
    }

}
