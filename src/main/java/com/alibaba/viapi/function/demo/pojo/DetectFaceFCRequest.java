package com.alibaba.viapi.function.demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author benxiang.hhq
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectFaceFCRequest {
    private Integer faceCount;
    private String imageOssUrl;
    private List<Integer> faceRectangles;
    private String ossBucketName;
    private String outputOssFolderKey;
}
