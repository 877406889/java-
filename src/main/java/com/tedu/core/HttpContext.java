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
	 * ��Ӧ״̬����
	 */
	/**
	 * ���룺����
	 */
	public static final int STATUS_CODE_OK=200;
	/**
	 * ���룺δ�ҵ�
	 */
	public static final int STATUS_CODE_NOT_FOUND=404;
	/**
	 * ���룺����
	 */
	public static final int STATUS_CODE_ERROR=500;
	/**
	 * ״̬����������ӳ��
	 */
	private static Map<Integer,String> code_reason_mapping;
	private final static Map<String,String>mimeTypeMapping= new HashMap();
	/*
	 * ������ʼ����������ⲿ�ṩһ�����÷���
	 */
	static{
		init();
	}
	public static void init(){
		//��ʼ��״̬������������ӳ��
		initmimeTypeMapping();
		//��ʼ��״̬����������ӳ��
		initCodeReasonMapping();
	}
	/*
	 * ������ʼ��map
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
	 * ��ȡ�ļ����ͺ������ⲿ����
	 */
	public static String getContextTypeBymime(String mime){
		return mimeTypeMapping.get(mime);
	}
	
	public static String getReasonByCode(int code){
		return code_reason_mapping.get(code);
	}
}
