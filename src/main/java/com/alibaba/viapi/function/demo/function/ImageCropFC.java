package com.alibaba.viapi.function.demo.function;

import com.alibaba.viapi.function.demo.object.FaceImage;
import com.alibaba.viapi.function.demo.pojo.ImageCropRequest;
import com.alibaba.viapi.function.demo.pojo.ImageCropResponse;
import com.alibaba.viapi.function.demo.util.ImageUtils;
import com.alibaba.viapi.function.demo.util.OSSUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.aliyun.oss.OSSClient;

/**
 * @author benxiang.hhq
 */
public class ImageCropFC implements PojoRequestHandler<ImageCropRequest, ImageCropResponse> {
    @Override
    public ImageCropResponse handleRequest(ImageCropRequest imageCropRequest, Context context) {
        FaceImage faceImage = imageCropRequest.getFaceImage();
        String ossRegion = imageCropRequest.getOssRegion();
        String imageOssPath = imageCropRequest.getImageOssPath();
        OSSClient ossClient = OSSUtils.buildClient(ossRegion, context.getExecutionCredentials());
        try {
            ImageUtils.cutoutPicture(imageOssPath, faceImage, ossClient);
            String imageHttpUrls = OSSUtils.generatePresignedUrl(ossClient, faceImage.getCropOSSUrl(), null);
            return ImageCropResponse.builder().imageHttpUrl(imageHttpUrls).build();
        } catch (Exception e) {
            return ImageCropResponse.builder().imageHttpUrl("").build();
        }
    }
}
