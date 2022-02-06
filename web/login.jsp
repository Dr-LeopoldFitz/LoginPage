<%--
  Created by IntelliJ IDEA.
  User: Hive
  Date: 2022/2/6
  Time: 19:14
  使用JSP实现带验证码的登陆界面.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>登录</title>

    <script>
        window.onload = function () {
            document.getElementById("img").onclick = function () {
                this.src = "/loginpage/getCheckCodeServlet?time=" + new Date().getTime();
                /*传入?time参数可保证验证码图片被点击时可以刷新，不至于因为缓存原因不刷新(因为时间一直在变且不会有重复值)*/
            }
        }


    </script>
    <style>
        .centerArea {
            /*让div水平居中*/
            width: 240px;
            height: 75px;
            margin: 20% auto;
        }

        .warn {
            color: red;
        }


    </style>
</head>
<body>
<div class="centerArea">
    <form action="/loginpage/checkCodeServlet" method="post">
        <table>
            <tr>
                <td align="">用户名</td>
                <td><input type="text" name="username"></td>
            </tr>
            <tr>
                <td>密码</td>
                <td><input type="password" name="password"></td>
            </tr>
            <tr>
                <td>验证码</td>
                <td><input type="text" name="checkCode"></td>
            </tr>
            <tr>
                <td colspan="2" style="text-align:center;"><img id="img" src="/loginpage/getCheckCodeServlet"></td>
            </tr>
            <tr>
                <td colspan="3" style="text-align:center;"><input type="submit" value="登录"
                                                                  style="margin: 10px auto;width: 40%"></td>
            </tr>
        </table>
    </form>
</div>
<div class="warn" style="text-align: center"><%=request.getAttribute("cc_error") == null ? "" : request.getAttribute("cc_error")%>
</div>
<div class="warn" style="text-align: center"><%=request.getAttribute("login_error") == null ? "" : request.getAttribute("login_error") %>
</div>

</body>
</html>