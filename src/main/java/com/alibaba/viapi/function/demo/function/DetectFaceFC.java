package com.alibaba.viapi.function.demo.function;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.object.FaceImage;
import com.alibaba.viapi.function.demo.pojo.DetectFaceFCRequest;
import com.alibaba.viapi.function.demo.pojo.DetectFaceFCResponse;
import com.alibaba.viapi.function.demo.pojo.ImageCropRequest;
import com.alibaba.viapi.function.demo.util.OSSUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author benxiang.hhq
 */
public class DetectFaceFC extends BasePopFC
    implements PojoRequestHandler<DetectFaceFCRequest, DetectFaceFCResponse> {

    private static String OUT_PUT_FILE_KEY_FORMAT = "%s/images/%s.png";

    @Override
    public DetectFaceFCResponse handleRequest(DetectFaceFCRequest detectFaceFCRequest, Context context) {
        try {
            this.initialize(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FunctionComputeLogger logger = context.getLogger();
        String imageOssUrl = detectFaceFCRequest.getImageOssUrl();
        Pair<String, String> bucketAndKey = OSSUtils.parseBucketAndKey(imageOssUrl);
        if (bucketAndKey.getValue().startsWith(detectFaceFCRequest.getOutputOssFolderKey())) {
            logger.error("output folder is invalid.");
            throw new RuntimeException("output folder is invalid. detectFaceFCRequest=" + JSON.toJSONString(detectFaceFCRequest));
        }

        try {
            return transResult(detectFaceFCRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DetectFaceFCResponse transResult(DetectFaceFCRequest detectMaskFCRequest) {
        return DetectFaceFCResponse.builder()
            .faceCount(detectMaskFCRequest.getFaceCount())
            .detectFaceImageList(transfer(detectMaskFCRequest)).build();
    }

    private List<FaceImage> transfer(DetectFaceFCRequest detectFaceFCRequest) {
        int faceCount = detectFaceFCRequest.getFaceCount();
        List<Integer> faceRectangles = detectFaceFCRequest.getFaceRectangles();
        int index;
        List<FaceImage> list = new ArrayList<>();
        for(int i = 0; i < faceCount; i++) {
            index = 4* i;
            FaceImage faceImage = new FaceImage();
            faceImage.setMainBodyLeftX(faceRectangles.get(index));
            faceImage.setMainBodyLeftY(faceRectangles.get(++index));
            faceImage.setMainBodyWidth(faceRectangles.get(++index));
            faceImage.setMainBodyHeight(faceRectangles.get(++index));
            faceImage.setCropOSSUrl(OSSUtils.buildOssPath(detectFaceFCRequest.getOssBucketName()
                , String.format(OUT_PUT_FILE_KEY_FORMAT, detectFaceFCRequest.getOutputOssFolderKey()
                    , UUID.randomUUID().toString())));

            list.add(faceImage);
        }
        return list;
    }


}