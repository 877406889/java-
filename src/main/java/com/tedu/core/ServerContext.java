package com.tedu.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 服务端配置相关信息
 * @author 87740
 *
 */
public class ServerContext {
	public static String URIEcoding;
	public static int port;
	public static String protocol;
	public static int threadPool;
	public static Map<String,String> servletMapping;
	static{
		init();
	}
	
	private static void init(){
		initServletMapping();
		initServerInfo();
	}
	/**
	 * 加载conf/server.xml文件，初始化相关信息
	 */
	private static void initServerInfo(){
		
	}
	/**
	 * 初始化Servlet的映射
	 */
	private static void initServletMapping(){
		servletMapping=new HashMap<String,String>();
		try{
			SAXReader reader=new SAXReader();
			Document doc=reader.read(new FileInputStream("conf"+File.separator+"ServletMapping.xml"));
			Element root=doc.getRootElement();
			List<Element>list=root.elements("mapping");
			for(Element mapping:list){
				String uri=mapping.attributeValue("uri");
				String className=mapping.attributeValue("classname");
				servletMapping.put(uri, className);
			}
	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
