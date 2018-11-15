package com.qj.source.business;


import com.qj.source.annotation.QjCompent;
import com.qj.source.annotation.Qjmapping;
import com.qj.source.business.service.OrderService;

@QjCompent
@Qjmapping("/order")
public class OrderController   {
	
	private OrderService orderService;
	
	@Qjmapping("/list")
	public String selectList(){
		//System.out.println("调用业务方法成功");
		String result = orderService.getOrderById(1);
		return result;
		
	}
	
}
