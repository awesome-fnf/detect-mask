package com.alibaba.viapi.function.demo.event.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author benxiang.hhq
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestParameters {
    private String sourceIPAddress;
}
