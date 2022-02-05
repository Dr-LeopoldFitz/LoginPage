package com.hive.dao;

import com.hive.domain.User;
import com.hive.util.JDBCUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Hive
 * Description:操作数据库中User表的类
 * Date: 2022/2/5 20:34
 */
public class UserDao {
    //声明JDBCTemplate对象供本类中所有方法共用
    // (JdbcTemplate是Spring对JDBC的封装，目的是使JDBC更加易于使用。)
    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    /**
     * 登录方法
     * @param loginUser 只有用户名和密码
     * @return user包含用户全部数据,若没有查询到返回null
     */
    public User login(User loginUser){
        try {
            //1.编写sql
            String sql = "select * from USER where username = ? and password = ?";
            //2.调用query方法
            User user = template.queryForObject(sql,
                    new BeanPropertyRowMapper<User>(User.class),
                    loginUser.getUsername(), loginUser.getPassword());


            return user;
        } catch (DataAccessException e) {
            e.printStackTrace();//记录日志
            return null;
        }
    }
}
