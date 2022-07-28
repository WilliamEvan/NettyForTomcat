package com.grayson.netty.tomcat.nio.servlet;

import com.grayson.netty.tomcat.nio.http.GraysonRequest;
import com.grayson.netty.tomcat.nio.http.GraysonResponse;

public class SecondServlet extends GraysonAbstarctServlet {
    public String doGet(GraysonRequest request, GraysonResponse response) throws Exception {
        return this.doPost(request, response);
    }

    public String doPost(GraysonRequest request, GraysonResponse response) throws Exception {
        response.write("This is secondServlet from nio");
        return null;
    }
}
