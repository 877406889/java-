package HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.core.HttpContext;

public class HttpRequest {
	private String uri;
	private String method;
	private String protocol;
	/*
	 * 请求信息
	 */
	private String requestLine;
	private Map<String,String> params=new HashMap();
	/*
	 * 该map用来解析消息头
	 */
	private Map<String,String> headers=new HashMap<String,String>();
	public HttpRequest(InputStream in){
		parseRequestLine(in);
		parseHeaders(in);
		parseContent(in);
	}
	/**
	 * 解析消息正文
	 */
	private void parseContent(InputStream in){
		//判断消息头中的Content-Type
		String contentType=this.headers.get("Content-Type");
		if(contentType!=null&&"application/x-www-form-urlencoded".equals(contentType)){
			System.out.println("解析表单数据");
			int contentLength=Integer.parseInt(this.headers.get("Content-Length"));
			try {
				byte[]buf=new byte[contentLength];
				in.read(buf);
				String line=new String(buf);
				//因为iso8859-1无法识别中文所以将中文换算成
				//16进制的utf-8编码
				//java有api直接解码
				line=URLDecoder.decode(line,"UTF-8");
				System.out.println("解码后："+line);
				parseParams(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	/*
	 * 解析URI
	 * URI可能会包含客户端传递过来的数据
	 * 所以要对它进行解析
	 * 例如：
	 * uri:/myweb/reg?user=fancq&pwd=123456
	 * 或
	 * uri:/myweb/reg.html
	 * 对于第一种情况，需要将？左边的内容赋值给requestLine这个属性，而右边的内容
	 * 则每个参数都存入params这个map中
	 * 而第二种情况由于没有参数，那么直接将uri赋值给requestLine即可
	 */
	private void parseUri(){
		int index=this.uri.indexOf("?");
		if(index==-1){
			requestLine=this.uri;
		}else{
			requestLine=this.uri.substring(0, index);
			String query=this.uri.substring(index+1);
			parseParams(query);
		}
		System.out.println("解析uri完毕");
		this.headers.forEach((k,v)->System.out.println(k+":"+v));
	}
	/*
	 * 解析头信息
	 */
	private void parseHeaders(InputStream in){
		while(true){
			String line=readLine(in);
			if("".equals(line)){
				break;
			}
			String name=line.substring(0, line.indexOf(":")).trim();
			String value=line.substring(line.indexOf(":")+1).trim();
			this.headers.put(name, value);
		}
		Set<Entry<String,String>>set=this.headers.entrySet();
		for(Entry<String,String>e:set){
			System.out.println(e.getKey()+":"+e.getValue());
		}
	}
	
	private void parseRequestLine(InputStream in){
			String Line=readLine(in);
			String[]data=Line.split("\\s");
			this.method=data[0];
			this.uri=data[1];
			this.protocol=data[2];
			parseUri();
	}

	private String readLine(InputStream in){
		StringBuilder builder=new StringBuilder();
		try{
			char c1=0,c2=0;
			int d=-1;
			while((d=in.read())!=-1){
				c2=(char)d;
				if((c1==HttpContext.CR)&&(c2==HttpContext.LF)){
					break;
				}
				builder.append(c2);
				c1=c2;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return builder.toString().trim();
	}
	/**
	 * 将按照&拆分的方法提出来
	 * 主要用于解析用户名密码什么的
	 * @param line
	 */
	private void parseParams(String line){
		String[]paramArr=line.split("&");
		for(String paramStr:paramArr){
			String[]para=paramStr.split("=");
			if(para.length==2){
				this.params.put(para[0], para[1]);
			}
			else if(para.length==1){
				this.params.put(para[0], "");
			}
		}
	}
	
	public String getUri() {
		return uri;
	}

	public String getMethod() {
		return method;
	}

	public String getProtocol() {
		return protocol;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	/*
	 * 根据给定的参数获取参数的值
	 */
	public String getParameter(String name){
		return params.get(name);
	}
	
	public String getRequestLine() {
		return requestLine;
	}
	
}
