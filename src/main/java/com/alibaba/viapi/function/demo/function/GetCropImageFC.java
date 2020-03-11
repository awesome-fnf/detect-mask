package com.alibaba.viapi.function.demo.function;

import java.util.List;

import com.alibaba.viapi.function.demo.object.FaceImage;
import com.alibaba.viapi.function.demo.pojo.GetCropImageRequest;
import com.alibaba.viapi.function.demo.pojo.GetCropImageResponse;
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
public class GetCropImageFC implements PojoRequestHandler<GetCropImageRequest, GetCropImageResponse> {
    @Override
    public GetCropImageResponse handleRequest(GetCropImageRequest imageCropRequest, Context context) {
        Integer faceIndex = imageCropRequest.getFaceIndex();
        List<FaceImage> detectFaceImageList = imageCropRequest.getDetectMaskImageList();
        FaceImage nextDetectFaceImage = null;
        if (faceIndex >= 0) {
            detectFaceImageList.set(faceIndex, imageCropRequest.getDetectMaskImage());
        }
        faceIndex++;
        if (faceIndex < detectFaceImageList.size()) {
            nextDetectFaceImage = detectFaceImageList.get(faceIndex);
        } else {
            nextDetectFaceImage = new FaceImage();
        }
        return GetCropImageResponse.builder().detectFaceImage(nextDetectFaceImage)
            .detectMaskImageList(detectFaceImageList).faceIndex(faceIndex).build();
    }
}
