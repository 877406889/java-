package HttpResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.core.HttpContext;

public class HttpResponse {
	private OutputStream out;
	public File entity;
	private Map<String,String>header=new HashMap<String,String>();
	/*
	 * 当前响应的状态代码
	 */
	private int statusCode=HttpContext.STATUS_CODE_OK;
	public File getEntity() {
		return entity;
	}
	/*
	 * 调用该函数来设置头信息所用的文件类型
	 */
	public void setContentType(String contenttype){
		this.header.put("Content-Type",contenttype);
	}
	/*
	 * 将头信息所用的文件length存入map之中（设置length）
	 */
	public void setContentLength(int length){
		this.header.put("Content-Length", length+"");
	}
	/**
	 * 设置状态代码
	 * @param code
	 */
	public void setStatusCode(int code){
		this.statusCode=code;
		
	}
	
	public void setEntity(File entity) {
		this.entity = entity;
	}

	public HttpResponse (OutputStream out){
		this.out=out;
	}
	
	public void flush(){
		sendStatusLine();
		sendHeaders();
		sendContent();
	}
	
	private void println(String line){
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(HttpContext.CR);
			out.write(HttpContext.LF);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendStatusLine(){
		String status="HTTP/1.1"+" "+statusCode+" "+HttpContext.getReasonByCode(statusCode);
		println(status);
	}
	
	private void sendHeaders(){
		Set<Entry<String,String>> headerSet=header.entrySet();
		for(Entry<String,String>header : headerSet){
			String line=header.getKey()+":"+header.getValue();
			println(line);
		}
		println("");
	}
	
	private void sendContent(){
		try {
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(entity));
			int len=-1;
			byte[]data=new byte[1024*10];
			try {
				while((len=bis.read(data))!=-1){
					out.write(data, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
