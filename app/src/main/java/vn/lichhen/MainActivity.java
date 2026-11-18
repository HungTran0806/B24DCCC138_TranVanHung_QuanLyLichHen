package vn.lichhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class MainActivity extends Activity {
    private ArrayAdapter<Appointment> adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
        ListView list = findViewById(R.id.appointments);
        adapter = new ArrayAdapter<Appointment>(this, R.layout.row) {
            @Override public View getView(int position, View row, ViewGroup parent) {
                if (row == null) row = getLayoutInflater().inflate(R.layout.row, parent, false);
                Appointment appointment = getItem(position);
                ((TextView) row.findViewById(R.id.title)).setText(appointment.title);
                ((TextView) row.findViewById(R.id.location)).setText(appointment.location);
                ((TextView) row.findViewById(R.id.when)).setText(appointment.displayTime());
                ((TextView) row.findViewById(R.id.status)).setText(appointment.reminder ? "Có nhắc" : "Không nhắc");
                return row;
            }
        };
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.empty));
        list.setOnItemClickListener((parent, row, position, ignored) ->
                startActivity(new Intent(this, AppointmentDetailActivity.class)
                        .putExtra(AppointmentDetailActivity.EXTRA_ID, adapter.getItem(position).id)));
        findViewById(R.id.add).setOnClickListener(view ->
                startActivity(new Intent(this, AddAppointmentActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(database.getAllAppointments());
            adapter.notifyDataSetChanged();
            ((TextView) findViewById(R.id.total)).setText(database.countAll() + "\nTổng lịch hẹn");
            ((TextView) findViewById(R.id.enabled)).setText(database.countReminders() + "\nCó nhắc nhở");
        }
    }
}
