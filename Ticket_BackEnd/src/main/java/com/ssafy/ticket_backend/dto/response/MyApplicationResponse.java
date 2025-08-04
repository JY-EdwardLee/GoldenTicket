package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.Waitlist;
import com.ssafy.ticket_backend.model.WaitlistStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApplicationResponse {

    private Long waitlist_id;
    private Long ticketId;
    private WaitlistStatus status;
    private LocalDateTime date;
    private LocalDateTime matchedDate;
    private int price;
    private GameResponse game;

    public void waitlistToMyApplicationResponse(Waitlist waitlist) {
        this.waitlist_id = waitlist.getId();
        this.status = waitlist.getStatus();
        this.date = waitlist.getCreatedAt();
    }
}
