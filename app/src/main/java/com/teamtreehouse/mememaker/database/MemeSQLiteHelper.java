package com.teamtreehouse.mememaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeSQLiteHelper extends SQLiteOpenHelper {

    //Meme Table functionality
    public static final String MEMES_TABLE = "MEMES";
    public static final String COLUMN_MEME_ASSET = "ASSET";
    public static final String COLUMN_MEME_NAME = "NAME";
    private static String CREATE_MEMES = "CREATE TABLE" + MEMES_TABLE +"("
                                          + BaseColumns._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                                          COLUMN_MEME_ASSET +"TEXT," +
                                          COLUMN_MEME_NAME +"TEXT)";


    //Meme Table Annotations functionality
    public static final String ANNOTATIONS_TABLE = "ANNOTATIONS";
    public static final String COLUMN_ANNOTATION_COLOR = "COLOR";
    public static final String COLUMN_ANNOTATION_X = "X";
    public static final String COLUMN_ANNOTATION_Y = "Y";
    public static final String COLUMN_ANNOTATION_TITLE = "TITLE";
    public static final String COLUMN_FOREIGN_KEY_MEME = "MEME_ID";

    private static final String CREATE_ANNOTATIONS = "CREATE TABLE " + ANNOTATIONS_TABLE + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ANNOTATION_X + " INTEGER, " +
            COLUMN_ANNOTATION_Y + " INTEGER, " +
            COLUMN_ANNOTATION_TITLE + " TEXT, " +
            COLUMN_ANNOTATION_COLOR + " TEXT, " +
            COLUMN_FOREIGN_KEY_MEME + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_FOREIGN_KEY_MEME + ")" +
            "REFERENCES MEMES(_ID))";

    private static final String DB_NAME = "memes.db";
    private static final int DB_VERSION = 1;

    public MemeSQLiteHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MEMES);
        sqLiteDatabase.execSQL(CREATE_ANNOTATIONS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }



}
