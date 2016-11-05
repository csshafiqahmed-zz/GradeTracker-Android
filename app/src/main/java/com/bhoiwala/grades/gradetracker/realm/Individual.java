package com.bhoiwala.grades.gradetracker.realm;

import io.realm.RealmObject;

public class Individual extends RealmObject{
    public String individualCategoryName;
    public String parent;
    public String gradeReceived;
    public String maxGradePossible;

}
