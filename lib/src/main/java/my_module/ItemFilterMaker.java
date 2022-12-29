package my_module;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemFilterMaker<T> {
	private Class<? extends T> clazz;
	private Map<String, Field> declaredFields;
	private Map<String, Field> comparableFields;
	private Map<String, Method> getterMethods;
	
	public ItemFilterMaker(Class<? extends T> c) {
		this.clazz = c;
		declaredFields = Arrays.asList(clazz.getDeclaredFields()).stream()
		.collect(Collectors.toMap(Field::getName, Function.identity()));
		comparableFields =declaredFields.entrySet().stream()
		.filter(element->{
			Class<?> elementClass = element.getValue().getType();
			return Comparable.class.isAssignableFrom(elementClass) ? true : false;})
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		getterMethods = Arrays.asList(clazz.getDeclaredMethods()).stream()
		.filter(element -> {
			String name = element.getName().replace("get", "");
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			return (comparableFields.containsKey(name))?true:false;})
		.collect(Collectors.toMap(Method::getName, Function.identity()));
	}
	
	public ItemSorter<T> createItemSorter(String fieldName, boolean descendingOrder) throws IllegalArgumentException {
		if(!declaredFields.containsKey(fieldName)){
			throw new IllegalArgumentException("cannot find target field");
		}
		if(!comparableFields.containsKey(fieldName)){
			throw new IllegalArgumentException("target field is not comparable");
		}
		String methodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
		if (!getterMethods.containsKey(methodName)) {
			throw new IllegalArgumentException("cannot find getter method on the target field");
		}
		Method getter = getterMethods.get(methodName);
		return new ItemSorter<>(getter, descendingOrder);
	}

	public ItemFilter<T> createItemFilter(String fieldName, Object filterValue, FilterOperator operation) {
		if (!declaredFields.containsKey(fieldName)){
			throw new IllegalArgumentException("cannot find target field");
		}
		if(operation != FilterOperator.EQUAL && operation != FilterOperator.NOT_EQUAL && !comparableFields.containsKey(fieldName)) {
			throw new IllegalArgumentException("target field is not comparable");
		}
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method getter = getterMethods.get(methodName);
		return new ItemFilter<>(fieldName, filterValue, operation, getter);
	}

	public void printInformation() {
		System.out.println("declaredFields");
		for(java.util.Map.Entry<String, Field> entry : declaredFields.entrySet()) {
			System.out.println(String.format("  %s // %s", entry.getKey(), entry.getValue()));
		}
	}

}	