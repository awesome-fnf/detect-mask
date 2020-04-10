package com.alibaba.viapi.function.demo.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.pojo.ExecutionInput;
import com.alibaba.viapi.function.demo.util.OSSUtils;
import com.alibaba.viapi.function.demo.util.SystemUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.event.OSSEvent;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.fnf.model.v20190315.StartExecutionRequest;

/**
 * @author benxiang.hhq
 */
public class ImageFileAddTriggerFC extends BasePopFC implements StreamRequestHandler {

    private static final String FLOW_NAME = "FLOW_NAME";
    private static final String OUTPUT_DST = "OUTPUT_DST";
    private static final String MAX_IMAGE_FILE_SIZE = "MAX_IMAGE_FILE_SIZE";

    private static final List<String> SUPPORT_EXTENSIONS;
    static {
        SUPPORT_EXTENSIONS = new CopyOnWriteArrayList<>();
        SUPPORT_EXTENSIONS.add("jpg");
        SUPPORT_EXTENSIONS.add("png");
        SUPPORT_EXTENSIONS.add("jpeg");
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        try {
            this.initialize(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper mapper=new ObjectMapper();
        OSSEvent ossEvent = mapper.readValue(inputStream, OSSEvent.class);

        FunctionComputeLogger logger = context.getLogger();
        OSSEvent.Event event = ossEvent.events[0];

        String ossRegion = event.region;
        OSSEvent.Event.Oss oss = event.oss;
        String ossBucketName = oss.bucket.name;
        String ossKey = oss.object.key;

        String extension = OSSUtils.getExtension(ossKey);
        if (!SUPPORT_EXTENSIONS.contains(extension)) {
            return ;
        }

        String flowName = SystemUtils.getStringEnvValue(FLOW_NAME, "viapi-detect-mask-demo-shanghai");
        String outputOssFolderKey = SystemUtils.getStringEnvValue(OUTPUT_DST, "target");
        Long maxImageFileSize = SystemUtils.getLongEnvValue(MAX_IMAGE_FILE_SIZE, 3*1024*1024L);
        StartExecutionRequest request = new StartExecutionRequest();
        request.setFlowName(flowName);

        if ( oss.object.size > maxImageFileSize ) {
            return ;
        }

        OSSClient ossClient = OSSUtils.buildClient(ossRegion,context.getExecutionCredentials());
        String imageOssUrl = OSSUtils.buildOssPath(ossBucketName, ossKey);
        String input = JSON.toJSONString(ExecutionInput.builder()
                                             .imageOssUrl(imageOssUrl)
                                             .ossRegion(ossRegion)
                                             .outputOssFolderKey(outputOssFolderKey)
                                             .ossBucketName(ossBucketName)
                                             .imageHttpUrl(OSSUtils.generatePresignedUrl(ossClient, imageOssUrl, null))
                                             .build());
        request.setInput(input);
        try {
            getAcsResponse(fnfAcsClient, request, logger);
            return ;
        } catch (Exception e) {
            throw new RuntimeException(e );
        }
    }


}