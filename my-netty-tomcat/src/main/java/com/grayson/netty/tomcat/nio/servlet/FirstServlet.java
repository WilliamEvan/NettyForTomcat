package com.grayson.netty.tomcat.nio.servlet;

import com.grayson.netty.tomcat.nio.http.GraysonRequest;
import com.grayson.netty.tomcat.nio.http.GraysonResponse;

public class FirstServlet extends GraysonAbstarctServlet {
    public String doGet(GraysonRequest request, GraysonResponse response) throws Exception {
        return this.doPost(request, response);
    }

    public String doPost(GraysonRequest request, GraysonResponse response) throws Exception {
        //System.out.println("This is FirstServlet.");
        response.write("This is other FirstServlet from nio");
        return null;
    }

}
