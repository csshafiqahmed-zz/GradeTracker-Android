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
import com.bhoiwala.grades.gradetracker.adapters.IndividualAdapter;
import com.bhoiwala.grades.gradetracker.realm.Individual;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;

public class ThirdActivity extends AppCompatActivity {

    public static String categoryChosen = "";
    final Context context = this;
    ArrayList<Individual> listOfIndividuals;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_layer);
        Intent intent = getIntent();
        categoryChosen = intent.getExtras().getString("category");
        String[] titles = categoryChosen.split(" / ");
        String title = titles[0];
        String subtitle = titles[1];
        ActionBar ab = getSupportActionBar();
        ab.setTitle(title);
        ab.setSubtitle(subtitle);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5e00f7")));
        ab.setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();
        FloatingActionButton addIndividualButton = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        assert addIndividualButton != null;
        addIndividualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_individual_name, null);
                final EditText individualName = (EditText) promptsView.findViewById(R.id.enteredIndividualName);
                final EditText gradeReceived = (EditText) promptsView.findViewById(R.id.enteredUserGrade);
                final EditText maxPoints = (EditText) promptsView.findViewById(R.id.enteredMaxPoints);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);
                Boolean editDB = false;
                String individualNameToEdit = "";
                showPopupMenu(alertDialogBuilder,individualName,gradeReceived,maxPoints, editDB, individualNameToEdit);
            }});

        refreshViews();
    }

    public void showPopupMenu(AlertDialog.Builder alertDialogBuilder, final EditText individualName, final EditText gradeReceived, final EditText maxPoints, final Boolean editDB, final String individualNameToEdit){
        forceOpenKeyboard();
        individualName.setSelection(individualName.getText().length());
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = individualName.getText().toString();
                        String grade = gradeReceived.getText().toString();
                        String totalPossible = maxPoints.getText().toString();
                        if(!editDB) {
                            saveIndividualName(name, grade, totalPossible);
                            toast("Assignment added");
                        }else{
                            editIndividualinDB(name, grade, totalPossible, individualNameToEdit);
                            toast("Assignment updated");
                        }
                        forceCloseKeyboard(individualName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        forceCloseKeyboard(individualName);
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
        final Boolean[] goodToGo3 = {editDB};
        individualName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                goodToGo[0] = s.length() >= 1;
                if(goodToGo[0] && goodToGo2[0] && goodToGo3[0]){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        gradeReceived.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                goodToGo2[0] = s.length() >= 1;
                if(goodToGo[0] && goodToGo2[0] && goodToGo3[0]){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });
        maxPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                goodToGo3[0] = s.length() >= 1;
                if(goodToGo[0] && goodToGo2[0] && goodToGo3[0]){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else{
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

    }
    public void populateMenuPopup(final String individualToChange, final String gradeReceivedToChange, final String maxPointsToChange) {
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
                    editIndividual(individualToChange,gradeReceivedToChange, maxPointsToChange);
                }else{
                    deleteIndividualinDB(individualToChange);
                }
                alertDialog.dismiss();
            }
        });
    }


    public void editIndividual(String individualToEdit, String gradeReceivedToEdit, String maxPointsToEdit){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.add_individual_name, null);
        final EditText individualName = (EditText) promptsView.findViewById(R.id.enteredIndividualName);
        final EditText gradeReceived = (EditText) promptsView.findViewById(R.id.enteredUserGrade);
        final EditText maxPoints = (EditText) promptsView.findViewById(R.id.enteredMaxPoints);
        individualName.setText(individualToEdit);
        gradeReceived.setText(gradeReceivedToEdit);
        maxPoints.setText(maxPointsToEdit);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        Boolean editDB = true;
        showPopupMenu(alertDialogBuilder, individualName, gradeReceived, maxPoints, editDB, individualToEdit);
    }

    public void editIndividualinDB(String name, String grade, String totalPossible, String individualNameToEdit){
        RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", categoryChosen).findAll();
        for(Individual individual: individuals){
            if(individual.individualCategoryName.equals(individualNameToEdit)){
                realm.beginTransaction();
                individual.individualCategoryName = name;
                individual.gradeReceived = grade;
                individual.maxGradePossible = totalPossible;
                realm.commitTransaction();
            }
        }
        refreshViews();
    }

    public void deleteIndividualinDB(String individualName){
        RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", categoryChosen).equalTo("individualCategoryName", individualName).findAll();
        realm.beginTransaction();
        individuals.deleteAllFromRealm();
        realm.commitTransaction();
        toast("Assignment deleted");
        refreshViews();
    }

    public void refreshViews() {
        RealmResults<Individual> individuals = realm.where(Individual.class).equalTo("parent", categoryChosen).findAll();
        listOfIndividuals = new ArrayList<>();
        for (Individual individual: individuals){
            listOfIndividuals.add(individual);
        }
        IndividualAdapter individualAdapter = new IndividualAdapter(this, listOfIndividuals);
        final ListView listView = (ListView) findViewById(R.id.individualList);
        listView.setAdapter(individualAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Individual listItem = (Individual) listView.getItemAtPosition(i);
                String individualToChange = listItem.individualCategoryName;
                String gradeReceivedToChange = listItem.gradeReceived;
                String maxPointsToChange = listItem.maxGradePossible;
                populateMenuPopup(individualToChange, gradeReceivedToChange, maxPointsToChange);
                return true;
            }
        });
        Log.v("**** REFRESHED 3", " successfully **** ");
    }

    public void saveIndividualName(final String individualName, final String grade, final String totalPossible){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Individual individual = realm.createObject(Individual.class);
                individual.individualCategoryName = individualName;
                individual.parent = categoryChosen;
                individual.gradeReceived = grade;
                individual.maxGradePossible = totalPossible;
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.v("**** DATABASE 3 - ", individualName + "-" + grade + "/" + totalPossible + " saved ****");
                refreshViews();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.v("**** ERROR 3 - ", error.getMessage());
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
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void toast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
