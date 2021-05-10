package org.fjsimon.spielubung.delegate;

import org.fjsimon.spielubung.model.SpielMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeldungServiceTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private MeldungService testee;

    @Test
    public void notifyPlayer_should_call_convertAndSendToUser(){

        SpielMessage message = new SpielMessage();
        testee.notifyPlayer("player", message);

        verify(simpMessagingTemplate, times(1))
                .convertAndSendToUser(eq("player"), eq("/queue/notifications"), eq(message));
    }
}