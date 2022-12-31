package my_module;

import java.lang.reflect.Method;

public class ItemFilter<T> {
	private Object filterValue;
	private FilterOperator operation;
	private Method getter;
	
	ItemFilter(String fieldName, Object filterValue, FilterOperator operation, Method getter) throws IllegalArgumentException {
		this.getter = getter;
		this.filterValue = filterValue;
		if(!(filterValue instanceof Comparable)) {
			if(operation != FilterOperator.EQUAL && operation != FilterOperator.NOT_EQUAL){
				throw new IllegalArgumentException("target field is not comparable");
			}
		}
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "ItemFilter [filterValue=" + filterValue + ", operation=" + operation + "]";
	}
	public Object getFilterValue() {
		return filterValue;
	}
	public FilterOperator getOperation() {
		return operation;
	}
	public Method getGetter() {
		return getter;
	}
}
