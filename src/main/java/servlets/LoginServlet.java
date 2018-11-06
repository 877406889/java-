package servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tedu.core.HttpContext;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

/**
 * ���������¼ҵ��
 * @author 87740
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response){
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		System.out.println(username+":"+password);
		BufferedReader br=null;
		try{
			br=new BufferedReader(new InputStreamReader(new FileInputStream("user.txt")));
			String line=null;
			boolean login =false;
			while((line=br.readLine())!=null){
				String[] infos=line.split(",");
				String user=infos[0];
				String pwd=infos[1];
				System.out.println("���ڱȶ��û���"+user+":"+pwd);
				if(username.equals(user)&&password.equals(pwd)){
					login=true;
					break;
				}
			}
			if(login){
				System.out.println("��¼�ɹ�");
				forward("/myweb/login_success.html",request,response);
			}else{
				System.out.println("��¼ʧ��");
				forward("/myweb/login_error.html",request,response);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
