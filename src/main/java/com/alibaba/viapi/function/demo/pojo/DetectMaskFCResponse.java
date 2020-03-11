package com.alibaba.viapi.function.demo.pojo;

import com.alibaba.viapi.function.demo.object.FaceImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author benxiang.hhq
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectMaskFCResponse {
    private FaceImage faceImage;
}
