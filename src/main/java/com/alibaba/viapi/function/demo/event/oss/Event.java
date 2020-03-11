package com.alibaba.viapi.function.demo.event.oss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {
    private String eventName;
    private String eventSource;
    private String eventTime;
    private String eventVersion;
    private Oss oss;
    private String region;
    private RequestParameters requestParameters;
    private ResponseElements responseElements;
    private UserIdentity userIdentity;
}
