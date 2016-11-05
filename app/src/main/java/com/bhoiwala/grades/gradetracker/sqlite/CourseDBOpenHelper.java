//package com.bhoiwala.grades.gradetracker.sqlite;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class CourseDBOpenHelper extends SQLiteOpenHelper{
//
//    private static final String LOGTAG = "Logtag";
//
//    private static final String DATABASE_NAME = "courses.db";
//    private static final int DATABASE_VERSION = 1;
//
//    public static final String TABLE_COURSES = "courses";
//    public static final String COLUMN_COURSE = "courseName";
//    public static final String COLUMN_GRADE = "courseGrade";
//
//    private static final String TABLE_CREATE =
//            "CREATE TABLE " + TABLE_COURSES + " (" +
//                    COLUMN_COURSE + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                    COLUMN_GRADE + " NUMERIC " +
//                    ")";
//
//    public CourseDBOpenHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(TABLE_CREATE);
//        Log.i(LOGTAG, "Table has been created");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
//        onCreate(db);
//    }
//}
