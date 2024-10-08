package com.example.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class CompositeController {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeController.class);
    private WebClient webClient;

    @Value("${service.date-service.url}")
    String urlDate;

    @Value("${service.time-service.url}")
    String urlTime;

    public CompositeController(WebClient.Builder builder) {
        webClient = builder.build();
    }

    @GetMapping(value = "/datetime")
    public Mono<LocalDateTimeWithTimestamp> dateTime() throws InterruptedException {

        LOG.info("Calling time API on URL: {}", urlTime);
        Mono<LocalTimeWithTimestamp> localTimeMono = webClient.get().uri(urlTime).retrieve()
                .bodyToMono(LocalTime.class)
                .map(time -> new LocalTimeWithTimestamp(time, LocalTime.now()));

        LOG.info("Calling time API on URL: {}", urlDate);
        Mono<LocalDateWithTimestamp> localDateMono = webClient.get().uri(urlDate).retrieve()
                .bodyToMono(LocalDate.class)
                .map(date -> new LocalDateWithTimestamp(date, LocalTime.now()));

        return Mono.zip(localDateMono, localTimeMono,
                (localDate, localTime) -> new LocalDateTimeWithTimestamp(localDate, localTime));
    }
}
