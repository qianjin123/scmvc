package com.qj.source.annotation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Dispatcher extends HttpServlet {
	
	List<String> pathList = new ArrayList<String>();
	Map<String,Class> beans = new HashMap<String,Class>();
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("xxxx");
		
		try {
			// 
			URL url =this.getClass().getResource("/com/qj/source");
			String path = url.getPath(); ///D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/scmvc/WEB-INF/classes/com/qj/source/
			System.out.println(path);
			
			URL url1 =this.getClass().getResource("/");//得到classes文件夹路径
			String basePath = url1.getPath(); //D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/scmvc/WEB-INF/classes/
			List<String> paths = getAllFile(path);
			Iterator<String> it = paths.iterator();
			while(it.hasNext()){
				String classPath = it.next().replace(basePath, "").replace("/", ".");//com.xx.xx.class
				String className = classPath.substring(0,classPath.lastIndexOf("."));//com.xx.xx
				String cname = className.substring(className.lastIndexOf(".")+1); //class名称
				Class clazz = Class.forName(className);
				System.out.println("type:"+clazz.getTypeName()); 
				if(clazz.isAnnotationPresent(QjCompent.class)){
					beans.put(clazz.getTypeName(), clazz);//class类名存入map
					
				}
				 
				System.out.println("扫描包下所有类名===>>"+cname);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		
		//调用方法
		String uri = req.getRequestURI();
		String contextpath = req.getContextPath();
		String mapping = uri.replace(contextpath, ""); // /order/list
		System.out.println(req.getContextPath()+"  "+req.getRequestURI()+"  "+req.getRequestURL() );
		Set<Entry<String, Class>> b = beans.entrySet();
		boolean methodAnno = false; //判断方法上注解路径是否匹配请求路径
		for (Entry<String, Class> entry : b) {
			Class clazz = entry.getValue();//类的实例
			Field[] fields = clazz.getDeclaredFields();
			Object instance = null; //对象实例
			for (Field field : fields) { //遍历类中所有属性
				Class<?> fieldType = field.getType(); //类中依赖的类型 orderService.class
				for (Entry<String, Class> entry_ : b) { 
					Class classType =  entry_.getValue(); //存入map 的Class对象 = OrderServiceImpl.class
					if( fieldType.isAssignableFrom(classType)  ){ //fieldType是否是classType的父类,  如果相等就注入
						field.setAccessible(true);//如果是private私有的，设置可访问的
						try {
							instance = clazz.newInstance();
							field.set(instance, classType.newInstance()); //参数一：所在类的对象 参数二：本身的实例
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
			
			//匹配路径
			if(clazz.isAnnotationPresent(Qjmapping.class)){
				Qjmapping q = (Qjmapping) clazz.getAnnotation(Qjmapping.class);
				String classMapping = q.value(); // /order
				if(mapping.contains(classMapping)){
					Method[] method = clazz.getMethods();
					for (Method m : method) {
						if(m.isAnnotationPresent(Qjmapping.class)){
							Qjmapping qj = m.getAnnotation(Qjmapping.class);
							String methMapping = qj.value();
							if( (classMapping+methMapping).equals(mapping) ){
								Object[] args = new Object[]{};
								Parameter[] para = m.getParameters();
								for (int i = 0; i < para.length; i++) {
									Class parameClass = para[i].getType();
									if(int.class == parameClass ){
										args[i] = int.class;
									}
									if(String.class == parameClass ){
										args[i] = String.class;
									}
								}
								//调用方法
								try {
									Object o = m.invoke(instance, args); //这个instance一定是被注入相关依赖后得实例
									out.print(o.toString());
								} catch ( Exception e) {
									e.printStackTrace();
								} 
								 methodAnno = true; 
							}
						}
					}
				}
			
			
			}
			 
		}
		if( !methodAnno){
			out.print("404");
			return;
		} 
	}
	
	public List<String> getAllFile(String path){
		File file = new File(path);
		String[] filePaths = file.list();
		for (String string : filePaths) {
			File fi = new File(path +string + "/" );
			if(fi.isDirectory()){
				getAllFile(path+string + "/");
			}else{
				pathList.add(path+string);
			}
			//System.out.println(string);
		}
		return pathList;
	}
	
}
