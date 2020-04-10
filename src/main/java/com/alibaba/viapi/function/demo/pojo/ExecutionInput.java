package com.alibaba.viapi.function.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yibo.fyb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionInput {
    private String imageOssUrl;
    private String imageHttpUrl;
    private String ossBucketName;
    private String ossRegion;
    private String outputOssFolderKey;
}