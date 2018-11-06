package servlets;

import HttpRequest.HttpRequest;
import HttpResponse.HttpResponse;

public class WebRegServlet extends HttpServlet{

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("¿ªÊ¼×¢²á");
	}
	
}
