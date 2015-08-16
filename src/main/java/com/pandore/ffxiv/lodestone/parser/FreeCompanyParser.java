package com.pandore.ffxiv.lodestone.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.entity.LSFreeCompany;

import exceptions.LodestoneParserException;
import exceptions.UnexpectedHtmlStructureException;

public class FreeCompanyParser {
	
	private boolean verbose = true;
	
	private final String rootUrl;
	
	public FreeCompanyParser(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	
	public LSFreeCompany getFreeCompanyById(String freeCompanyId, boolean parseMembers) throws LodestoneParserException {
		if (verbose) {
			System.out.println("Parsing Lodestone for free company " + freeCompanyId);
		}
		
		// result
		LSFreeCompany freeCompany = new LSFreeCompany();
		freeCompany.setId(freeCompanyId);
				
		// --- Get basic info (name, rank, tag, world)
		String url = rootUrl + URLS.FREE_COMPANY + freeCompanyId;
		Document html;
		try {
			html = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new LodestoneParserException("Could not connect to Lodestone", e);
		}
		parseTopLevelInfo(freeCompany, html);
		
		
		// --- Get members
		if (parseMembers) {
			String membersUrl = rootUrl + URLS.FREE_COMPANY + freeCompanyId + URLS.FREE_COMPANY_MEMBERS_SUFFIX;
			parseMembers(freeCompany, membersUrl);
		}
		
		return freeCompany;
	}
	
	
	private void parseTopLevelInfo(LSFreeCompany freeCompany, Document html) throws LodestoneParserException {
		
		Elements infoTable = html.select("table.table_style2 > tbody");
		ParserUtils.checkElementsSize(infoTable, 1, "Cannot find HTML for free company info");
		Element table = infoTable.first();
		
//		for (Element th : table.select("tr > th")) {
//			System.out.println(th.text());
//		}
		
		// get world
		Elements spans = html.select("div.ic_freecompany_box > div.crest_id.centering_h > span"); // there will be several, get the last one
		Element world = spans.last();
		freeCompany.setWorld(world.text().replace("(", "").replace(")", ""));  // remove parenthesis: "(Ragnarok)" -> "Ragnarok"
		
		//get name and tag
		Element nameAndTag = getTDfromTH(table, "Free Company Name «Company Tag»", false);
		
		Elements name = nameAndTag.select("span");
		ParserUtils.checkElementsSize(name, 1, "Cannot find HTML for free company name");
		freeCompany.setName(name.first().text());
		
		freeCompany.setTag(nameAndTag.textNodes().get(0).text().replace("«", "").replace("»", ""));
		
		// get rank
		Element rank = getTDfromTH(table, "^Rank$", true);
		freeCompany.setRank(ParserUtils.extractNumeric(rank.text()));
	}
	
	private Element getTDfromTH(Element table, String thContent, boolean isRegexp) throws UnexpectedHtmlStructureException {
		Elements th;
		if (isRegexp) {
			th = table.select("tr > th:matchesOwn(" + thContent + ")");
		} else {
			th = table.select("tr > th:containsOwn(" + thContent + ")");
		}
		ParserUtils.checkElementsSize(th, 1, "Cannot find table header: " + thContent);
		
		Elements td = th.first().parent().select("td");
		ParserUtils.checkElementsSize(td, 1, "Cannot find table cell");
		
		return td.first();
	}
	
	private void parseMembers(LSFreeCompany freeCompany, String membersUrl) throws LodestoneParserException {
		parseMembers(freeCompany, membersUrl, 1, true);
	}
	
	private void parseMembers(LSFreeCompany freeCompany, String membersUrl, int pageNumber, boolean goToNextPage) throws LodestoneParserException {
		
		// Get the html page
		String pageUrl = membersUrl + "?page=" + pageNumber;
		Document html;
		try {
			html = Jsoup.connect(pageUrl).get();
		} catch (IOException e) {
			throw new LodestoneParserException("Could not connect to Lodestone", e);
		}
		
		
		// Parse members on the page
		CharacterParser characterParser = new CharacterParser(rootUrl);
		
		Elements members = html.select("div.base_body div.player_name_area div.name_box > a");
		for (Element member : members) {
			// format should be "/lodestone/character/id/
			String characterId = member.attr("href").split("/")[3];
			LSCharacter character = characterParser.getCharacterById(characterId);
			freeCompany.addMember(character);
		}
		
		// Check if we need to continue to next page
		if (goToNextPage) {
			Elements pagingEnd = html.select("div.mb10 > div.pager > div.pagination.clearfix > div.current_list > span.show_end");
			ParserUtils.checkElementsSize(pagingEnd, 1, "Cannot find HTML element for FC members paging");
			int end = ParserUtils.extractNumeric(pagingEnd.text());
			
			Elements pagingTotal = html.select("div.mb10 > div.pager > div.pagination.clearfix > div.current_list > span.total");
			ParserUtils.checkElementsSize(pagingTotal, 1, "Cannot find HTML element for FC members paging");
			int total = ParserUtils.extractNumeric(pagingTotal.text());
			
			if (end < total) {
				parseMembers(freeCompany, membersUrl, pageNumber + 1, true);
			}
		}
	}

}
