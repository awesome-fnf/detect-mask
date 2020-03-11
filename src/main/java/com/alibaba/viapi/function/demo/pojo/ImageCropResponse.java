package com.alibaba.viapi.function.demo.pojo;

import com.alibaba.viapi.function.demo.object.ImageEdge;

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
public class ImageCropResponse {
    private ImageEdge faceImage;
}
