package com.grayson.netty.tomcat.bio;

import com.grayson.netty.tomcat.bio.http.GraysonRequest;
import com.grayson.netty.tomcat.bio.http.GraysonResponse;
import com.grayson.netty.tomcat.bio.servlet.GraysonAbstarctServlet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于bio实现的一个简易tomcat
 */
public class GraysonTomcat {

    //tomcat默认端口
    private int port = 8080;
    private ServerSocket server;
    private Properties properties = new Properties();
    private Map<String, GraysonAbstarctServlet> servletMapping = new ConcurrentHashMap<String, GraysonAbstarctServlet>(8);

    //tomcat启动入口
    public static void main(String[] args) {
        new GraysonTomcat().start();
    }

    //1、初始化方法：加载web.properties参数,
    //2、解析配置：将servlet.*.className与servlet.*.url建立映射关系，并缓存，
    //3、解析浏览器请求的url映射到对应的servlet，通过反射创建servlet实例，访问对应的servlet的doget或dopost方法
    private void start() {
        //1、初始化方法：加载web.properties参数,
        init();

        try {
            //2、启动服务端socket等待用户请求（java中操作tcp的方法：ServerSocket/Socket）
            server = new ServerSocket(port);
            System.out.println("启动中...，监听端口是：" + this.port);
            //一直等待用户请求
            while (true){
                Socket client = server.accept();
                //socket一般会携带两个对象InputStream/OutputStream.request和response就是对InputStream/OutputStream的封装。
                //获得请求信息，解析http内容
                process(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(){
        //获取resources文件路径
        String WEB_INF = this.getClass().getResource("/").getPath();
        //获取文件输入流
        try {
            FileInputStream is = new FileInputStream(WEB_INF + "web-bio.properties");
            //加载web.properties文件输入流
            properties.load(is);

            for(Object k : properties.keySet()){
                String key = k.toString();
                //是否key以url结尾
                if(key.endsWith(".url")){
                    //获取servlet.*
                    String servletName = key.replaceAll("\\.url$", "");
                    //key -> servlet.*.className, value -> className
                    String className = properties.getProperty(servletName + ".className");
                    String url = properties.getProperty(key);

                    //通过className反射获取servlet对象, java9之后推荐的写法.getDeclaredConstructor()
                    GraysonAbstarctServlet obj = (GraysonAbstarctServlet)Class.forName(className).getDeclaredConstructor().newInstance();
                    //将url和对应的Servlet对象缓存(建立映射关系）
                    servletMapping.put(url, obj);
                    //根据j2ee规范，web.xml中还有<load-on-startup>值需要配置
                    //（大于等于1，表示sevlet在web容器启动时初始化，否则，在请求时初始化，这里直接默认启动时初始化，忽略了<load-on-startup>）
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(Socket client) throws Exception{

            InputStream is = client.getInputStream();
            OutputStream os = client.getOutputStream();

            GraysonRequest request = new GraysonRequest(is);
            GraysonResponse response = new GraysonResponse(os);

            String url = request.getUrl();
            if(servletMapping.containsKey(url)){
                servletMapping.get(url).service(request, response);
            } else {
                response.write("404 - Not Found!!");
            }
            os.flush();
            os.close();
            is.close();
            client.close();
    }
}
