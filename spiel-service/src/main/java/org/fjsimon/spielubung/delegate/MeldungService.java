package org.fjsimon.spielubung.delegate;

import lombok.AllArgsConstructor;
import org.fjsimon.spielubung.model.SpielMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MeldungService {

    private static final String UPDATE_QUEUE = "/queue/updates";
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyPlayer(String playerName, SpielMessage message) {
        messagingTemplate.convertAndSendToUser(playerName, UPDATE_QUEUE, message);
    }
}
