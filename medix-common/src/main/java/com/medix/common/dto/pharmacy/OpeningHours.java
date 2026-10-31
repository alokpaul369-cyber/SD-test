package com.medix.common.dto.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Opening hours for one day of the week. Times are local pharmacy time ("HH:mm").
 * If {@code closesAt} is before {@code opensAt} the pharmacy closes after midnight.
 *
 * @param day      day of week, e.g. "MONDAY"
 * @param opensAt  opening time; null when closed
 * @param closesAt closing time; null when closed
 * @param closed   true if closed all day
 */
public record OpeningHours(
        @NotNull DayOfWeek day,
        LocalTime opensAt,
        LocalTime closesAt,
        boolean closed
) {

    public static OpeningHours open(DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
        return new OpeningHours(day, opensAt, closesAt, false);
    }

    public static OpeningHours closedOn(DayOfWeek day) {
        return new OpeningHours(day, null, null, true);
    }

    /** Open days need both times, and they must differ. */
    @JsonIgnore
    @AssertTrue(message = "Open days need different opening and closing times")
    public boolean isTimesValid() {
        return closed || (opensAt != null && closesAt != null && !opensAt.equals(closesAt));
    }

    /** True if the pharmacy is open at {@code time} on this day (ignores spill-over from the previous day). */
    public boolean isOpenAt(LocalTime time) {
        if (closed || opensAt == null || closesAt == null) {
            return false;
        }
        if (closesAt.isAfter(opensAt)) {
            return !time.isBefore(opensAt) && time.isBefore(closesAt);
        }
        return !time.isBefore(opensAt) || time.isBefore(closesAt);
    }
}
