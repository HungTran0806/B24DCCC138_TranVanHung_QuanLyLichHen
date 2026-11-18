package vn.lichhen;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class AddAppointmentActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.add);
        EditText title = findViewById(R.id.title);
        EditText location = findViewById(R.id.location);
        DatePicker date = findViewById(R.id.date);
        TimePicker time = findViewById(R.id.time);
        time.setIs24HourView(true);
        Switch reminder = findViewById(R.id.reminder);
        TextView error = findViewById(R.id.error);
        findViewById(R.id.save).setOnClickListener(view -> {
            date.clearFocus();
            time.clearFocus();
            error.setVisibility(View.GONE);
            String name = title.getText().toString().trim();
            String place = location.getText().toString().trim();
            if (name.isEmpty()) title.setError("Nhập tên lịch hẹn.");
            if (place.isEmpty()) location.setError("Nhập địa điểm.");
            if (name.isEmpty() || place.isEmpty()) return;

            LocalDate selectedDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDayOfMonth());
            LocalTime selectedTime = LocalTime.of(time.getHour(), time.getMinute());
            if (!selectedDate.atTime(selectedTime).isAfter(LocalDateTime.now())) {
                error.setText("Chọn thời điểm sau thời điểm hiện tại.");
                error.setVisibility(View.VISIBLE);
                return;
            }

            String storedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT));
            try (DatabaseHelper database = new DatabaseHelper(this)) {
                database.insertAppointment(new Appointment(0, name, place, selectedDate.toString(),
                        storedTime, reminder.isChecked()));
            } catch (SQLiteException exception) {
                error.setText("Không thể lưu lịch hẹn. Vui lòng thử lại.");
                error.setVisibility(View.VISIBLE);
                return;
            }
            finish();
        });
    }
}
