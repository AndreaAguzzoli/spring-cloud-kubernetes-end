package com.example.composite;

import java.time.LocalDate;
import java.time.LocalTime;

public record LocalDateWithTimestamp(LocalDate localDate, LocalTime timestamp) {
}
