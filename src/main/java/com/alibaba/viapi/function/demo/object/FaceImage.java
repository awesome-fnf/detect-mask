package com.alibaba.viapi.function.demo.object;

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
public class FaceImage extends ImageEdge {
    private String cropOSSUrl;
    private Float FaceProbability;
    private Integer Mask;
}
