package com.qj.source.business;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qj.source.annotation.QjClassAnno;
import com.qj.source.annotation.QjMethodAnno;
import com.qj.source.business.service.OrderService;

@QjClassAnno("/order")
public class OrderController   {
	
	private OrderService orderService;
	
	@QjMethodAnno("/list")
	public String selectList(){
		//System.out.println("调用业务方法成功");
		String result = orderService.getOrderById(1);
		return result;
		
	}
	
}
