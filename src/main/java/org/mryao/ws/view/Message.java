package org.mryao.ws.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @JsonProperty("t")
    private Integer type;

    @JsonProperty("d")
    private String data;
}
