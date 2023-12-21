package com.kousenit.claudeai;

import java.time.LocalDate;

public record Person(String firstName,
                     String lastName,
                     String origin,
                     LocalDate dob) {
}
