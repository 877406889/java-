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
	 * ������Ϣ
	 */
	private String requestLine;
	private Map<String,String> params=new HashMap();
	/*
	 * ��map����������Ϣͷ
	 */
	private Map<String,String> headers=new HashMap<String,String>();
	public HttpRequest(InputStream in){
		parseRequestLine(in);
		parseHeaders(in);
		parseContent(in);
	}
	/**
	 * ������Ϣ����
	 */
	private void parseContent(InputStream in){
		//�ж���Ϣͷ�е�Content-Type
		String contentType=this.headers.get("Content-Type");
		if(contentType!=null&&"application/x-www-form-urlencoded".equals(contentType)){
			System.out.println("����������");
			int contentLength=Integer.parseInt(this.headers.get("Content-Length"));
			try {
				byte[]buf=new byte[contentLength];
				in.read(buf);
				String line=new String(buf);
				//��Ϊiso8859-1�޷�ʶ���������Խ����Ļ����
				//16���Ƶ�utf-8����
				//java��apiֱ�ӽ���
				line=URLDecoder.decode(line,"UTF-8");
				System.out.println("�����"+line);
				parseParams(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	/*
	 * ����URI
	 * URI���ܻ�����ͻ��˴��ݹ���������
	 * ����Ҫ�������н���
	 * ���磺
	 * uri:/myweb/reg?user=fancq&pwd=123456
	 * ��
	 * uri:/myweb/reg.html
	 * ���ڵ�һ���������Ҫ������ߵ����ݸ�ֵ��requestLine������ԣ����ұߵ�����
	 * ��ÿ������������params���map��
	 * ���ڶ����������û�в�������ôֱ�ӽ�uri��ֵ��requestLine����
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
		System.out.println("����uri���");
		this.headers.forEach((k,v)->System.out.println(k+":"+v));
	}
	/*
	 * ����ͷ��Ϣ
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
	 * ������&��ֵķ��������
	 * ��Ҫ���ڽ����û�������ʲô��
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
	 * ���ݸ����Ĳ�����ȡ������ֵ
	 */
	public String getParameter(String name){
		return params.get(name);
	}
	
	public String getRequestLine() {
		return requestLine;
	}
	
}
