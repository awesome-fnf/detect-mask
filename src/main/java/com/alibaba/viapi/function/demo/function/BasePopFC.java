package com.alibaba.viapi.function.demo.function;

import java.io.IOException;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.Credentials;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;

/**
 * @author benxiang.hhq
 */
public class BasePopFC implements FunctionInitializer {

    protected static IAcsClient viapiAcsClient = null;

    protected static IAcsClient fnfAcsClient = null;

    private static final String DEFAULT_POP_REGION = "cn-shanghai";

    @Override
    public void initialize(Context context) throws IOException {
        FunctionComputeLogger logger = context.getLogger();
        logger.info("start to init viapiAcsClient. requestId=" + context.getRequestId());
        if (viapiAcsClient == null) {
            viapiAcsClient = buildAcsClient(context, DEFAULT_POP_REGION);
        }

        if (fnfAcsClient == null) {
            fnfAcsClient = buildAcsClient(context, DEFAULT_POP_REGION);
        }
    }

    private DefaultAcsClient buildAcsClient(Context context, String region) {
        Credentials executionCredentials = context.getExecutionCredentials();
        DefaultProfile profile = DefaultProfile.getProfile(
            region,
            executionCredentials.getAccessKeyId(),
            executionCredentials.getAccessKeySecret(),
            executionCredentials.getSecurityToken());
        return new DefaultAcsClient(profile);
    }

    protected <R extends RpcAcsRequest<T>, T extends AcsResponse> T getAcsResponse(IAcsClient client, R req,
                                                            FunctionComputeLogger logger) throws Exception {
        try {
            return client.getAcsResponse(req);
        } catch (ServerException e) {
            // 服务端异常
            logger.error(String.format("ServerException: errCode=%s, errMsg=%s", e.getErrCode(), e.getErrMsg()));
            throw e;
        } catch (ClientException e) {
            // 客户端错误
            logger.error(String.format("ClientException: errCode=%s, errMsg=%s", e.getErrCode(), e.getErrMsg()));
            throw e;
        } catch (Exception e) {
            logger.error("Exception:" + e.getMessage());
            throw e;
        }
    }
}
