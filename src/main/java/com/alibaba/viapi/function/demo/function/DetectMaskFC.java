package com.alibaba.viapi.function.demo.function;

import java.io.IOException;

import com.alibaba.viapi.function.demo.object.FaceImage;
import com.alibaba.viapi.function.demo.pojo.DetectMaskFCRequest;
import com.alibaba.viapi.function.demo.pojo.DetectMaskFCResponse;
import com.alibaba.viapi.function.demo.util.OSSUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.facebody.model.v20191230.DetectMaskRequest;
import com.aliyuncs.facebody.model.v20191230.DetectMaskResponse;
import com.aliyuncs.facebody.model.v20191230.DetectMaskResponse.Data;

/**
 * @author benxiang.hhq
 */
public class DetectMaskFC extends BasePopFC implements PojoRequestHandler<DetectMaskFCRequest, DetectMaskFCResponse> {

    @Override
    public DetectMaskFCResponse handleRequest(DetectMaskFCRequest detectMaskFCRequest, Context context) {
        try {
            this.initialize(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FunctionComputeLogger logger = context.getLogger();
        DetectMaskRequest req = new DetectMaskRequest();

        String imageOssUrl = detectMaskFCRequest.getImageOssUrl();
        OSSClient ossClient = OSSUtils.buildClient(detectMaskFCRequest.getOssRegion(),
                                                   context.getExecutionCredentials());
        String imageHttpUrls = OSSUtils.generatePresignedUrl(ossClient, imageOssUrl, null);

        req.setImageURL(imageHttpUrls);
        try {
            DetectMaskResponse resp = getAcsResponse(viapiAcsClient, req, logger);
            Data data = resp.getData();
            FaceImage faceImage = detectMaskFCRequest.getFaceImage();
            faceImage = faceImage == null?new FaceImage() : faceImage;
            faceImage.setFaceProbabilityOfMaskDetect(data.getFaceProbability());
            faceImage.setMask(data.getMask());
            return DetectMaskFCResponse.builder()
                .faceImage(faceImage).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}