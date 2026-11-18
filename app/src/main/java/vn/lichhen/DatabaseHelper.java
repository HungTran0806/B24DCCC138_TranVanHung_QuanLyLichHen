package vn.lichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

final class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context) {
        super(context, "appointments.db", null, 1);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE appointments (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, location TEXT NOT NULL, date TEXT NOT NULL, "
                + "time TEXT NOT NULL, reminder INTEGER NOT NULL CHECK (reminder IN (0, 1)))");
        insertUser(db, "admin", "123456");
        insertAppointment(db, new Appointment(0, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", true));
        insertAppointment(db, new Appointment(0, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", true));
        insertAppointment(db, new Appointment(0, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", false));
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Missing migration from " + oldVersion + " to " + newVersion);
    }

    boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().query("users", new String[]{"id"},
                "username = ? AND password = ?", new String[]{username, password}, null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return db.insertOrThrow("users", null, values);
    }

    long insertAppointment(Appointment appointment) {
        return insertAppointment(getWritableDatabase(), appointment);
    }

    private long insertAppointment(SQLiteDatabase db, Appointment appointment) {
        ContentValues values = new ContentValues();
        values.put("title", appointment.title);
        values.put("location", appointment.location);
        values.put("date", appointment.date);
        values.put("time", appointment.time);
        values.put("reminder", appointment.reminder ? 1 : 0);
        return db.insertOrThrow("appointments", null, values);
    }

    ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> result = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("appointments", null, null, null,
                null, null, "date ASC, time ASC, id ASC")) {
            while (cursor.moveToNext()) result.add(readAppointment(cursor));
        }
        return result;
    }

    Appointment getAppointmentById(long id) {
        try (Cursor cursor = getReadableDatabase().query("appointments", null, "id = ?",
                new String[]{Long.toString(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readAppointment(cursor) : null;
        }
    }

    private Appointment readAppointment(Cursor cursor) {
        return new Appointment(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("title")),
                cursor.getString(cursor.getColumnIndexOrThrow("location")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("reminder")) == 1);
    }

    boolean updateReminder(long id, boolean on) {
        ContentValues values = new ContentValues();
        values.put("reminder", on ? 1 : 0);
        return getWritableDatabase().update("appointments", values, "id = ?",
                new String[]{Long.toString(id)}) == 1;
    }

    boolean deleteAppointment(long id) {
        return getWritableDatabase().delete("appointments", "id = ?", new String[]{Long.toString(id)}) == 1;
    }

    int countAll() {
        return count("SELECT COUNT(*) FROM appointments", null);
    }

    int countReminders() {
        return count("SELECT COUNT(*) FROM appointments WHERE reminder = ?", new String[]{"1"});
    }

    private int count(String sql, String[] arguments) {
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, arguments)) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
    }
}

