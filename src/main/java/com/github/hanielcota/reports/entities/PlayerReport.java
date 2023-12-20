package com.github.hanielcota.reports.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class PlayerReport {

    private int id;
    private String nick;
    private LocalDateTime timestamp;
    private String role;
    private String online;
    private String reportedBy;
    private String reason;
}
