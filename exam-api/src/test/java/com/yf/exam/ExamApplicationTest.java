package com.yf.exam;

import com.yf.exam.modules.sys.user.entity.SysRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExamApplicationTest {

    @Autowired
    private RedisTemplate<String, String> strRedisTemplate;
    @Autowired
    private RedisTemplate<String, Serializable> serializableRedisTemplate;

    @Test
    public void testString() {
        strRedisTemplate.opsForValue().set("strKey", "zwqh");
        System.out.println(strRedisTemplate.opsForValue().get("strKey"));
    }

    @Test
    public void testSerializable() {
        SysRole sysRole = new SysRole();
        sysRole.setId("123");
        sysRole.setRoleName("student");
        serializableRedisTemplate.opsForValue().set("sysRole", sysRole);
        SysRole role = (SysRole) serializableRedisTemplate.opsForValue().get("sysRole");
        assert role != null;
        System.out.println(role.toString());
    }

}