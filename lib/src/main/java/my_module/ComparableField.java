package my_module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ComparableField implements Comparable<ComparableField> {
	private Object field;
	private Class<?> fieldClass;
	private Method compareMethod;
	
	public Object getField(){
		return this.field;
	}
	
	public ComparableField(Object field) {
		if (!(field instanceof Comparable)) {
			throw new IllegalArgumentException("not comparable field");
		}
		this.field = field;
		fieldClass = field.getClass();
		List<Method> methods = Arrays.asList(field.getClass().getDeclaredMethods());
		for (Method m : methods) {
			if(m.getName().equals("compareTo")) {
				compareMethod = m;
				break;
			}
		}
		return;
	}
	
	public int compareTo(ComparableField o) {
		Object targetField = o.getField();
		if (!fieldClass.isAssignableFrom(targetField.getClass())) {
			throw new IllegalArgumentException("comparing target must be same type");
		}
		try {
			return (int)compareMethod.invoke(field, targetField);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println(e.getClass() + "/" + e.getMessage());
			return 0;
		}
	}
}
