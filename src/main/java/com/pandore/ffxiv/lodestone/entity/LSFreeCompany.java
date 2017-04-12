package com.pandore.ffxiv.lodestone.entity;

import java.util.ArrayList;
import java.util.List;

public class LSFreeCompany {

	private String id;
	private String name;
	private String tag;
	private String world;
	private String gc;
	private int rank;
	
	private List<LSCharacter> members = new ArrayList<LSCharacter>();

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

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getWorld() {
		return world;
	}
	public void setWorld(String world) {
		this.world = world;
	}

	public String getGc() {
		return gc;
	}
	public void setGc(String gc) {
		this.gc = gc;
	}

	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}

	public List<LSCharacter> getMembers() {
		return members;
	}
	public void setMembers(List<LSCharacter> members) {
		this.members = members;
	}
	public void addMember(LSCharacter member) {
		this.members.add(member);
	}
	@Override
	public String toString() {
		return "LSFreeCompany [id=" + id + ", name=" + name + ", tag=" + tag
				+ ", world=" + world + ", rank=" + rank
				+ ", active members=" + members.size() + "]";
	}
}
