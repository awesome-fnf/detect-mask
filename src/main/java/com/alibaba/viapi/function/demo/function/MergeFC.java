package com.alibaba.viapi.function.demo.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.viapi.function.demo.pojo.MergeRequest;
import com.alibaba.viapi.function.demo.pojo.MergeResponse;
import com.alibaba.viapi.function.demo.util.OSSUtils;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.PojoRequestHandler;
import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author benxiang.hhq
 */
public class MergeFC implements PojoRequestHandler<MergeRequest, MergeResponse> {

    @Override
    public MergeResponse handleRequest(MergeRequest imageCropRequest, Context context) {
        String outputJsonPath = buildOutputKey(imageCropRequest.getImageOssUrl(), imageCropRequest.getOutputOssFolderKey());
        OSSClient ossClient = OSSUtils.buildClient(imageCropRequest.getOssRegion(), context.getExecutionCredentials());
        OSSUtils.put(ossClient, outputJsonPath, JSON.toJSONString(imageCropRequest));
        return MergeResponse.builder().data(outputJsonPath).build();
    }

    private String buildOutputKey(String originalOssUrl, String outputKey) {
        Pair<String, String> bucketAndKey = OSSUtils.parseBucketAndKey(originalOssUrl);
        String key  = bucketAndKey.getValue();
        int i = key.indexOf("/", 1);
        return OSSUtils.buildOssPath(bucketAndKey.getKey(), String.format("%s%s.json", outputKey, key.substring(i)));
    }


}
