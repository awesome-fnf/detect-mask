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
public class CreateOSSBucketResponse {
    private String bucketName;
    private String ossRegion;
    private String sourceBucketFolder;
    private String targetBucketFolder;
}
