package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyResponse {
    private String message;
    private Object object;

    public MyResponse(Object object, String message) {
        this.message = message;
        this.object = object;
    }
}
