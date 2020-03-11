package com.alibaba.viapi.function.demo.pojo;

import java.util.List;

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
public class GetCropImageRequest {
    private List<FaceImage> detectMaskImageList;
    private FaceImage detectMaskImage;
    private Integer faceIndex;
}
