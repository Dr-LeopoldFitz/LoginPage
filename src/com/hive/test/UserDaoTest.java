package com.hive.test;

import com.hive.dao.UserDao;
import com.hive.domain.User;
import org.junit.Test;

/**
 * @author Hive
 * Description:
 * Date: 2022/2/5 20:57
 */
public class UserDaoTest {

    @Test
    public void testLogin(){
        User loginuser=new User();
        loginuser.setUsername("Lash");
        loginuser.setPassword("123456");

        UserDao dao = new UserDao();
        User user = dao.login(loginuser);
        System.out.println(user);
    }
}
