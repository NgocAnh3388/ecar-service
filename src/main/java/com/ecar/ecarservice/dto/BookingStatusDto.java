package com.ecar.ecarservice.dto;

import com.ecar.ecarservice.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingStatusDto {
    private BookingStatus status;
}