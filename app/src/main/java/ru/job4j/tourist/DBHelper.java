package ru.job4j.tourist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    SQLiteDatabase dbRead = getReadableDatabase();
    SQLiteDatabase dbWrite = getWritableDatabase();
    private static DBHelper mInstance;

    public static final String DB = "locations.db";
    public static final int VERSION = 1;
    private Context context;

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBSchema.LocationsTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DBSchema.LocationsTable.NAME);
        onCreate(db);
    }

    public List<Loc> getAllLocations() {
        List<Loc> result = new ArrayList<>();
        Cursor cursor = dbRead.query(
                DBSchema.LocationsTable.NAME,
                null, null, null,
                null, null, null
        );
        if (cursor.moveToFirst()) {
            do {
                result.add(new Loc(
                        cursor.getInt(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.ID)),
                        cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.NAME)),
                        Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LATITUDE))),
                        Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LONGITUDE)))
                ));
            } while (cursor.moveToNext());
        }
        return result;
    }


    public void addLocation(Loc loc) {
        ContentValues value = new ContentValues();
        value.put(DBSchema.LocationsTable.Cols.NAME, loc.getName());
        value.put(DBSchema.LocationsTable.Cols.LATITUDE, String.valueOf(loc.getLatitude()));
        value.put(DBSchema.LocationsTable.Cols.LONGITUDE, String.valueOf(loc.getLongitude()));
        dbWrite.insert(DBSchema.LocationsTable.NAME, null, value);
    }

    public Loc getLocation(int id) {
        Loc result = null;
        String selectionExam = "id =?";
        String[] selectionArgsExam = new String[]{String.valueOf(id)};
        Cursor cursor = dbRead.query(
                DBSchema.LocationsTable.NAME,
                null, selectionExam, selectionArgsExam,
                null, null, null
        );
        cursor.moveToFirst();
        result = new Loc(
                cursor.getInt(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.ID)),
                cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.NAME)),
                Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LATITUDE))),
                Double.valueOf(cursor.getString(cursor.getColumnIndex(DBSchema.LocationsTable.Cols.LONGITUDE)))
        );
        cursor.close();
        return result;
    }

    public void upadateLocation(Loc loc) {
        ContentValues value = new ContentValues();
        value.put(DBSchema.LocationsTable.Cols.NAME, loc.getName());
        value.put(DBSchema.LocationsTable.Cols.LATITUDE, String.valueOf(loc.getLatitude()));
        value.put(DBSchema.LocationsTable.Cols.LONGITUDE, String.valueOf(loc.getLongitude()));
        dbWrite.update(DBSchema.LocationsTable.NAME, value, "id =?",
                new String[]{String.valueOf(loc.getId())});

    }

    public void deleteLocation(Loc loc) {
        dbWrite.delete(DBSchema.LocationsTable.NAME, "id = ?", new String[]{String.valueOf(loc.getId())});
    }

    public void deleteAllLocation() {
        dbWrite.execSQL("delete from " + DBSchema.LocationsTable.NAME);
    }

}
