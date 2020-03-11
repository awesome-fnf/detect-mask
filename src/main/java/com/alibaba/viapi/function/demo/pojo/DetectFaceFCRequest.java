package com.alibaba.viapi.function.demo.pojo;

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
public class DetectFaceFCRequest {
    private String imageOssUrl;
    private String ossBucketName;
    private String ossRegion;
    private String outputOssFolderKey;
}
