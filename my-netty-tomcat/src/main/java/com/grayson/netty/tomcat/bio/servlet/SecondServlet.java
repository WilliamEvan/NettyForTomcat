package com.grayson.netty.tomcat.bio.servlet;

import com.grayson.netty.tomcat.bio.http.GraysonRequest;
import com.grayson.netty.tomcat.bio.http.GraysonResponse;

public class SecondServlet extends GraysonAbstarctServlet{
    public String doGet(GraysonRequest request, GraysonResponse response) throws Exception {
        return this.doPost(request, response);
    }

    public String doPost(GraysonRequest request, GraysonResponse response) throws Exception {
        response.write("This is secondServlet from bio");
        return null;
    }
}
