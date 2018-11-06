package com.tedu.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	public static final int CR=13;
	public static final int LF=10;
	/**
	 * 响应状态代码
	 */
	/**
	 * 代码：正常
	 */
	public static final int STATUS_CODE_OK=200;
	/**
	 * 代码：未找到
	 */
	public static final int STATUS_CODE_NOT_FOUND=404;
	/**
	 * 代码：错误
	 */
	public static final int STATUS_CODE_ERROR=500;
	/**
	 * 状态代码与描述映射
	 */
	private static Map<Integer,String> code_reason_mapping;
	private final static Map<String,String>mimeTypeMapping= new HashMap();
	/*
	 * 用来初始化整个类给外部提供一个调用方法
	 */
	static{
		init();
	}
	public static void init(){
		//初始化状态代码与描述的映射
		initmimeTypeMapping();
		//初始化状态代码与描述映射
		initCodeReasonMapping();
	}
	/*
	 * 用来初始化map
	 */
	private static void initCodeReasonMapping(){
		code_reason_mapping=new HashMap<Integer,String>();
		code_reason_mapping.put(STATUS_CODE_OK, "OK");
		code_reason_mapping.put(STATUS_CODE_NOT_FOUND, "NOT FOUND");
		code_reason_mapping.put(STATUS_CODE_ERROR, "ERROR");
	}
	private static void initmimeTypeMapping(){
		SAXReader reader=new SAXReader();
		try {
			Document doc=reader.read(new FileInputStream("conf"+File.separator+"web.xml"));
			Element root=doc.getRootElement();
			List<Element>list=root.elements("mime-mapping");
			for(Element ele:list){
				Element keyxml=ele.element("extension");
				String key=keyxml.getText();
				Element valuexml=ele.element("mime-type");
				String value=valuexml.getText();
				mimeTypeMapping.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
   	}
	/*
	 * 获取文件类型函数给外部调用
	 */
	public static String getContextTypeBymime(String mime){
		return mimeTypeMapping.get(mime);
	}
	
	public static String getReasonByCode(int code){
		return code_reason_mapping.get(code);
	}
}
