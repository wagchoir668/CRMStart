package com.wagchoir.crm.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PrintJson {

	//将boolean值解析为json串
	public static void printJsonFlag(HttpServletResponse response,boolean flag) throws IOException {
		Map<String,Boolean> map = new HashMap<>();
		map.put("success",flag);
		
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(map);;
		response.getWriter().print(json);
	}
	
	//将对象（Domain、List、Map）解析为json串
	public static void printJsonObj(HttpServletResponse response,Object obj) throws IOException {
		/*
		 * Person p
		 * 	id name age
		 * {"id":"?","name":"?","age":?}
		 * 
		 * List<Person> pList
		 * [{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?},{"id":"?","name":"?","age":?}...]
		 * 
		 * Map
		 * 	key value
		 * {key:value}
		 */
		ObjectMapper om = new ObjectMapper();
		String json =  om.writeValueAsString(obj);
		response.getWriter().print(json);
	}
}