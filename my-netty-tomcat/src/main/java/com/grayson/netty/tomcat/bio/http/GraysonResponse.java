package com.grayson.netty.tomcat.bio.http;

import java.io.IOException;
import java.io.OutputStream;

public class GraysonResponse {

    private OutputStream out;

    public GraysonResponse(OutputStream os) {
        this.out = os;
    }

    public String write(String s) throws IOException {
        StringBuilder sb = new StringBuilder();
        //浏览器要求的相应格式
        sb.append("HTTP/1.1 200 ok\n")
            .append("Content-Type: text/html;\n")
            .append("\r\n")
            .append(s);
        out.write(sb.toString().getBytes());
        return null;
    }
}
