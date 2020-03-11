package com.alibaba.viapi.function.demo.function;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.event.oss.Event;
import com.alibaba.viapi.function.demo.event.oss.OSSEvent;
import com.alibaba.viapi.function.demo.event.oss.Oss;
import com.alibaba.viapi.function.demo.pojo.DetectFaceFCRequest;
import com.alibaba.viapi.function.demo.util.OSSUtils;
import com.alibaba.viapi.function.demo.util.SystemUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.aliyuncs.fnf.model.v20190315.StartExecutionRequest;

/**
 * @author benxiang.hhq
 */
public class ImageFileAddTriggerFC extends BasePopFC implements PojoRequestHandler<OSSEvent, String> {

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
    public String handleRequest(OSSEvent ossEvent, Context context) {
        try {
            this.initialize(context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FunctionComputeLogger logger = context.getLogger();
        Event event = ossEvent.getEvents().get(0);

        String ossRegion = event.getRegion();
        Oss oss = event.getOss();
        String ossBucketName = oss.getBucket().getName();
        String ossKey = oss.getObject().getKey();

        String extension = OSSUtils.getExtension(ossKey);
        if (!SUPPORT_EXTENSIONS.contains(extension)) {
            return "ok";
        }

        String flowName = SystemUtils.getStringEnvValue(FLOW_NAME, "viapi-detect-mask-demo-shanghai");
        String outputOssFolderKey = SystemUtils.getStringEnvValue(OUTPUT_DST, "target");
        Long maxImageFileSize = SystemUtils.getLongEnvValue(MAX_IMAGE_FILE_SIZE, 3*1024*1024L);
        StartExecutionRequest request = new StartExecutionRequest();
        request.setFlowName(flowName);

        if ( oss.getObject().getSize() > maxImageFileSize ) {
            return "ok";
        }

        String input = JSON.toJSONString(DetectFaceFCRequest.builder()
                                             .imageOssUrl(OSSUtils.buildOssPath(ossBucketName, ossKey))
                                             .ossRegion(ossRegion)
                                             .outputOssFolderKey(outputOssFolderKey)
                                             .ossBucketName(ossBucketName)
                                             .build());
        request.setInput(input);
        try {
            getAcsResponse(fnfAcsClient, request, logger);
            return "ok";
        } catch (Exception e) {
            throw new RuntimeException(e );
        }
    }


}