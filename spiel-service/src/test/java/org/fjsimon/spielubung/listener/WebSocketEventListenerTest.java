package org.fjsimon.spielubung.listener;

import org.fjsimon.spielubung.delegate.SpielDelegate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTest {

    @Mock
    private SpielDelegate spielDelegate;

    @InjectMocks
    private WebSocketEventListener testee;

    @Test
    public void handleWebSocketConnectedTest() {

        SessionConnectedEvent event = Mockito.mock(SessionConnectedEvent.class);
        Principal principal = Mockito.mock(Principal.class);
        when(event.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn("name");
        testee.handleWebSocketConnected(event);

        verify(spielDelegate, times(1)).save(any());
    }

    @Test
    public void handleWebSocketDisconnectedTest() {

        SessionDisconnectEvent event = Mockito.mock(SessionDisconnectEvent.class);
        Map headers = new HashMap();
        Map simpSessionAttributes = new HashMap();
        simpSessionAttributes.put("username", "username");
        headers.put("simpSessionAttributes", simpSessionAttributes);

        when(event.getMessage()).thenReturn(new GenericMessage("payload", headers));
        testee.handleWebSocketDisconnected(event);

        verify(spielDelegate, times(1)).remove("username");
    }

}