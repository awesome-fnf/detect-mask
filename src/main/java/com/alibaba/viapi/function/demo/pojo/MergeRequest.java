package com.alibaba.viapi.function.demo.pojo;

import java.util.List;

import com.alibaba.viapi.function.demo.object.FaceImage;

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
public class MergeRequest {
    private String outputOssFolderKey;
    private String imageOssUrl;
    private String ossBucketName;
    private String ossRegion;
    private Integer faceCount;
    private List<FaceImage> faceImageList;
    private FaceImage singleFaceImage;
}
