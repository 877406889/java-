package servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tedu.core.HttpContext;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;
/**
 * ����ע��ҵ��
 * @author 87740
 *
 */
public class RegServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response){
		System.out.println("��ʼע��");
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		String nickname=request.getParameter("nickname");
		System.out.println(username+","+password+","+nickname);
		PrintWriter pw=null;
		try{
			pw=new PrintWriter(new OutputStreamWriter(new FileOutputStream("user.txt",true)));
			pw.println(username+","+password+","+nickname);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
		System.out.println("ע�����");
		forward("/myweb/reg_success.html",request,response);
	}
}
