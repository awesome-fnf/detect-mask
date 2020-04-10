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

/**
 * @author benxiang.hhq
 */
public class DetectMaskFC extends BasePopFC implements PojoRequestHandler<DetectMaskFCRequest, String> {

    @Override
    public String handleRequest(DetectMaskFCRequest detectMaskFCRequest, Context context) {
        try {
            this.initialize(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FunctionComputeLogger logger = context.getLogger();
        String imageOssUrl = detectMaskFCRequest.getImageOssUrl();
        OSSClient ossClient = OSSUtils.buildClient(detectMaskFCRequest.getOssRegion(),
                                                   context.getExecutionCredentials());
        String imageHttpUrls = OSSUtils.generatePresignedUrl(ossClient, imageOssUrl, null);

        return imageHttpUrls;
    }

}