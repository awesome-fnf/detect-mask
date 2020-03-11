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
public class Oss {
    private Bucket bucket;
    private Object object;
    private java.lang.String ossSchemaVersion;
    private java.lang.String ruleId;
}
