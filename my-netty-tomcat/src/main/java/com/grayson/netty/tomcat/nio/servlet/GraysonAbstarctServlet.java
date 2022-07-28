package com.grayson.netty.tomcat.nio.servlet;

import com.grayson.netty.tomcat.nio.http.GraysonRequest;
import com.grayson.netty.tomcat.nio.http.GraysonResponse;

public abstract class  GraysonAbstarctServlet {

    public void service(GraysonRequest request, GraysonResponse response) throws Exception{
        if("GET".equals(request.getMethod())){
            this.doGet(request, response);
        } else {
            this.doPost(request, response);
        }
    }

    public abstract String doGet(GraysonRequest request, GraysonResponse response) throws Exception;
    public abstract String doPost(GraysonRequest request, GraysonResponse response) throws Exception;
}
