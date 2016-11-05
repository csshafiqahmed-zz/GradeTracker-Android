package com.bhoiwala.grades.gradetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bhoiwala.grades.gradetracker.R;
import com.bhoiwala.grades.gradetracker.realm.Categories;
import com.bhoiwala.grades.gradetracker.realm.Course;

import java.util.ArrayList;

public class CourseAdapter extends ArrayAdapter<Course> {

    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.course_custom_adapter, parent, false);
        }
        // Lookup view for data population
        TextView tvCourseName = (TextView) convertView.findViewById(R.id.tvCourseName);
        TextView tvFinalGrade = (TextView) convertView.findViewById(R.id.tvOverallGrade);
        // Populate the data into the template view using the data object
        tvCourseName.setText(course.className);
        String grade = "--";
        if(course.finalGrade != null && !course.finalGrade.equals("NaN") && !course.finalGrade.equals("0.00")){
            grade = course.finalGrade + "%";
        }
        tvFinalGrade.setText(grade);

        // Return the completed view to render on screen
        return convertView;
    }
}
