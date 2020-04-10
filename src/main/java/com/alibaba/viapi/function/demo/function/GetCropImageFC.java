package com.alibaba.viapi.function.demo.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.alibaba.viapi.function.demo.object.FaceImage;
import com.alibaba.viapi.function.demo.pojo.GetCropImageRequest;
import com.alibaba.viapi.function.demo.pojo.GetCropImageResponse;

import com.alibaba.viapi.function.demo.pojo.MergeResponse;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author benxiang.hhq
 */
public class GetCropImageFC implements StreamRequestHandler {
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        ObjectMapper inputMapper=new ObjectMapper();
        inputMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
        GetCropImageRequest imageCropRequest = inputMapper.readValue(inputStream, GetCropImageRequest.class);

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

        ObjectMapper outputMapper=new ObjectMapper();//先创建objmapper的对象
        String output = outputMapper.writeValueAsString(GetCropImageResponse.builder().detectFaceImage(nextDetectFaceImage)
                .detectMaskImageList(detectFaceImageList).faceIndex(faceIndex).build());
        outputStream.write(output.getBytes());
        return ;
    }
}
