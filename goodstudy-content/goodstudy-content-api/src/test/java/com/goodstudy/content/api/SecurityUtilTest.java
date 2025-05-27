package com.goodstudy.content.api;

import com.goodstudy.content.api.SecurityUtil.XcUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    @Test
    void testGetUser_MockPath() {
        XcUser user = SecurityUtil.getUser();

        assertNotNull(user);
        assertEquals("1", user.getId());
        assertEquals("zhangsan", user.getUsername());

        // 全字段默认值断言（非必须，但提高覆盖度）
        assertNull(user.getPassword());
        assertNull(user.getSalt());
        assertNull(user.getName());
        assertNull(user.getNickname());
        assertNull(user.getWxUnionid());
        assertNull(user.getCompanyId());
        assertNull(user.getUserpic());
        assertNull(user.getUtype());
        assertNull(user.getBirthday());
        assertNull(user.getSex());
        assertNull(user.getEmail());
        assertNull(user.getCellphone());
        assertNull(user.getQq());
        assertNull(user.getStatus());
        assertNull(user.getCreateTime());
        assertNull(user.getUpdateTime());
    }
}
