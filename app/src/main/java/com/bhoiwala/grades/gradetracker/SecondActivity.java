package com.bhoiwala.grades.gradetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.bhoiwala.grades.gradetracker.adapters.CategoryAdapter;
import com.bhoiwala.grades.gradetracker.realm.Categories;
import com.bhoiwala.grades.gradetracker.realm.Individual;
import java.text.DecimalFormat;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SecondActivity extends AppCompatActivity {

    public static String classChosen = "";
    final Context context = this;
    ArrayList<Categories> listOfCategories;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layer);
        Intent intent = getIntent();
        classChosen = intent.getExtras().getString("course");
        ActionBar ab = getSupportActionBar();
        ab.setTitle(classChosen);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#005af7")));
        ab.setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();
        FloatingActionButton addCategoryButton = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        assert addCategoryButton != null;
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_category_name, null);
                final EditText categoryName = (EditText) promptsView.findViewById(R.id.enteredCategoryName);
                final EditText categoryWeight = (EditText) promptsView.findViewById(R.id.enteredWeight);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                Boolean editDB = false;
                String categoryToEdit = "";
                showPopupMenu(alertDialogBuilder, categoryName, categoryWeight, editDB, categoryToEdit);
            }});
        refreshViews();
    }

    public void refreshViews() {
        RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", classChosen).findAll();
        calculateAverages(categories);
        listOfCategories = new ArrayList<>();
        for (Categories category: categories){
            listOfCategories.add(category);
        }
        CategoryAdapter listAdapter2 = new CategoryAdapter(this, listOfCategories);
        final ListView listView = (ListView) findViewById(R.id.categoryList);
        listView.setAdapter(listAdapter2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Categories listItem = (Categories) listView.getItemAtPosition(i);
                String categoryChosen = classChosen + " / " + listItem.categoryName;
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("category", categoryChosen);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Categories listItem = (Categories) listView.getItemAtPosition(i);
                String categoryToChange = listItem.categoryName;
                String weightToChange = listItem.categoryWeight;
                populateMenuPopup(categoryToChange, weightToChange);
                return true;
            }
        });
    }

    public void showPopupMenu(AlertDialog.Builder alertDialogBuilder, final EditText categoryName, final EditText categoryWeight, final Boolean editDB, final String categoryToEdit){
        forceOpenKeyboard();
        categoryName.setSelection(categoryName.getText().length()); // places cursor in the end of the string
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = categoryName.getText().toString();
                        String weight = categoryWeight.getText().toString();
                        if(!editDB){
                            saveCategoryName(input, weight);
                            toast("Category added");
                        }else{
                            editCategoryinDB(input, weight, categoryToEdit);
                            toast("Category updated");
                        }

                        forceCloseKeyboard(categoryWeight);
                        dialog.cancel();
//                        Toast.makeText(getApplicationContext(), "You entered: " + input, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        forceCloseKeyboard(categoryWeight);
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        if(!editDB) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
        final Boolean[] goodToGo = {editDB};
        final Boolean[] goodToGo2 = {editDB};
        categoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.v("AFTER categoryName - ", s.toString());
                Boolean exists = checkIfExists(s.toString());
                if(categoryName.getText().toString().equals(categoryToEdit)){
                    exists = false;
                }
                if (s.length() >= 1 && !exists) {
                    goodToGo[0] = true;
                } else {
                    if(exists){toast("Category already exists");}
                    goodToGo[0] = false;
                }
                if(goodToGo[0] && goodToGo2[0]){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        }); // text watcher
        categoryWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(editDB) s = categoryWeight.getText();
                goodToGo2[0] = s.length() >= 1;
                if(goodToGo[0] && goodToGo2[0]){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        }); // text watcher

    }
    public void populateMenuPopup(final String categoryToChange, final String weightToChange) {
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
                    editCategory(categoryToChange, weightToChange);
                }else{
                    deleteCategoryinDB(categoryToChange);
                }
                alertDialog.dismiss();
            }
        });

    }
    public void editCategory(String categoryToEdit, String weightToEdit){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_category_name, null);
        final EditText categoryName = (EditText) promptsView.findViewById(R.id.enteredCategoryName);
        final EditText categoryWeight = (EditText) promptsView.findViewById(R.id.enteredWeight);
        categoryName.setText(categoryToEdit);
        categoryWeight.setText(weightToEdit);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        Boolean editDB = true;
        showPopupMenu(alertDialogBuilder, categoryName, categoryWeight, editDB, categoryToEdit);
    }

    public void editCategoryinDB(String newCategoryName, String newWeight, String currentCategory){
        RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", classChosen).findAll();
        for (Categories category : categories){
            if(category.categoryName.equals(currentCategory)){
                String parent = category.categoryClass + " / " + category.categoryName;
                realm.beginTransaction();
                category.categoryName = newCategoryName;
                category.categoryWeight = newWeight;
                realm.commitTransaction();
                String newParent = category.categoryClass + " / " + category.categoryName;
                RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", parent).findAll();
                for (Individual individual: individuals){
                    realm.beginTransaction();
                    individual.parent = newParent;
                    realm.commitTransaction();
                }
            }
        }
        refreshViews();
    }

    public void deleteCategoryinDB(String categoryName){
        RealmResults<Categories> categories = realm.where(Categories.class).equalTo("categoryClass", classChosen).equalTo("categoryName", categoryName).findAll();
        for( Categories category : categories){
            String parent = category.categoryClass + " / " + categoryName;
            RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", parent).findAll();
            realm.beginTransaction();
            individuals.deleteAllFromRealm();
            realm.commitTransaction();
        }
        realm.beginTransaction();
        categories.deleteAllFromRealm();
        realm.commitTransaction();
        toast("Category deleted");
        refreshViews();
    }


    public void calculateAverages(RealmResults<Categories> categories) {
        for (Categories category: categories){
            Log.d("categories: ", String.valueOf(categories));
            Log.d("category name: ", category.categoryName);
            String parent = classChosen + " / " + category.categoryName;
            RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", parent).findAll();
            Log.d("individuals: ", String.valueOf(individuals));
            float sum = (float) 0.0;
            for (Individual individual: individuals){
                sum += (Float.parseFloat(individual.gradeReceived) / Float.parseFloat(individual.maxGradePossible));
            }
            float weight = (Float.parseFloat(category.categoryWeight));
            float average = (sum/individuals.size()) * weight;
            DecimalFormat df = new DecimalFormat("#.00");
            Categories updateCategory = realm.where(Categories.class).equalTo("categoryName", category.categoryName).findFirst();
            realm.beginTransaction();
            updateCategory.categoryAverage = String.valueOf(df.format(average));
            realm.commitTransaction();
        }
    }
    public Boolean checkIfExists(String categoryName){
        RealmQuery<Categories> category = realm.where(Categories.class).equalTo("categoryClass", classChosen).equalTo("categoryName", categoryName);
        return category.count() != 0;
    }

    public void saveCategoryName(final String usercategoryName, final String categoryWeight){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Categories category = realm.createObject(Categories.class);
                category.categoryName = usercategoryName;
                category.categoryClass = classChosen;
                category.categoryWeight = categoryWeight;
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v("Database 2", "Stored ok");
                refreshViews();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.v("Error", error.getMessage());
            }
        });

    }

    public void forceOpenKeyboard(){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void forceCloseKeyboard(EditText editText){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        refreshViews();
    }

}
