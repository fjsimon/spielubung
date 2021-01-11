package org.fjsimon.spielubung.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fjsimon.spielubung.model.Spieler;
import org.fjsimon.spielubung.delegate.SpielDelegate;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Slf4j
@Component
public class WebSocketEventListener {

    private final SpielDelegate spielDelegate;

    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        log.debug("player connected: {}", event.getUser().getName());
        spielDelegate.save(new Spieler(event.getUser().getName()));
    }


    @EventListener
    public void handleWebSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        ofNullable(accessor.getSessionAttributes().get("username"))
                .map(String.class::cast)
                .ifPresent(username -> {
                    log.debug("player disconnected: {}", username);
                    spielDelegate.remove(username);
                });
    }
}