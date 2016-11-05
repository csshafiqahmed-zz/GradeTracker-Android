package com.bhoiwala.grades.gradetracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bhoiwala.grades.gradetracker.R;
import com.bhoiwala.grades.gradetracker.realm.Individual;

import java.util.ArrayList;

public class IndividualAdapter extends ArrayAdapter<Individual> {


        public IndividualAdapter(Context context, ArrayList<Individual> individuals) {
            super(context, 0, individuals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Individual individual = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.individual_custom_adapter, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvGrade = (TextView) convertView.findViewById(R.id.tvGrade);
            // Populate the data into the template view using the data object
            tvName.setText(individual.individualCategoryName);
            String grade = individual.gradeReceived + "/" + individual.maxGradePossible;
            tvGrade.setText(grade);
            // Return the completed view to render on screen
            return convertView;
        }

}

