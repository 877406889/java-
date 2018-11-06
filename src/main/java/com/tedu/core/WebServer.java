package com.tedu.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;
import servlets.HttpServlet;
import servlets.LoginServlet;
import servlets.RegServlet;


public class WebServer {
	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer(){
		try {
		  server=new ServerSocket(8080);
		  threadPool=Executors.newFixedThreadPool(30);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		try {
			while(true)
			{
			//System.out.println("初始化客户端");
			Socket socket=server.accept();
			ClientHandler handler=new ClientHandler(socket);
			threadPool.execute(handler);
			//System.out.println("客户端初始化完毕");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args){
		WebServer server=new WebServer();
		server.start();
	}
	
	public class ClientHandler implements Runnable{
		private Socket socket;
		public ClientHandler(Socket socket){
			this.socket=socket;
		}
		public void run(){
			try{
				//在此解析请求行
				System.out.println("开始解析请求行");
				HttpRequest request=new HttpRequest(socket.getInputStream());
				HttpResponse response=new HttpResponse(socket.getOutputStream());
				System.out.println(request.getMethod());
				System.out.println("uri:"+request.getRequestLine());
				System.out.println(request.getProtocol());
				if(ServerContext.servletMapping.containsKey(request.getRequestLine())){
					String className=ServerContext.servletMapping.get(request.getRequestLine());
					System.out.println("该Servlet名字："+className);
					//通过反射加载该类
					Class cls=Class.forName(className);
					System.out.println("反射完毕");
					HttpServlet servlet=(HttpServlet)cls.newInstance();
					System.out.println("实例化完毕");
					servlet.service(request, response);
					}
				else{
					File file=new File("webapps"+request.getRequestLine());
					if(file.exists()){
						System.out.println("该文件存在");
						forward(request.getRequestLine(),request,response);
					}else{
						response.setStatusCode(HttpContext.STATUS_CODE_NOT_FOUND);
						forward("/global/404.html", request, response);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try {
					socket.close();//最后一定要关闭流
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/**
		 * 跳转页面
		 */
		private void forward(String path,HttpRequest request,HttpResponse response){
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
}
