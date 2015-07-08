package com.strato.publish.common.model;

public class VersionInfo implements Comparable<VersionInfo>{
	public VersionInfo(int first,int second ,int third,String last){
		this.first=first;
		this.second = second;
		this.third = third;
		this.last=last;
	}
	private int first;
	private int second;
	private int third;
	String last;
	@Override
	public int compareTo(VersionInfo o){
		if(first==o.first){
			if(second==o.second){
				if(third==o.third){
					return 0;
				}
				else{
					return third-o.third;
				}
			}
			else{
				return second-o.second;
			}
		}else {
			return first-o.first;
		}	
	}
	
	public int getFirst() {
		return first;
	}
	public void setFirst(int first) {
		this.first = first;
	}
	public int getSecond() {
		return second;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public int getThird() {
		return third;
	}
	public void setThird(int third) {
		this.third = third;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
	@Override
	public String toString() {
		StringBuffer buf=new StringBuffer();
		buf.append(first);
		buf.append(".").append(second);
		buf.append(".").append(third);
		if(last!=null){
			buf.append(".").append(last);
		}
		return buf.toString();
	}
	
	
}
