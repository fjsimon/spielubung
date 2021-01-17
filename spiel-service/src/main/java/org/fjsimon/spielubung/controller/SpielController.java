package org.fjsimon.spielubung.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fjsimon.spielubung.delegate.SpielDelegate;
import org.fjsimon.spielubung.model.SpielMessage;
import org.fjsimon.spielubung.model.Spielzug;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@AllArgsConstructor
@Controller
public class SpielController {

    private final SpielDelegate spielDelegate;

    @MessageMapping("/start")
    @SendToUser("/queue/notifications")
    public SpielMessage start(Principal principal, SimpMessageHeaderAccessor accessor) {
        accessor.getSessionAttributes().put("username", principal.getName());
        return spielDelegate.start(principal.getName());
    }

    @MessageExceptionHandler
    @SendToUser("/queue/exceptions")
    public String handleExceptions(Throwable throwable) {

        String message = throwable.getMessage();
        log.debug(message);
        return message;
    }

    @MessageMapping("/number")
    public void number(Principal principal, Spielzug spielzug) {

        spielDelegate.number(spielzug.getValue(), principal.getName());
    }

    @MessageMapping("/play")
    public void play(Principal principal, Spielzug spielzug) {

        spielDelegate.move(spielzug, principal.getName());
    }
}
