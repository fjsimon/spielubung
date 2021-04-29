package org.fjsimon.spielubung.controller;

import org.fjsimon.spielubung.delegate.MeldungService;
import org.fjsimon.spielubung.delegate.SpielDelegate;
import org.fjsimon.spielubung.delegate.SpielerRepository;
import org.fjsimon.spielubung.expections.ApplicationException;
import org.fjsimon.spielubung.model.SpielMessage;
import org.fjsimon.spielubung.model.SpielStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpielControllerSpringBootTest {

    @LocalServerPort
    private int port;

    @MockBean
    private SpielDelegate spielDelegate;

    @MockBean
    private SpielerRepository spielerRepository;

    @MockBean
    private MeldungService meldungService;

    private WebSocketStompClient stompClient;

    private  String url;

    private CompletableFuture<Object> completableFuture;

    @BeforeEach
    public void setup() {
        url = String.format("ws://localhost:%d/spielubung", port);

        stompClient = new WebSocketStompClient(new SockJsClient(
            Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

        stompClient.setTaskScheduler(new ConcurrentTaskScheduler());

        completableFuture = new CompletableFuture<>();
    }

    @Test
    public void when_username_no_specified_then_exception() throws Exception {
        // When & Then
        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> {
                stompClient.connect(url, new StompSessionHandlerAdapter() {
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        assertThat(headers.getFirst("message"))
                                .isEqualTo("username is required to establish a connection");
                    }
                }).get();
            });
    }

    @Test
    public void when_username_already_exist_then_exception() throws Exception {
        // given
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("username", "Chapulin");

        given(spielerRepository.existsByName(anyString()))
                .willReturn(true);

        assertThatExceptionOfType(ExecutionException.class)
            .isThrownBy(() -> {
                stompClient.connect(url, new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandlerAdapter() {
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        assertThat(headers.getFirst("message")).isEqualTo("Player with username already connected!!");
                    }
                }).get();
            });

        verify(spielerRepository).existsByName(anyString());
    }

    @Test
    public void start_endpoint_should_call_delegate() throws Exception {

        // given
        given(spielDelegate.start(anyString()))
                .willReturn(SpielMessage.builder().spielStatus(SpielStatus.WARTEN).build());

        // when
        StompSession stompSession = createSession(new MappingJackson2MessageConverter());

        stompSession.subscribe("/user/queue/notifications", new TestStompFrameHandler(SpielMessage.class));
        stompSession.send("/app/start", null);

        // then
        SpielMessage message = (SpielMessage) completableFuture.get(3, TimeUnit.SECONDS);

        assertThat(message.getSpielStatus()).isEqualTo(SpielStatus.WARTEN);
    }

    @Test
    public void when_exception_occurs_then_publish_exceptions_queue() throws Exception {
        // given
        given(spielDelegate.start(anyString()))
                .willThrow(new ApplicationException("could not find player"));

        // when
        StompSession stompSession = createSession(new StringMessageConverter());
        stompSession.subscribe("/user/queue/exceptions", new TestStompFrameHandler(String.class));
        stompSession.send("/app/start", null);

        // then
        String errorMessage = (String) completableFuture.get(3, TimeUnit.SECONDS);
        assertThat(errorMessage).isEqualTo("could not find player");
    }

//    public void random_number_endpoint_should_delegate() throws Exception {
//
//        StompSession stompSession = createSession(new MappingJackson2MessageConverter());
//        Spielzug spielzug = Spielzug.builder().value(10).build();
//
//        stompSession.setAutoReceipt(true);
//        stompSession.send("/app/number", spielzug);
//
//    }
//
//    public void play_endpoint_should_delegate() throws Exception {
//
//        StompSession stompSession = createSession(new MappingJackson2MessageConverter());
//        Spielzug spielzug = Spielzug.builder().value(12).move(0).build();
//
//        stompSession.setAutoReceipt(true);
//        stompSession.send("/app/play", spielzug);
//    }

    private StompSession createSession(MessageConverter messageConverter) throws Exception {

        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("username", "Tripaseca");
        stompClient.setMessageConverter(messageConverter);
        return stompClient.connect(url, new WebSocketHttpHeaders(), stompHeaders, new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            exception.printStackTrace();
            }
        }).get(1, TimeUnit.SECONDS);
    }

    class TestStompFrameHandler implements StompFrameHandler {

        private final Class<?> aClass;
        public TestStompFrameHandler(Class aClass) {
            this.aClass = aClass;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return aClass;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete(payload);
        }
    }
}