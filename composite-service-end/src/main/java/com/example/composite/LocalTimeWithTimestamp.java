package com.example.composite;

import java.time.LocalTime;

public record LocalTimeWithTimestamp(LocalTime localTime, LocalTime timestamp) {
}
