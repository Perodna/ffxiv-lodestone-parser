package com.pandore.ffxiv.lodestone.entity;

import java.util.List;


public class LSCharacter {
	
	private String id;
	
	private String name;
	private String world;
	private String title;
	
	private int level;
	private int itemLevel;
	private LSItem weapon;
	private List<LSItem> gearSet;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	public LSItem getWeapon() {
		return weapon;
	}
	public void setWeapon(LSItem weapon) {
		this.weapon = weapon;
		computeItemLevel();
	}
	
	public List<LSItem> getGearSet() {
		return gearSet;
	}
	public void setGearSet(List<LSItem> gearSet) {
		this.gearSet = gearSet;
		computeItemLevel();
	}
	public void addGear(LSItem gear) {
		this.gearSet.add(gear);
		computeItemLevel();
	}
	
	public int getItemLevel() {
		return itemLevel;
	}
	
	private void computeItemLevel() {
		int totalLevel = 0;
		int numberOfItems = 0;
		
		
		if (weapon != null) {
			totalLevel += weapon.getLevel();
			numberOfItems++;
		}
		
		if (gearSet != null) {
			for (LSItem gear : gearSet) {
				if (!gear.isJobStone()) {
					totalLevel += gear.getLevel();
					numberOfItems++;
				}
			}
		}
		
		itemLevel = Math.round(totalLevel / numberOfItems); 
		
//		// round up of totalLevel / numberOfItems
//		itemLevel = (totalLevel + numberOfItems - 1) / numberOfItems;
	}
	
	public String getClassOrJob() {
		LSItem jobStone = null;
		
		// Use the job stone if there is one
		if (gearSet != null)
		for (LSItem item : gearSet) {
			if (item.isJobStone()) {
				jobStone = item;
				break;
			}
		}
		
		if (jobStone !=null) {
			String fullJobName = jobStone.getName().replace("Soul of the ", "");
			switch (fullJobName) {
				// Battle classes
				case "Paladin":
					return "PLD";
				case "Warrior":
					return "WAR";
				case "Dark Knight":
					return "DRK";
				case "White Mage":
					return "WHM";
				case "Scholar":
					return "SCH";
				case "Astrologian":
					return "AST";
				case "Monk":
					return "MNK";
				case "Dragoon":
					return "DRG";
				case "Ninja":
					return "NIN";
				case "Bard":
					return "BRD";
				case "Machinist":
					return "MCH";
				case "Black Mage":
					return "BLM";
				case "Summoner":
					return "SMN";
					
				// TODO gathering classes
				// TODO crafting classes
		
				default:
					return fullJobName;
			}
		}
		
		// If there is no job stone, use the class of the weapon (it can't be the job of the weapon)
		if (weapon == null || weapon.getClasses() == null || weapon.getClasses().isEmpty()) {
			return null;
		}
		return weapon.getClasses().get(0); // the first one should be the base class
	}
	
	@Override
	public String toString() {
		return "LSCharacter [id=" + id + ", name=" + name + ", world=" + world
				+ ", title=" + title +  ", class/job=" + getClassOrJob() + ", level=" + level + ", itemLevel=" + itemLevel + "]";
	}
}
