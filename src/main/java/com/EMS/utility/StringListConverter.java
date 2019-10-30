package com.EMS.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.codec.binary.StringUtils;


@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> list) {
		//remove white space 
		list.replaceAll(String::trim);
		return String.join(",", list);
		

	}

	@Override
	public List<String> convertToEntityAttribute(String joined) {
		return new ArrayList<>(Arrays.asList(joined.split(",")));
	}

}
