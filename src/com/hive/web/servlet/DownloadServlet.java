package com.hive.web.servlet;

import com.hive.utils.DownloadUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * @author Hive
 * Description: 实现文件下载功能的Servlet
 * Date: 2022/2/6 14:39
 */

@WebServlet("/downloadServlet")
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取请求参数，文件名称
        String filename = req.getParameter("filename");
        //2.使用字节输入流加载文件进内存
        //2.1找到文件服务器路径
        ServletContext servletContext = this.getServletContext();
        String realPath = servletContext.getRealPath("/img/" + filename);
        //2.2用字节流关联
        FileInputStream fis = new FileInputStream(realPath);

        //3.设置response的响应头
        //3.1设置响应头类型：content-type
        String mimeType = servletContext.getMimeType(filename);//获取文件的mime类型
        resp.setHeader("content-type",mimeType);

        //3.2设置响应头打开方式:content-disposition
        //用工具类解决中文文件名下载乱码问题(工具类不需要自己写)
        //1.获取user-agent请求头、
        String agent = req.getHeader("user-agent");
        //2.使用工具类方法编码文件名即可
        filename = DownloadUtils.getFileName(agent, filename);
        //打开方式改为 attachment 方式
        resp.setHeader("content-disposition","attachment;filename="+filename);

        //4.将输入流的数据写出到输出流中(文件对拷)
        ServletOutputStream sos = resp.getOutputStream();
        byte[] buff = new byte[1024 * 8];
        int len = 0;
        while((len = fis.read(buff)) != -1){
            sos.write(buff,0,len);
        }

        fis.close();

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
