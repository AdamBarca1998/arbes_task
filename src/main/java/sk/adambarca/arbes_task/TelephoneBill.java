package sk.adambarca.arbes_task;

import java.time.LocalDateTime;

public record TelephoneBill(
        long phoneNumber,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
}
