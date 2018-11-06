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
			//System.out.println("��ʼ���ͻ���");
			Socket socket=server.accept();
			ClientHandler handler=new ClientHandler(socket);
			threadPool.execute(handler);
			//System.out.println("�ͻ��˳�ʼ�����");
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
				//�ڴ˽���������
				System.out.println("��ʼ����������");
				HttpRequest request=new HttpRequest(socket.getInputStream());
				HttpResponse response=new HttpResponse(socket.getOutputStream());
				System.out.println(request.getMethod());
				System.out.println("uri:"+request.getRequestLine());
				System.out.println(request.getProtocol());
				if(ServerContext.servletMapping.containsKey(request.getRequestLine())){
					String className=ServerContext.servletMapping.get(request.getRequestLine());
					System.out.println("��Servlet���֣�"+className);
					//ͨ��������ظ���
					Class cls=Class.forName(className);
					System.out.println("�������");
					HttpServlet servlet=(HttpServlet)cls.newInstance();
					System.out.println("ʵ�������");
					servlet.service(request, response);
					}
				else{
					File file=new File("webapps"+request.getRequestLine());
					if(file.exists()){
						System.out.println("���ļ�����");
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
					socket.close();//���һ��Ҫ�ر���
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/**
		 * ��תҳ��
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
