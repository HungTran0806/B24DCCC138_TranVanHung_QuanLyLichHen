package vn.lichhen;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public final class AppointmentDetailActivity extends Activity {
    static final String EXTRA_ID = "vn.lichhen.APPOINTMENT_ID";

    private boolean restoringReminder;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.detail);
        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        Appointment appointment;
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            appointment = database.getAppointmentById(id);
        }
        if (appointment == null) {
            showMessage("Không tìm thấy lịch hẹn.");
            finish();
            return;
        }

        ((TextView) findViewById(R.id.title)).setText(appointment.title);
        ((TextView) findViewById(R.id.location)).setText(appointment.location);
        ((TextView) findViewById(R.id.when)).setText(appointment.displayTime());
        Switch reminder = findViewById(R.id.reminder);
        reminder.setSaveEnabled(false);
        reminder.setChecked(appointment.reminder);
        reminder.setText(reminderLabel(appointment.reminder));
        reminder.setOnCheckedChangeListener((button, enabled) -> {
            if (restoringReminder) return;
            try (DatabaseHelper database = new DatabaseHelper(this)) {
                if (database.updateReminder(id, enabled)) {
                    reminder.setText(reminderLabel(enabled));
                } else {
                    showMessage("Không tìm thấy lịch hẹn.");
                    finish();
                }
            } catch (SQLiteException exception) {
                restoringReminder = true;
                reminder.setChecked(!enabled);
                restoringReminder = false;
                showMessage("Không thể cập nhật nhắc nhở.");
            }
        });

        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Xóa lịch hẹn?")
                .setMessage(appointment.title)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, choice) -> deleteAppointment(id))
                .show());
    }

    private void deleteAppointment(long id) {
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            if (!database.deleteAppointment(id)) showMessage("Không tìm thấy lịch hẹn.");
        } catch (SQLiteException exception) {
            showMessage("Không thể xóa lịch hẹn.");
            return;
        }
        finish();
    }

    private String reminderLabel(boolean enabled) {
        return enabled ? "Nhắc nhở · Đang bật" : "Nhắc nhở · Đang tắt";
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
