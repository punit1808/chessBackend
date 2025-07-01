package com.chessmaster.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseDto {

    private String Messege;
    private boolean status;
    private LocalDateTime DateAndTime;
}