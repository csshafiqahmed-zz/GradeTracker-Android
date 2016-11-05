package com.bhoiwala.grades.gradetracker.realm;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Course extends RealmObject {


    public String className;
    public String finalGrade;
}
