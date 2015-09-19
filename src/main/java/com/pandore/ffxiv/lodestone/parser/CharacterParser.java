package com.pandore.ffxiv.lodestone.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pandore.ffxiv.lodestone.entity.LSCharacter;
import com.pandore.ffxiv.lodestone.entity.LSItem;

import exceptions.LodestoneParserException;

public class CharacterParser {
	
	private static Logger logger = LoggerFactory.getLogger(CharacterParser.class);
	private boolean verbose = true;
	
	private final String rootUrl;
	
	public CharacterParser(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	
	public void getCharacterByName(String name, String world) {
		// TODO
	}
	
	public LSCharacter getCharacterById(String characterId) throws LodestoneParserException {
		if (verbose) {
			logger.info("Parsing Lodestone for character " + characterId);
		}
		
		// result
		LSCharacter character = new LSCharacter();
		character.setId(characterId);
		
		// build URL and get HTML
		String url = rootUrl + URLS.CHARACTER + characterId;
		Document html;
		try {
			html = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new LodestoneParserException("Could not connect to Lodestone", e);
		}
		
		// Get name, world, title
		parsePlate(character, html);
		
		// Get gear and level
		parseGearAndLevel(character, html);
		
		// Get all classes
		// TODO, implement and call parseClasses()
		
		// Yay!
		return character;
	}
	
	
	/**
	 * Parse name, world, and title
	 */
	private void parsePlate(LSCharacter character, Document html) throws LodestoneParserException {
		Elements playerNameClass = html.getElementsByClass("player_name_txt");
		ParserUtils.checkElementsSize(playerNameClass, 1, "Cannot find html for character name");
		
		// get character name
		Elements h2 = playerNameClass.first().getElementsByTag("h2");
		ParserUtils.checkElementsSize(h2, 1, "Cannot find html for character name");
		
		Elements playerNameTag = h2.first().getElementsByTag("a");
		ParserUtils.checkElementsSize(playerNameTag, 1, "Cannot find html for character name");
		character.setName(playerNameTag.text());
		
		// get character world
		Elements playerWorldTag = playerNameClass.first().getElementsByTag("span");
		ParserUtils.checkElementsSize(playerWorldTag, 1, "Cannot find HTML for character world");
		character.setWorld(playerWorldTag.text().replace("(", "").replace(")", "")); // remove parenthesis: "(Ragnarok)" -> "Ragnarok"
		
		// get character title
		Elements playerTitleTag = h2.first().getElementsByClass("chara_title");
		if (playerTitleTag.size() > 0) { // a character can have no title
			ParserUtils.checkElementsSize(playerTitleTag, 1, "Cannot find HTML for character title");
			character.setTitle(playerTitleTag.text());
		}
	}
	
	private void parseClasses(LSCharacter character, Document html) throws LodestoneParserException {
		// TODO
	}
	
	/**
	 * Parse level, weapon, left and right side gear
	 * @param character
	 * @param html
	 * @throws LodestoneParserException
	 */
	private void parseGearAndLevel(LSCharacter character, Document html) throws LodestoneParserException {
		
		// class info and weapon
		Elements classInfo = html.select("div.contents:not(.none) > div.clearfix > div#param_class_info_area.clearfix");
		ParserUtils.checkElementsSize(classInfo, 1, "Cannot find HTML for character current class info");
		
		// get level
		Elements level = classInfo.select("div#class_info > div.level");
		ParserUtils.checkElementsSize(level, 1, "Cannot find HTML for character level");
		character.setLevel(ParserUtils.extractNumeric(level.first().text()));
		
		// get weapon
		Elements weapon = classInfo.select("div.item_detail_box");
		ParserUtils.checkElementsSize(weapon, 1, "Cannot find HTML for character weapon");
		LSItem weaponItem = parseItem(weapon.first());
		character.setWeapon(weaponItem);
		
		// get left and right side gear
		Elements itemColumns = html.select("div.contents:not(.none) > div.clearfix > div.param_right_area > div#chara_img_area.clearfix > div.icon_area");
		ParserUtils.checkElementsSize(itemColumns, 2, "Cannot find HTML for character left and right side gear");
		List<LSItem> gearSet = new ArrayList<LSItem>();
		for (Element column : itemColumns) { // loop on 2 colums : left and right side
			// items will contain at max 6 items for left side,
			// and at max 7 items for right side (1 shield, 5 accessories, 1 job stone).
			// This selector will NOT retrieve empty slots
			Elements items = column.select("div.item_detail_box");
			for (Element item : items) {
				LSItem gear = parseItem(item);
				gearSet.add(gear);
			}
		}
		character.setGearSet(gearSet);
	}
	
	private LSItem parseItem(Element element) throws LodestoneParserException {
		LSItem item = new LSItem();
		
		// get item name
		Elements itemName = element.select("h2.item_name");
		ParserUtils.checkElementsSize(itemName, 1, "Cannot find HTML for character item name");
		item.setName(itemName.first().text());
		
		// get item level
		Elements itemLevel = element.select("div.pt3.pb3");
		ParserUtils.checkElementsSize(itemLevel, 1, "Cannot find HTML for character item level");
		item.setLevel(ParserUtils.extractNumeric(itemLevel.first().text()));
		
		// get item category
		Elements itemCategory = element.select("h3.category_name");
		ParserUtils.checkElementsSize(itemCategory, 1, "Cannot find HTML for character item category");
		item.setCategory(itemCategory.first().text());
		
		// get item classes restriction
		Elements itemClasses = element.select("span.class_ok");
		ParserUtils.checkElementsSize(itemClasses, 1, "Cannot find HTML for character item classes");
		item.setClasses(Arrays.asList(itemClasses.first().text().split(" ")));
		
		return item;
	}
	
}
