package com.goodstudy.learning.service;

import com.goodstudy.base.model.RestResponse;

/**
 * @author Mr.M
 * @version 1.0
 * @description 在线学习相关的接口
 * @date 2023/2/28 10:17
 */
public interface LearningService {

    /**
     * @description 获取教学视频
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     * @return com.xuecheng.base.model.RestResponse<java.lang.String>
     * @author Mr.M
     * @date 2022/10/5 9:08
     */
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);

}
