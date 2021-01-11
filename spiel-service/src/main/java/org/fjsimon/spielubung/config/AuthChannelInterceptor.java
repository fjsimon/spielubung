package org.fjsimon.spielubung.config;

import lombok.AllArgsConstructor;
import org.fjsimon.spielubung.delegate.SpielerRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final SpielerRepository spielerRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String user = ofNullable(accessor.getFirstNativeHeader("username"))
                    .filter(username -> !StringUtils.isEmpty(username))
                    .orElseThrow(() -> new MessagingException("username is required to establish a connection"));

            checkPlayerDoesNotExist(user);
            accessor.setUser(() -> user);

        }

        return message;
    }

    private void checkPlayerDoesNotExist(String username) {
        if (spielerRepository.existsByName(username)) {
            throwMessagingException(String.format("Player with username %s already connected!!", username));
        }
    }

    private void throwMessagingException(String errorMessage) {

        throw new MessagingException(errorMessage);
    }
}