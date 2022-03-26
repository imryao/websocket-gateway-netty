package org.mryao.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    KEY(0, "key"),
    DATA(1, "data");

    private final Integer code;

    private final String name;
}
