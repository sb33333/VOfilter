package my_module;

import java.lang.reflect.Method;

public class ItemSorter<T> {
	private boolean descending;
	private Method getter;
	
	ItemSorter (Method getterMethod, boolean descending) {
	this.descending = descending;
	this.getter = getterMethod;
	}
	
	public boolean isDescending() {
		return descending;
	}
	public Method getGetter() {
		return getter;
	}

}