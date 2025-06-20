package com.goodstudy.learning.service.impl;

import com.goodstudy.base.model.RestResponse;
import com.goodstudy.content.model.po.CoursePublish;
import com.goodstudy.learning.feignclient.ContentServiceClient;
import com.goodstudy.learning.feignclient.MediaServiceClient;
import com.goodstudy.learning.model.dto.XcCourseTablesDto;
import com.goodstudy.learning.service.LearningService;
import com.goodstudy.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.M
 * @version 1.0
 * @description 在线学习接口
 * @date 2023/2/28 10:20
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {

        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        //判断如果为null不再继续
        if(coursepublish == null){
            return RestResponse.validfail("课程不存在");
        }

        //远程调用内容管理服务根据课程计划id（teachplanId）去查询课程计划信息，如果is_preview的值为1表示支持试学
        //也可以从coursepublish对象中解析出课程计划信息去判断是否支持试学
        //todo:如果支持试学调用媒资服务查询视频的播放地址，返回

        //用户已登录
        if(StringUtils.isNotEmpty(userId)){
            //获取学习资格
            XcCourseTablesDto learningStatus = myCourseTablesService.getLearningStatus(userId, courseId);
            //学习资格，[{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
            String learnStatus = learningStatus.getLearnStatus();
            if("702002".equals(learnStatus)){
                return RestResponse.validfail("无法学习，因为没有选课或选课后没有支付");
            }else if("702003".equals(learnStatus)){
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            }else{
                //有资格学习，要返回视频的播放地址
                //程调用媒资获取视频播放地址
                RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
                return playUrlByMediaId;

            }

        }
        //如果用户没有登录
       //取出课程的收费规则
        String charge = coursepublish.getCharge();
        if("201000".equals(charge)){
            //有资格学习，要返回视频的播放地址
            //远程调用媒资获取视频播放地址
            RestResponse<String> playUrlByMediaId = mediaServiceClient.getPlayUrlByMediaId(mediaId);
            return playUrlByMediaId;
        }
        return RestResponse.validfail("课程需要购买");
    }
}
