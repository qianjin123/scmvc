package com.qj.source.business.service.impl;

import com.qj.source.annotation.QjClassAnno;
import com.qj.source.business.service.OrderService;

@QjClassAnno("/orderService")
public class OrderServiceImpl implements OrderService {

	public String getOrderById(int id) {
		return "order id = 0001";
	}

}
