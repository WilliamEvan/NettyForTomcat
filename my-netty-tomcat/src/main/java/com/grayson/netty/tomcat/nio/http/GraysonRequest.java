package com.grayson.netty.tomcat.nio.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class GraysonRequest {

//    private String method;
//    private String url;

//    public GraysonRequest(InputStream is) {
//        try {
//            //先拿到http协议的具体内容
//            String content = "";
//            //缓存区，bio的写法
//            byte[] buff = new byte[1024];
//            int len = 0;
//            if ((len = is.read(buff)) > 0) {
//                content = new String(buff, 0, len);
//            }
//            //System.out.println("request解析结果：" + content);
//
//            //GET /firstSevlet.do HTTP/1.1
//            //Host: localhost:8080
//            //Connection: keep-alive
//            //...
//            //解析以上http协议内容
//            //以换行进行分割
//            String line = content.split("\\n")[0];
//            //每行以空格分割（观察http内容）
//            String[] arr = line.split("\\s");
//            this.method = arr[0];
//            //暂时忽略url的参数
//            this.url =arr[1].split("\\?")[0];
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public GraysonRequest(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;

    }

    public String getUrl(){
        return this.req.uri();
    }

    public String getMethod(){
        return this.req.method().name();
    }
    //这里是获取url参数的（所有的），bio未实现
    public Map<String, List<String>> getParameters(){
        QueryStringDecoder decoder =  new QueryStringDecoder(req.uri());
        return decoder.parameters();
    }
    //获取指定的某一个参数
    public String getParameter(String name){
        Map<String, List<String>> params = getParameters();
        List<String> param = params.get(name);
        if(null == param){
            return null;
        }else {
            return param.get(0);
        }
    }
}
