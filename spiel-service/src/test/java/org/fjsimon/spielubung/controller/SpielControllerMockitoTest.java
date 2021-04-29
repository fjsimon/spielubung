package org.fjsimon.spielubung.controller;


import org.fjsimon.spielubung.delegate.SpielDelegate;
import org.fjsimon.spielubung.model.SpielMessage;
import org.fjsimon.spielubung.model.Spielzug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.security.Principal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringControllerMockitoTest {


    @Mock
    private SpielDelegate spielDelegate;

    @InjectMocks
    private SpielController testee;

    @Test
    public void start_endpoint_should_delegate() throws Exception {

        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");
        SimpMessageHeaderAccessor simpMessageHeaderAccessor = Mockito.mock(SimpMessageHeaderAccessor.class);

        when(spielDelegate.start(eq("name"))).thenReturn(new SpielMessage());

        SpielMessage message = testee.start(principal, simpMessageHeaderAccessor);

        assertThat(message, is(notNullValue()));

        verify(spielDelegate, times(1)).start(eq("name"));
    }

    @Test
    public void handleExceptions_should_delegate() throws Exception {

        RuntimeException e = new RuntimeException("message");
        assertThat(testee.handleExceptions(e), is("message"));
    }

    @Test
    public void random_number_endpoint_should_delegate() throws Exception {

        Spielzug spielzug = Spielzug.builder().value(10).build();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");
        testee.number(principal, spielzug);

        verify(spielDelegate, times(1)).number(eq(10), eq("name"));
    }

    @Test
    public void play_endpoint_should_delegate() throws Exception {

        Spielzug spielzug = Spielzug.builder().value(10).move(1).build();
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("name");
        testee.play(principal, spielzug);

        verify(spielDelegate, times(1)).move(any(Spielzug.class), eq("name"));
    }


}
