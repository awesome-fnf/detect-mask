package com.alibaba.viapi.function.demo.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.pojo.MergeRequest;
import com.alibaba.viapi.function.demo.pojo.MergeResponse;
import com.alibaba.viapi.function.demo.util.OSSUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;

/**
 * @author benxiang.hhq
 */
public class MergeFC implements StreamRequestHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        ObjectMapper inputMapper=new ObjectMapper();
        inputMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
        MergeRequest imageCropRequest = inputMapper.readValue(inputStream, MergeRequest.class);

        String outputJsonPath = buildOutputKey(imageCropRequest.getImageOssUrl(), imageCropRequest.getOutputOssFolderKey());
        OSSClient ossClient = OSSUtils.buildClient(imageCropRequest.getOssRegion(), context.getExecutionCredentials());
        OSSUtils.put(ossClient, outputJsonPath, JSON.toJSONString(imageCropRequest));

        ObjectMapper outputMapper=new ObjectMapper();//先创建objmapper的对象
        String output = outputMapper.writeValueAsString(MergeResponse.builder().data(outputJsonPath).build());
        outputStream.write(output.getBytes());
        return ;
    }

    private String buildOutputKey(String originalOssUrl, String outputKey) {
        Pair<String, String> bucketAndKey = OSSUtils.parseBucketAndKey(originalOssUrl);
        String key  = bucketAndKey.getValue();
        int i = key.indexOf("/", 1);
        return OSSUtils.buildOssPath(bucketAndKey.getKey(), String.format("%s%s.json", outputKey, key.substring(i)));
    }


}
