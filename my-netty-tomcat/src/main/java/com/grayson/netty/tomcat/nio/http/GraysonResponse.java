package com.grayson.netty.tomcat.nio.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.OutputStream;

public class GraysonResponse {

//    private OutputStream out;
//
//    public GraysonResponse(OutputStream os) {
//        this.out = os;
//    }
//    public String write(String s) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        //浏览器要求的相应格式
//        sb.append("HTTP/1.1 200 ok\n")
//                .append("Content-Type: text/html;\n")
//                .append("\r\n")
//                .append(s);
//        out.write(sb.toString().getBytes());
//        return null;
//    }

    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public GraysonResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req =req;
    }
    public void write(String s) throws IOException {
        if(null == s || 0 == s.length()){
            return;
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                //设置版本号
                HttpVersion.HTTP_1_1,
                //设置返回状态
                HttpResponseStatus.OK,
                //统一输出格式
                Unpooled.wrappedBuffer(s.getBytes("UTF-8"))
        );
        response.headers().set("Content-Type","text/html");
        ctx.write(response);
        ctx.flush();
        ctx.close();
    }


}
