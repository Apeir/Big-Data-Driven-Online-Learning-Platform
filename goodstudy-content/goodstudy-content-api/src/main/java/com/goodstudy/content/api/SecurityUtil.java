package com.goodstudy.content.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/24 11:26
 */
@Slf4j
public class SecurityUtil {

    public static XcUser getUser() {
//        try {
//            //拿 到当前用户身份
//            Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            if (principalObj instanceof String) {
//                //取出用户身份信息
//                String principal = principalObj.toString();
//                //将json转成对象
//                XcUser user = JSON.parseObject(principal, XcUser.class);
//                return user;
//            }
//        } catch (Exception e) {
//            log.error("获取当前登录用户身份出错:{}", e.getMessage());
//            e.printStackTrace();
//        }
//
//        return null;
        // 模拟登录用户，仅用于开发测试
        XcUser mockUser = new XcUser();
        mockUser.setId("1");
        mockUser.setUsername("zhangsan");
        return mockUser;
    }


    @Data
    public static class XcUser implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id;

        private String username;

        private String password;

        private String salt;

        private String name;
        private String nickname;
        private String wxUnionid;
        private String companyId;
        /**
         * 头像
         */
        private String userpic;

        private String utype;

        private LocalDateTime birthday;

        private String sex;

        private String email;

        private String cellphone;

        private String qq;

        /**
         * 用户状态
         */
        private String status;

        private LocalDateTime createTime;

        private LocalDateTime updateTime;


    }


}
