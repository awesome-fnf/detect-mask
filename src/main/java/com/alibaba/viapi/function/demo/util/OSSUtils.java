package com.alibaba.viapi.function.demo.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.aliyun.fc.runtime.Credentials;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author benxiang.hhq
 */
public class OSSUtils {
    public static final int OSS_PROTOCOL_HEADER_LENGTH = 6;
    public static final String SYMBOL_SLASH = "/";
    private static final String OSS_FILE_PATTERN = "oss://%s/%s";

    public static String buildOssPath(String bucket, String key) {
        return String.format(OSS_FILE_PATTERN, bucket, key);
    }


    public static void createFolder(OSSClient client, String bucketName, String folderName) {
        ObjectMetadata objectMeta = new ObjectMetadata();
        byte[] buffer = new byte[0];
        InputStream in = new ByteArrayInputStream(buffer);
        objectMeta.setContentLength(0);
        client.putObject(bucketName, folderName + "/", in, objectMeta);
    }

    public static String put(OSSClient client, String ossPath, InputStream inputStream, ObjectMetadata metadata) {
        try {
            Pair<String, String> bucketAndKey = parseBucketAndKey(ossPath);
            client.putObject(bucketAndKey.getKey(), bucketAndKey.getValue(), inputStream, metadata);
        } catch (Exception e) {
            //logger.error(String.format("put file fail,bucket=%s objectKey=%s, localFilePath=%s."
            //    , bucket, key , e.getMessage()), e);
            return null;
        }
        return ossPath;
    }

    public static String put(OSSClient client, String ossPath, String data) {
        try {
            Pair<String, String> bucketAndKey = parseBucketAndKey(ossPath);
            ObjectMetadata objectMetadata = getObjectMetadata(ossPath);
            InputStream inputStream = new ByteArrayInputStream(data.getBytes("UTF-8"));
            client.putObject(bucketAndKey.getKey(), bucketAndKey.getValue(), inputStream, objectMetadata);
        } catch (Exception e) {
            //logger.error(String.format("put file fail,bucket=%s objectKey=%s, localFilePath=%s."
            //    , bucket, key , e.getMessage()), e);
            return null;
        }
        return ossPath;
    }

    private static ObjectMetadata getObjectMetadata(String fileKey) {
        ObjectMetadata metadata = null;
        String contentType = getContentType(fileKey);
        if (StringUtils.isNotEmpty(contentType)) {
            metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
        }
        return metadata;
    }

    public static String put(OSSClient client, String bucket, String key, InputStream inputStream, ObjectMetadata metadata) {
        try {
            client.putObject(bucket, key, inputStream, metadata);
        } catch (Exception e) {
            //logger.error(String.format("put file fail,bucket=%s objectKey=%s, localFilePath=%s."
            //    , bucket, key , e.getMessage()), e);
            return null;
        }
        return buildOssPath(bucket, key);
    }

    public static void createBucket(OSSClient client, String bucketName) {
        client.createBucket(bucketName);
    }

    public static OSSClient buildClient(String region, Credentials creds) {
        String endpoint = String.format("oss-%s.aliyuncs.com", region);
        return new OSSClient(
            endpoint, creds.getAccessKeyId(), creds.getAccessKeySecret(), creds.getSecurityToken());
    }

    public static InputStream getInputStreamWithStyle(OSSClient client, String ossPath, String style) throws OSSException, ClientException {
        Pair<String, String> bucketAndKey = parseBucketAndKey(ossPath);
        return getInputStreamWithStyle(client, bucketAndKey.getKey(), bucketAndKey.getValue(), style);
    }

    public static InputStream getInputStreamWithStyle(OSSClient client, String bucket, String key, String style) throws OSSException, ClientException {
        InputStream inputStream = null;
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
            if (StringUtils.isNoneBlank(style)) {
                getObjectRequest.setProcess(style);
            }
            inputStream = client.getObject(getObjectRequest).getObjectContent();
            return org.apache.commons.io.IOUtils.toBufferedInputStream(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                IOUtils.safeClose(inputStream);
            }
        }
    }


    /**
     * 获取临时访问节点http
     * @param ossPath
     * @return
     */
    public static String generatePresignedUrl(OSSClient client, String ossPath, String imgProcess) {
        Pair<String, String> bucketAndKey = parseBucketAndKey(ossPath);
        String temporaryUrl = "";
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketAndKey.getKey(), bucketAndKey.getValue());
        request.setExpiration(DateUtils.addDays(new Date(), 1));
        if (StringUtils.isNoneBlank(imgProcess)) {
            request.setProcess(imgProcess);
        }
        temporaryUrl = client.generatePresignedUrl(request).toString();
        return temporaryUrl;
    }

    public static Pair<String,String> parseBucketAndKey(String ossPath) {
        ossPath = ossPath.substring(OSS_PROTOCOL_HEADER_LENGTH);
        int separateIndex = ossPath.indexOf(SYMBOL_SLASH);
        String bucketName = ossPath.substring(0, separateIndex);
        String objectKey = ossPath.substring(separateIndex + 1);
        return ImmutablePair.of(bucketName, objectKey);
    }


    private static final Map<String,String> CONTENT_TYPE_MAP;
    static {
        Map<String,String> TMP_CONTENT_TYPE_MAP = new HashMap<>();
        TMP_CONTENT_TYPE_MAP.put("json","application/json;charset=UTF-8");
        TMP_CONTENT_TYPE_MAP.put("xls","application/x-excel");
        TMP_CONTENT_TYPE_MAP.put("doc","application/msword");
        TMP_CONTENT_TYPE_MAP.put("ppt","application/vnd.ms-powerpoint");
        TMP_CONTENT_TYPE_MAP.put("movie","video/x-sgi-movie");
        TMP_CONTENT_TYPE_MAP.put("avi","avi");
        TMP_CONTENT_TYPE_MAP.put("rgb","image/x-rgb");
        TMP_CONTENT_TYPE_MAP.put("png","image/png");
        TMP_CONTENT_TYPE_MAP.put("jpe","image/jpeg");
        TMP_CONTENT_TYPE_MAP.put("jpg","image/jpeg");
        TMP_CONTENT_TYPE_MAP.put("jpeg","image/jpeg");
        TMP_CONTENT_TYPE_MAP.put("gif","image/gif");
        TMP_CONTENT_TYPE_MAP.put("bmp","image/bmp");
        TMP_CONTENT_TYPE_MAP.put("wav","audio/x-wav");
        TMP_CONTENT_TYPE_MAP.put("ra","audio/x-realaudio");
        TMP_CONTENT_TYPE_MAP.put("rm","audio/x-pn-realaudio");
        TMP_CONTENT_TYPE_MAP.put("mp3","audio/mpeg");
        TMP_CONTENT_TYPE_MAP.put("mpga","audio/mpeg");
        TMP_CONTENT_TYPE_MAP.put("pdf","application/pdf");
        CONTENT_TYPE_MAP = new ConcurrentHashMap<>();
        // 防止上面的key写入非小写字母
        for(Entry<String,String> entry : TMP_CONTENT_TYPE_MAP.entrySet()) {
            CONTENT_TYPE_MAP.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }

    private static final String DOT = ".";

    /**
     * 根据文件名，获取文件类型，注意：这个并不能获取到严格意义上的文件类型，比如拓展名是乱改的等情况下时
     */
    private static String getContentType(String fileKey) {
        int lastIndex = fileKey.lastIndexOf(DOT);
        if (lastIndex != -1) {
            String ext = fileKey.substring(lastIndex + 1).toLowerCase();
            return CONTENT_TYPE_MAP.get(ext);
        }
        return null;
    }


    public static String getExtension(String fileKey) {
        int lastIndex = fileKey.lastIndexOf(DOT);
        if (lastIndex != -1) {
            return fileKey.substring(lastIndex + 1).toLowerCase();
        }
        return null;
    }


}
