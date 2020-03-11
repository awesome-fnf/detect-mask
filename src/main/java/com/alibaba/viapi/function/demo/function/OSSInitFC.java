package com.alibaba.viapi.function.demo.function;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.pojo.CreateOSSBucketResponse;
import com.alibaba.viapi.function.demo.util.OSSUtils;
import com.alibaba.viapi.function.demo.util.SystemUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang.math.RandomUtils;

/**
 * @author benxiang.hhq
 */
public class OSSInitFC implements StreamRequestHandler {

    private static final String DEFAULT_REGION = "cn-shanghai";
    private static final String SOURCE_DST = "SOURCE_DST";
    private static final String OUTPUT_DST = "OUTPUT_DST";
    private static final String REGION = "REGION";

    private String buildBucketName() {
        return String.format("viapi-func-demo-%s-%s", System.currentTimeMillis(), RandomUtils.nextInt(100));
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        context.getLogger().info("start create bucket");

        String bucketName = buildBucketName();
        String region = SystemUtils.getStringEnvValue(REGION, DEFAULT_REGION);

        OSSClient client = OSSUtils.buildClient(region, context.getExecutionCredentials());
        OSSUtils.createBucket(client, bucketName);
        String targetFolderKey = SystemUtils.getStringEnvValue(OUTPUT_DST, "target");
        String sourceFolderKey = SystemUtils.getStringEnvValue(SOURCE_DST, "source");

        OSSUtils.createFolder(client, bucketName, targetFolderKey);
        OSSUtils.createFolder(client, bucketName, sourceFolderKey);
        CreateOSSBucketResponse response = CreateOSSBucketResponse.builder().bucketName(bucketName)
            .sourceBucketFolder(sourceFolderKey)
            .targetBucketFolder(targetFolderKey)
            .ossRegion(region).build();
        outputStream.write(JSON.toJSONString(response).getBytes("UTF-8"));
    }
}