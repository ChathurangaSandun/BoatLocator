package com.bankfinder.chathurangasandun.boatlocator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bankfinder.chathurangasandun.boatlocator.model.Location;

/**
 * Created by Chathuranga Sandun on 7/27/2016.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
        private static final String DATABASE_NAME= "location.db";

    // marks table name
    public static final String TABLE_LOCATION= "location";

    // marks Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_DATE= "date";
    public static final String KEY_TIME= "time";
    public static final String KEY_LATITUDE= "lat";
    public static final String KEY_LONGITUDE= "lng";
    public static final String KEY_BATRY= "batry";


    //crating table quary
    private static final String CREATE_LOCATION_TABLE =   "CREATE TABLE " +TABLE_LOCATION+" ("
            +KEY_ID+ " INTEGER , "
            +KEY_DATE+" TEXT,"
            +KEY_TIME+" TEXT,"
            +KEY_LATITUDE+" REAL ,"
            +KEY_LONGITUDE+" REAL ,"
            +KEY_BATRY+" INTEGER, PRIMARY KEY ("+ KEY_ID+"))";




    //all columns
    public static final String[] ALL_COLUMNS = {KEY_ID,KEY_DATE,KEY_TIME,KEY_LATITUDE,KEY_LONGITUDE,KEY_BATRY};




    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("table",CREATE_LOCATION_TABLE );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOCATION);
        db.execSQL(CREATE_LOCATION_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

    public void addLocation(Location location) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseOpenHelper.KEY_ID,location.getLocationID() );
        values.put(DatabaseOpenHelper.KEY_DATE, location.getDate());
        values.put(DatabaseOpenHelper.KEY_TIME, location.getTime());
        values.put(DatabaseOpenHelper.KEY_LATITUDE, location.getLat());
        values.put(DatabaseOpenHelper.KEY_LONGITUDE, location.getLng());
        values.put(DatabaseOpenHelper.KEY_BATRY, location.getBatryStatus());


        // Inserting Row
        long insert = db.insert(DatabaseOpenHelper.TABLE_LOCATION, null, values);

        db.close(); // Closing database connection
    }


    public void dropTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE  " + TABLE_LOCATION);

    }

   /* // Getting single contact
    public Marks getMarks(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MARKS,ALL_COLUMNS, KEY_ID + "=?",new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Marks marks = new Marks(Integer.parseInt(cursor.getString(0)),cursor.getString(1), Integer.parseInt(cursor.getString(2)));
        // return contact
        return marks;
    }

    // Getting All Contacts
    public List<Marks> getAllMark() {
        List<Marks> contactList = new ArrayList<Marks>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MARKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Marks mark = new Marks();
                mark.setSid(Integer.parseInt(cursor.getString(0)));
                mark.setName(cursor.getString(1));
                mark.setMark(Integer.parseInt(cursor.getString(2)));
                // Adding contact to list
                contactList.add(mark);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateMark(Marks mark) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, mark.getName());
        values.put(KEY_MARK, mark.getMark());

        // updating row
        return db.update(TABLE_MARKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(mark.getSid()) });
    }

    // Deleting single contact
    public void deleteMark(Marks mark) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MARKS, KEY_ID + " = ?",new String[]{String.valueOf(mark.getSid())});
        db.close();
    }


    // Getting contacts Count
    public int getMarksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MARKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }*/


    public void deleteAllRaws(){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_LOCATION;


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                db.delete(TABLE_LOCATION, KEY_ID + " = ?",new String[]{String.valueOf(id)});

            } while (cursor.moveToNext());
        }



        db.close();
    }



    public  void createdatabase(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOCATION);
        db.execSQL(CREATE_LOCATION_TABLE);

    }

}
