package servlets;

import java.io.File;

import com.tedu.core.HttpContext;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

/**
 * 所有Servlet的超类。定义Servlet都应当
 * 具备的功能
 * @author 87740
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	/*
	 * 用来返回页面
	 */
	public void forward(String path,HttpRequest request,HttpResponse response){
		try{
			File file=new File("webapps"+path);
			String name=file.getName().substring(file.getName().lastIndexOf(".")+1);
			String contentType=HttpContext.getContextTypeBymime(name);
			response.setContentType(contentType);
			response.setContentLength((int)file.length());
			response.setEntity(file);
			response.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
