package com.grayson.netty.tomcat.nio;

import com.grayson.netty.tomcat.nio.http.GraysonRequest;
import com.grayson.netty.tomcat.nio.http.GraysonResponse;
import com.grayson.netty.tomcat.nio.servlet.GraysonAbstarctServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 前提：先了解bio
 * 基于nio实现的一个简易tomcat
 * netty封装了http解析的方法
 */
public class GraysonTomcat {

    //tomcat默认端口
    private int port = 8080;
    private Properties properties = new Properties();
    private Map<String, GraysonAbstarctServlet> servletMapping = new HashMap<String, GraysonAbstarctServlet>(8);

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

        //Boss线程
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        //Worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //2、创建Netty服务端对象(ServerBootstrap 这里作用类似于bio中ServerSocket)
        ServerBootstrap server = new ServerBootstrap();

        try {
            //3、配置服务端参数
            server.group(boosGroup, workerGroup)
                    //配置主线程的处理逻辑，基于nio的socket
                    .channel(NioServerSocketChannel.class)
                    //子线程的回调处理
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel client) throws Exception {
                            //处理回调的逻辑
                            //netty内部逻辑处理是链式编程，每一个处理逻辑都是一个hander

                            //处理请求的encoder，即处理响应结果的封装
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //处理请求的decoder，即按http格式解码
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //用户自己的业务逻辑
                            client.pipeline().addLast(new GraysonTomcatHandler());
                        }
                    })
                    //配置主线程分配的最大的线程数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //启动服务
            ChannelFuture f = server.bind(this.port).sync();
            System.out.println("GraysonTomcat 已启动，监听端口：" + this.port);

            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void init(){
        //获取resources文件路径
        String WEB_INF = this.getClass().getResource("/").getPath();
        //获取文件输入流
        try {
            FileInputStream is = new FileInputStream(WEB_INF + "web-nio.properties");
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


    }

    private class GraysonTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest)msg;
                GraysonRequest request =  new GraysonRequest(ctx, req);
                GraysonResponse response =  new GraysonResponse(ctx, req);

                String url = request.getUrl();
                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request, response);
                }else {
                    response.write("404 - not found");
                }

            }
        }
    }
}
