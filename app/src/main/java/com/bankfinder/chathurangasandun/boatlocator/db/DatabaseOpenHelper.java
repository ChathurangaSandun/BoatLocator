package com.bankfinder.chathurangasandun.boatlocator.db;

/**
 * Created by Chathuranga Sandun on 7/27/2016.
 */
public class DatabaseopenHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "marks.db";

    // marks table name
    public static final String TABLE_MARKS = "marks";

    // marks Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MARK= "mark";

    //crating table quary
    private static final String CREATE_MARKS_TABLE =   "CREATE TABLE " +TABLE_MARKS+" ("+KEY_ID+ " INTEGER PRIMARY KEY, " +KEY_NAME+" TEXT,"+KEY_MARK+" INTEGER)";

    //all columns
    public static final String[] ALL_COLUMNS = {KEY_ID,KEY_NAME,KEY_MARK};




    public DatabaseopenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_MARKS);
        db.execSQL(CREATE_MARKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKS);
        onCreate(db);
    }

    public void addMark(Marks marks) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseopenHelper.KEY_NAME,marks.getName() );
        values.put(DatabaseopenHelper.KEY_MARK, marks.getMark());

        // Inserting Row
        db.insert(DatabaseopenHelper.TABLE_MARKS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
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
    }

}
