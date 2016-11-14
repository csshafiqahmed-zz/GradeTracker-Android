package com.bhoiwala.grades.gradetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.bhoiwala.grades.gradetracker.adapters.CourseAdapter;
import com.bhoiwala.grades.gradetracker.realm.Categories;
import com.bhoiwala.grades.gradetracker.realm.Course;
import com.bhoiwala.grades.gradetracker.realm.Individual;
import java.text.DecimalFormat;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity {

    final Context context = this;
    ArrayList<Course> listOfClasses;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Courses");
        setSupportActionBar(toolbar);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        FloatingActionButton addClassButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        assert addClassButton != null;
        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_class_popup, null);
                final EditText className = (EditText) promptsView.findViewById(R.id.enteredClassName);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                Boolean editDB = false;
                String courseToEdit = "";
                showPopupMenu(alertDialogBuilder, className, editDB, courseToEdit);
            }
        }); // ~ on click listener
        refreshViews();
    } // ends onCreate

    public void showPopupMenu(AlertDialog.Builder alertDialogBuilder, final EditText className, final Boolean editDB, final String courseToEdit) {
        forceOpenKeyboard();
        className.setSelection(className.getText().length()); // places cursor in the end of the string
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = className.getText().toString();
                        if(!editDB) {
                            saveClassName(input);
                            toast("Course added");
                        }else{
                            editCourseinDB(input, courseToEdit);
                            toast("Course updated");
                        }
                        forceCloseKeyboard(className);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        forceCloseKeyboard(className);
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        className.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Boolean exists = checkIfExists(s.toString());
                if (s.length() >= 1 && !exists) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    if (exists) {
                       toast("Class already exists");
                    }
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        }); // text watcher
    }

    public void refreshViews() {
        printRealmTables();
        RealmResults<Course> courses = realm.where(Course.class).findAll();
        calculateFinalClassGrade(courses);
        listOfClasses = new ArrayList<>();

        for (Course course : courses) {
            listOfClasses.add(course);
        }
        CourseAdapter listAdapter = new CourseAdapter(this, listOfClasses);
        final ListView listView = (ListView) findViewById(R.id.classList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course listItem = (Course) listView.getItemAtPosition(i);
                String classChosen = listItem.className;
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("course", classChosen);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Course listItem = (Course) listView.getItemAtPosition(i);
                String classToChange = listItem.className;
                populateMenuPopup(classToChange);
                return true;
            }
        });
        Log.v("**** REFRESHED 1", " successfully **** ");
    }

    public void calculateFinalClassGrade(RealmResults<Course> courses) {
        for (Course course : courses) {
            RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", course.className).findAll();
            float sum = (float) 0.0;
            for (Categories category : categories) {
                try {
                    sum += (Float.parseFloat(category.categoryAverage));
                }catch (NullPointerException e){
                    sum = 999;  // means that something is wrong
                }
            }
            DecimalFormat df = new DecimalFormat("0.00");
            Course updateCourse = realm.where(Course.class).equalTo("className", course.className).findFirst();
            realm.beginTransaction();
            updateCourse.finalGrade = String.valueOf(df.format(sum));
            realm.commitTransaction();
        }
    }

    public void populateMenuPopup(final String classToChange) {
        String[] items = {"Edit", "Delete"};
        ArrayAdapter<String> menuItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.course_edit_menu_popup, null);
        final ListView menuList = (ListView) promptsView.findViewById(R.id.listViewCourseMenu);
        menuList.setAdapter(menuItems);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menuChosen = (String) menuList.getItemAtPosition(i);
                if(menuChosen.equals("Edit")){
                    editCourse(classToChange);
                }else{
                    deleteCourseinDB(classToChange);
                }
                alertDialog.dismiss();
            }
        });
    }

    public void editCourse(String courseToEdit) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_class_popup, null);
        final EditText courseName = (EditText) promptsView.findViewById(R.id.enteredClassName);
        courseName.setText(courseToEdit);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        Boolean editDB = true;
        showPopupMenu(alertDialogBuilder, courseName, editDB, courseToEdit);
    }

    public void editCourseinDB(final String newClassName, final String courseToEdit){
        Course courseWithOldName = realm.where(Course.class).equalTo("className", courseToEdit).findFirst();
        realm.beginTransaction();
        courseWithOldName.className = newClassName;
        realm.commitTransaction();
        RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", courseToEdit).findAll();
        for (Categories oldCategory : categories){
            if(oldCategory.categoryClass.equals(courseToEdit)){
                String oldParent = courseToEdit + " / " + oldCategory.categoryName;
                realm.beginTransaction();
                oldCategory.categoryClass = newClassName; // old category got updated
                realm.commitTransaction();
                String newParent = newClassName + " / " + oldCategory.categoryName;
                RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", oldParent).findAll();
                for (Individual oldIndividual: individuals){
                    realm.beginTransaction();
                    oldIndividual.parent = newParent;
                    realm.commitTransaction();
                }
            }
        }
        refreshViews();
    }

    public void deleteCourseinDB(final String courseToDelete){
        RealmResults<Course> courses = realm.where(Course.class).equalTo("className", courseToDelete).findAll();
        for (Course course: courses){
            RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", course.className).findAll();
            for( Categories category : categories){
                String parent = category.categoryClass + " / " + category.categoryName;
                RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", parent).findAll();
//                Log.v("Individual - ", individuals.toString());
                realm.beginTransaction();
                individuals.deleteAllFromRealm();
                realm.commitTransaction();
            }
//            Log.v("Categories - ", categories.toString());
            realm.beginTransaction();
            categories.deleteAllFromRealm();
            realm.commitTransaction();
        }
//        Log.v("Courses - ", courses.toString());
        realm.beginTransaction();
        courses.deleteAllFromRealm();
        realm.commitTransaction();
        toast("Course deleted");
        refreshViews();
    }

    public Boolean checkIfExists(String className) {
        RealmQuery<Course> courses = realm.where(Course.class).equalTo("className", className);
        return courses.count() != 0;
    }

    public void saveClassName(final String className) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Course course = realm.createObject(Course.class);
                course.className = className;
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v("**** DATABASE 1 - ", className + " saved ****");
                refreshViews();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.v("**** ERROR 1 - ", error.getMessage());
            }
        });

    }

    public void forceOpenKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void forceCloseKeyboard(EditText editText) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void onResume() {  // After a pause OR at startup
        super.onResume();
        refreshViews();
    }

    public void printRealmTables() {
        RealmResults<Course> courses = realm.where(Course.class).findAll();
        for(Course course : courses){
            Log.v("---- COURSE ", course.className + " - " + course.finalGrade + " ----");
        }
        RealmResults<Categories> categories = realm.where(Categories.class).findAll();
        for(Categories category : categories){
            Log.v("---- CATEGORY ", category.categoryClass + " - " + category.categoryName + " - " + category.categoryWeight + " - " + category.categoryAverage + " ----" );
        }
        RealmResults<Individual> individuals = realm.where(Individual.class).findAll();
        for(Individual individual : individuals){
            Log.v("---- INDIVIDUAL ", individual.parent + " - " + individual.individualCategoryName + " - " + individual.gradeReceived + "/" + individual.maxGradePossible);
        }
    }

} // ends MainActivity
