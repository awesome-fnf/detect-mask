package com.alibaba.viapi.function.demo.pojo;

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
public class ImageCropRequest {
    private String imageOssPath;
    private String ossRegion;
    private String ossBucketName;
    private FaceImage faceImage;
}
