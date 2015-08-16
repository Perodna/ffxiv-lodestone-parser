package com.pandore.ffxiv.lodestone.parser;

import org.jsoup.select.Elements;

import exceptions.UnexpectedHtmlStructureException;

public class ParserUtils {
	
	public static int extractNumeric(String string) {
		return Integer.parseInt(string.replaceAll("[^\\d.]", ""));
	}
	
	public static void checkElementsSize(Elements elements, int expectedSize, String exceptionMessage ) throws UnexpectedHtmlStructureException{
		if (elements.size() != expectedSize) {
			throw new UnexpectedHtmlStructureException(exceptionMessage + " [actual number of elts: " + elements.size() + "]");
		}
	}
}
