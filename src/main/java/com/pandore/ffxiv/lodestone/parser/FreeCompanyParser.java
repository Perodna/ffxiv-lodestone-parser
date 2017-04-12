package com.pandore.ffxiv.lodestone.parser;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.entity.LSFreeCompany;

import exceptions.LodestoneParserException;
import exceptions.UnexpectedHtmlStructureException;

public class FreeCompanyParser {
	
	private static Logger logger = LoggerFactory.getLogger(FreeCompanyParser.class);
	private boolean verbose = true;
	
	private final String rootUrl;
	
	public FreeCompanyParser(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	
	/**
	 * 
	 * @param freeCompanyId
	 * @param parseMembers fetch company members. If one member cannot be fetched for any reason, it will keep trying to fetch the other members
	 * @return
	 * @throws LodestoneParserException
	 */
	public LSFreeCompany getFreeCompanyById(String freeCompanyId, boolean parseMembers) throws LodestoneParserException {
		if (verbose) {
			logger.info("Parsing Lodestone for free company {}", freeCompanyId);
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
		parseProfile(freeCompany, html);
		
		
		// --- Get members
		if (parseMembers) {
			String membersUrl = rootUrl + URLS.FREE_COMPANY + freeCompanyId + URLS.FREE_COMPANY_MEMBERS_SUFFIX;
			parseMembers(freeCompany, membersUrl);
		}

		if (verbose) {
			logger.info("Parsing done for free company {}", freeCompanyId);
		}

		return freeCompany;
	}
	
	
	private void parseProfile(LSFreeCompany freeCompany, Document html) throws LodestoneParserException {
		if (verbose) {
			logger.info("Parsing profile for free company {}", freeCompany.getId());
		}

		// get FC Profile tab
		Elements fcProfile = html.select("div.ldst__main");
		ParserUtils.checkElementsSize(fcProfile, 1, "Cannot find HTML for free company profile");

		Elements profileWindows = fcProfile.select("div.ldst__window");
		ParserUtils.checkElementsSize(profileWindows, 2, "Cannot find HTML for free company profile sub-windows");
		// 1st window is the Company Info, 2nd one is the Focus

		// get FC company info
		Element fcInfo = profileWindows.first();
		parseCompanyInfo(freeCompany, fcInfo);
	}

	private void parseCompanyInfo(LSFreeCompany freeCompany, Element fcInfo) throws UnexpectedHtmlStructureException {
		// get GC and world
		// SE uses p.entry__free_company__gc for both GC and world... World is the 2nd one.
		Elements gcElements = fcInfo.select("div.entry > a.entry__freecompany > div > p.entry__freecompany__gc");
		ParserUtils.checkElementsSize(gcElements, 2, "Cannot find HTML for free company GC and world");

		Element gc = gcElements.first();
		freeCompany.setGc(gc.text());

		Element world = gcElements.last();
		freeCompany.setWorld(world.text()); //.replace("(", "").replace(")", ""));  // remove parenthesis: "(Ragnarok)" -> "Ragnarok"
		
		//get name
		Elements name = fcInfo.select("p.freecompany__text__name");
		ParserUtils.checkElementsSize(name, 1, "Cannot find HTML for free company name");
		freeCompany.setName(name.first().text());

		//get tag
		Elements tag = fcInfo.select("p.freecompany__text__tag");
		ParserUtils.checkElementsSize(name, 1, "Cannot find HTML for free company tag");
		freeCompany.setTag(tag.first().text().replace("«", "").replace("»", ""));
		
		// get rank
		Elements children = fcInfo.children();
		boolean rankHeaderFound = false;
		int i = 0;
		while (i < children.size() && !rankHeaderFound) {
			Element child = children.get(i);
			if (child.hasClass("heading--lead") && "Rank".equals(child.text())) {
				// next element is the value for Rank
				rankHeaderFound = true;
			}
			i++;
		}
		Element rank = children.get(i);
		if (rank == null) {
			throw new UnexpectedHtmlStructureException("Cannot find rank tag");
		}
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
	
	private void parseMembers(LSFreeCompany freeCompany, String membersUrl, int pageNumber, boolean continueToNextPage) throws LodestoneParserException {
		if (verbose) {
			logger.info("Parsing page {} of members for free company {}", pageNumber, freeCompany.getId());
		}

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

		Elements charListWindow = html.select("div.ldst__window");
		ParserUtils.checkElementsSize(charListWindow, 1, "Cannot find HTML element for FC members window");

		Elements members = charListWindow.first().select("ul:not(.btn__pager) > li > a");
		for (Element member : members) {
			// format should be "/lodestone/character/id/
			String characterId = member.attr("href").split("/")[3];
			try {
				LSCharacter character = characterParser.getCharacterById(characterId);
				freeCompany.addMember(character);
			} catch (LodestoneParserException e) {
				logger.error("Could not fetch character from lodestone [id " + characterId + "]", e);
			}
		}

		if (continueToNextPage) {
			// Check if there is a next page
			Elements pagePosition = charListWindow.first().select("ul.btn__pager > li.btn__pager__current");
			// there should be 2 elements, at tge top and bottom of the page, take any
			ParserUtils.checkElementsSize(pagePosition, 2, "Cannot find HTML element for FC members paging");

			String[] pages = pagePosition.first().text().split(" of "); // "Page 2 of 3"
			int currentPage = ParserUtils.extractNumeric(pages[0]);
			int lastPage = ParserUtils.extractNumeric(pages[1]);

			if (currentPage < lastPage) {
				parseMembers(freeCompany, membersUrl, pageNumber + 1, continueToNextPage);
			}
		}
	}

}
