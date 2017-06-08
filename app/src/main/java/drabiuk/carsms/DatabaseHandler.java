package drabiuk.carsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = drabiuk.carsms.MainActivity.getNameOfDB();

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_GROUPS = "groups";

    // Contacts Table Columns names
    private static final String CONTACTS_KEY_ID = "id";
    private static final String CONTACTS_KEY_NAME = "name";
    private static final String CONTACTS_KEY_PH_NO = "phone_number";
    private static final String CONTACTS_KEY_GROUP_ID = "gid";

    private static final String GROUPS_KEY_ID = "id";
    private static final String GROUPS_KEY_NAME = "name";
    private static final String GROUPS_KEY_MSG = "msg";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + CONTACTS_KEY_ID + " INTEGER PRIMARY KEY," + CONTACTS_KEY_NAME + " TEXT,"
                + CONTACTS_KEY_PH_NO + " TEXT," + CONTACTS_KEY_GROUP_ID + " INTEGER"
                + ")";


        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
                + GROUPS_KEY_ID + " INTEGER PRIMARY KEY," + GROUPS_KEY_NAME + " TEXT,"
                + GROUPS_KEY_MSG + " TEXT" + ")";
        db.execSQL(CREATE_GROUPS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        // Create tables again
        onCreate(db);
    }

    public String GetMessageForPhoneNumber(String phonenumber) {
        int GID = 1;
        String msg = "";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{CONTACTS_KEY_ID, CONTACTS_KEY_NAME, CONTACTS_KEY_PH_NO, CONTACTS_KEY_GROUP_ID}, CONTACTS_KEY_PH_NO + "=?", new String[]{phonenumber}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            GID = Integer.parseInt(cursor.getString(3));
        } else return "NUMBER_NOT_IN_CONTACT_LIST";
        cursor.close();
        cursor = db.query(TABLE_GROUPS, new String[]{GROUPS_KEY_ID, GROUPS_KEY_NAME, GROUPS_KEY_MSG}, GROUPS_KEY_ID + "=?", new String[]{String.valueOf(GID)}, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            msg = cursor.getString(2);
        }
        cursor.close();
        return msg;
    }

    public void deleteDB() {
        File fdelete = new File("/data/data/" + drabiuk.carsms.MainActivity.getNameOfPackage() + "/databases/" + DATABASE_NAME);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("", "Deleting DB!");
            } else {
                Log.d("", "Not deleting DB!");
            }
        }
    }

    // Adding new contact
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACTS_KEY_NAME, contact.getName()); // Contact Name
        values.put(CONTACTS_KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
        values.put(CONTACTS_KEY_GROUP_ID, contact.getGroupID()); // Groups

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    void addGroup(ObjectGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GROUPS_KEY_NAME, group.getName()); // Contact Name
        values.put(GROUPS_KEY_MSG, group.getMsg()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_GROUPS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{CONTACTS_KEY_ID,
                        CONTACTS_KEY_NAME, CONTACTS_KEY_PH_NO, CONTACTS_KEY_GROUP_ID}, CONTACTS_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
        // return contact
        cursor.close();
        return contact;
    }

    ObjectGroup getGroup(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GROUPS, new String[]{GROUPS_KEY_ID,
                        GROUPS_KEY_NAME, GROUPS_KEY_MSG}, GROUPS_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ObjectGroup group = new ObjectGroup(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        cursor.close();
        return group;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contact.setGroupID(Integer.parseInt(cursor.getString(3)));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return contactList;
    }


    public List<ObjectGroup> getAllGroups() {
        List<ObjectGroup> GroupList = new ArrayList<ObjectGroup>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GROUPS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ObjectGroup contact = new ObjectGroup();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setMsg(cursor.getString(2));
                // Adding contact to list
                GroupList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        // return contact list
        return GroupList;
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACTS_KEY_NAME, contact.getName());
        values.put(CONTACTS_KEY_PH_NO, contact.getPhoneNumber());
        values.put(CONTACTS_KEY_GROUP_ID, contact.getGroupID());

        // updating row
        return db.update(TABLE_CONTACTS, values, CONTACTS_KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
    }


    public int updateGroup(ObjectGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GROUPS_KEY_NAME, group.getName());
        values.put(GROUPS_KEY_MSG, group.getMsg());

        // updating row
        return db.update(TABLE_GROUPS, values, GROUPS_KEY_ID + " = ?",
                new String[]{String.valueOf(group.getID())});
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, CONTACTS_KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getID())});
        db.close();
    }


    public void deleteGroup(ObjectGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, GROUPS_KEY_ID + " = ?",
                new String[]{String.valueOf(group.getID())});
        db.close();
    }

    public void deleteGroup(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPS, GROUPS_KEY_ID + " = ?", new String[]{String.valueOf(ID)});
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int items = cursor.getInt(0);
        cursor.close();

        // return count
        return items;
    }

    public int getGroupCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_GROUPS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int items = cursor.getInt(0);
        cursor.close();

        // return count
        return items;
    }

}