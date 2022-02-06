# 实例：登陆界面


## 案例需求

1.编写`login.html`登录页面：username & password 两个输入框
2.使用Druid数据库连接池技术,操作mysql，Data_Hive数据库中user表
3.使用JdbcTemplate技术封装JDBC
4.登录成功跳转到SuccessServlet展示：登录成功！用户名,欢迎您
5.登录失败跳转到FailServlet展示：登录失败，用户名或密码错误



## 分析

1. 创建项目，导入html页面，配置文件，jar包 (不要忘记将lib添加到库！)
2. 创建数据库环境

数据库：`Data_Hive`      表：`USER `

```sql
sudo service mysql start
mysql -u root -p
CREATE DATABASE Data_Hive;
USE Data_Hive;
CREATE TABLE USER(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(32) UNIQUE NOT NULL,
	password VARCHAR(32) NOT NULL
);
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/2462170d2a4f450d831d2f2094d03b0e.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)


3. 创建包 `com.hive.domain` , 创建类`User`

```java
package com.hive.domain;

/**
 * @author Hive
 * Description:用户的实体类
 * Date: 2022/2/5 20:26
 */
public class User {
    private int id;
    private String username;
    private String password; //Alt+Ins 生成 GetterSetter toString

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

```

接下来一般先写操作数据库逻辑的代码，再写`Servlet`

4.创建包`com.hive..util`,编写工具类`JDBCUtils`

```java
package com.hive.Dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Hive
 * Description:JDBC工具类 使用Durid连接池
 * Date: 2022/2/5 20:37
 */
public class JDBCUtils {
    private static DataSource ds;

    static {

        try {
            //1.加载配置文件
            Properties pro = new Properties();
            //使用ClassLoader加载配置文件，获取字节输入流
            InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties");
            pro.load(is);

            //2.初始化连接池对象
            ds = DruidDataSourceFactory.createDataSource(pro);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接池对象
     */
    public static DataSource getDataSource() {
        return ds;
    }


    /**
     * 获取连接Connection对象
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

```





5. 创建包`com.hive.dao`,创建类`UserDao`,提供`login`方法

*DAO(Data Access Object)*  数据访问对象，是一个面向对象的数据库接口

```java
package com.hive.Dao;

import com.hive.domain.User;
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

```



写完本类后即测试，创建`com.hive.test`包 创建`UserDaoTest`类测试，养成良好习惯

```java
package com.hive.test;

import com.hive.Dao.UserDao;
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
        loginuser.setUsername("Hive");
        loginuser.setPassword("12345");

        UserDao dao = new UserDao();
        User user = dao.login(loginuser);
        System.out.println(user);
    }
}

```

![在这里插入图片描述](https://img-blog.csdnimg.cn/d7f1839aa5ae4b2cb1f9e17faf2c23fb.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)
测试通过



6. 编写`com.hive.web.servlet.LoginServlet`类

![在这里插入图片描述](https://img-blog.csdnimg.cn/523753bcd19f4ba8896aa3c8d4d30b32.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)

![在这里插入图片描述](https://img-blog.csdnimg.cn/d6dd710b384849fead3f4653da56d939.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)

```java
package com.hive.web.servlet;

import com.hive.dao.UserDao;
import com.hive.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hive
 * Description:
 * Date: 2022/2/5 21:37
 */
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.设置编码
        req.setCharacterEncoding("utf-8");
        //2.获取请求参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        //3.封装user对象
        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);

        //4.调用UserDao的login方法
        UserDao dao = new UserDao();
        User user = dao.login(loginUser);

        //5.判断user
        if(user == null){
            //登录失败,转发
            req.getRequestDispatcher("/failServlet").forward(req,resp);
        }else{
            //登录成功
            //存储数据于request域中，用于转发后调用
            req.setAttribute("user",user);
            //转发
            req.getRequestDispatcher("/successServlet").forward(req,resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}

```



7. 编写`FailServlet`和`SuccessServlet`类

```java
package com.hive.web.servlet;

import com.hive.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hive
 * Description:
 * Date: 2022/2/5 21:59
 */
@WebServlet("/successServlet")
public class SuccessServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取request域中共享的user对象
        User user = (User) request.getAttribute("user");

        if(user != null){
            //给页面写一句话

            //设置编码
            response.setContentType("text/html;charset=utf-8");
            //输出
            response.getWriter().write("登录成功！"+user.getUsername()+",欢迎您");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}


```

```java
package com.hive.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hive
 * Description:
 * Date: 2022/2/5 21:59
 */
@WebServlet("/failServlet")
public class FailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //给页面写一句话

        //设置编码
        response.setContentType("text/html;charset=utf-8");
        //输出
        try {
            response.getWriter().write("登录失败，用户名或密码错误");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}


```



![在这里插入图片描述](https://img-blog.csdnimg.cn/e4a86ce9554b40c0b1f1c4bf9ef753e9.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)



![在这里插入图片描述](https://img-blog.csdnimg.cn/025651023f1a4f4281173375ff5005e1.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16

![在这里插入图片描述](https://img-blog.csdnimg.cn/09ff21bcd4134f758a6bddb3a0db1f81.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)

![在这里插入图片描述](https://img-blog.csdnimg.cn/b88ba2c8bd4a4ce09de8b6870651e432.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAQWx2ZXVz,size_20,color_FFFFFF,t_70,g_se,x_16)



8. `login.html`中`form`表单的`action`路径的写法
   	* `虚拟目录+Servlet的资源路径`

9. 注意，mysql8 和mysql5的druid配置文件有所不同

```properties
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://192.168.64.1:3306/Data_Hive?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
username=root
password=pxl531
filters=stat
initialSize=2
maxActive=300
maxWait=60000
timeBetweenEvictionRunsMillis=60000
minEvictableIdleTimeMillis=300000
validationQuery=SELECT 1
testWhileIdle=true
testOnBorrow=false
testOnReturn=false
poolPreparedStatements=false
maxPoolPreparedStatementPerConnectionSize=200
```





10. BeanUtils工具类，简化JavaBean的数据封装

当一条记录中数据项过多时手动封装太麻烦，使用BeanUtils工具类，简化数据封装
将 `commons-beanutils-1.8.0.jar` 添加到`/web/WEB-INF/lib`

更改LoginServlet.java内部分代码

```java
//2. 获取所有请求参数
        Map<String, String[]> map = req.getParameterMap();
        //3. 创建User对象
        User loginUser = new User();
        //3.2 使用BeanUtils封装
        try {
            BeanUtils.populate(loginUser, map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

```

1. JavaBean：标准的Java类
   1. 要求：
      1. 类必须被public修饰
      2. 必须提供空参的构造器
      3. 成员变量必须使用private修饰
      4. 提供公共setter和getter方法
   2. 功能：封装数据


2. 概念：
   成员变量：
   属性：setter和getter方法截取后的产物
       例如：getUsername() --> Username--> username


3. 方法：
   1. `setProperty()` //设置属性
   2. `getProperty()` //获得属性
   3. ==`populate(Object obj , Map map)`==:将map集合的键值对信息，封装到对应的JavaBean对象中



## 增加文件下载功能

1. 页面显示超链接
2. 点击超链接后弹出下载提示框
3. 完成图片文件下载


* 分析：

1. 超链接指向的资源如果能够被浏览器解析，则在浏览器中展示，如果不能解析，则弹出下载提示框。不满足需求
2. 任何资源都必须弹出下载提示框
3. 使用响应头设置资源的打开方式：
   * `content-disposition:attachment;filename=xxx`


* 步骤：

1. 定义页面，编辑超链接href属性，指向Servlet，传递资源名称filename
2. 定义Servlet
   1. 获取文件名称
   2. 使用字节输入流加载文件进内存
   3. 指定response的响应头： content-disposition:attachment;filename=xxx
   4. 将数据写出到response输出流


* 问题：

* 中文文件问题
  * 解决思路：
    1. 获取客户端使用的浏览器版本信息
    2. 根据不同的版本信息，设置filename的编码方式不同

![在这里插入图片描述](https://img-blog.csdnimg.cn/d90b528f27e4415eb0d8b16dd584034d.png)

## 附加功能
Cookie 实现上次登录时间记录
Session 实现验证码检验

源码：https://github.com/Dr-LeopoldFitz/LoginPage

