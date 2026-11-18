package vn.lichhen;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class Appointment {
    final long id;
    final String title, location, date, time;
    final boolean reminder;

    Appointment(long id, String title, String location, String date, String time, boolean reminder) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    String displayTime() {
        return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT))
                + " · " + time;
    }
}
