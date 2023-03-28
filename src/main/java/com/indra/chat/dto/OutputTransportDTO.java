package com.indra.chat.dto;

import com.indra.chat.utils.TransportActionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutputTransportDTO {

    private TransportActionEnum action;

    private Object object;
}