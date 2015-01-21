package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.util.ArrayList;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeDatasource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {

        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper(context);
    }
    private SQLiteDatabase open(){
        return mMemeSqlLiteHelper.getWritableDatabase();
    }

    private void close(SQLiteDatabase database){
        database.close();
    }

    public void delete(int memeId){
        SQLiteDatabase database = open();
        database.beginTransaction();
        //implementation
        database.delete(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        String.format("%s=%s", MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, String.valueOf(memeId)),null);
        database.delete(MemeSQLiteHelper.MEMES_TABLE,
                String.format("%s=%s", BaseColumns._ID, String.valueOf(memeId)),null);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public ArrayList<Meme> read(){
        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);
        return memes;

    }

    public ArrayList<Meme> readMemes(){
        SQLiteDatabase database = open();

        Cursor cursor =database.query(
                MemeSQLiteHelper.MEMES_TABLE,
                new String[]{MemeSQLiteHelper.COLUMN_MEME_NAME, BaseColumns._ID, MemeSQLiteHelper.COLUMN_MEME_ASSET},
                null,//selection
                null,//selection args
                null,//group by
                null,//having
                null);//order
        ArrayList<Meme> memes = new ArrayList<Meme>();
        if (cursor.isFirst()){
            do {
                Meme meme = new Meme(getIntColumnName(cursor, BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_ASSET),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_NAME),
                        null);
                memes.add(meme);
            }while (cursor.moveToNext());

        }
        cursor.close();
        close(database);
        return memes;

    }

    public void addMemeAnnotations(ArrayList<Meme> memes){
        SQLiteDatabase database = open();

        for (Meme meme: memes){
            ArrayList<MemeAnnotation> annotations = new ArrayList<MemeAnnotation>();
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE + " WHERE MEME_ID =" +
                            meme.getId(),null);

            if (cursor.moveToFirst()){
                do{
                MemeAnnotation annotation = new MemeAnnotation(
                        getIntColumnName(cursor,BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE),
                        getIntColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                        getIntColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y));
                annotations.add(annotation);
                }while (cursor.moveToNext());


            }
            meme.setAnnotations(annotations);
            cursor.close();
        }
        database.close();

    }

    public void update(Meme meme){
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateMemesValues = new ContentValues();
        updateMemesValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        database.update(MemeSQLiteHelper.MEMES_TABLE,
                updateMemesValues,
                String.format("%s=%d", BaseColumns._ID, meme.getId()), null);
        for (MemeAnnotation annotation: meme.getAnnotations()){
            ContentValues updateAnnotations = new ContentValues();
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, annotation.getId());
            updateAnnotations.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());

            if (annotation.hasBeenSaved()){
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                                updateAnnotations,
                                 String.format("%s=%d", BaseColumns._ID, annotation.getId()),
                                 null);
            } else {
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE, null, updateAnnotations);
            }

        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);

    }


    private int getIntColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }


    public void create(Meme meme){
        SQLiteDatabase database = open();
        database.beginTransaction();
        ContentValues memesValues = new ContentValues();
        memesValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        memesValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET, meme.getAssetLocation());
        long memeID = database.insert(MemeSQLiteHelper.MEMES_TABLE, null, memesValues);

        for(MemeAnnotation annotation : meme.getAnnotations()){
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeID);

            database.insert(MemeSQLiteHelper.MEMES_TABLE, null, annotationValues);

        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }
}
