package my_module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Easy way to filter and sort VO elements in java List.
 * 
 * {@code VOFilter#addFilter(String, Object, FilterOperator)} to add filter condition.
 * {@code VOFilter#addSorter(String, boolean)} to add sorting condition.
 * 
 * Apply filtering or sorting {@code VOFilter#doFiltering(List)}, {@code VOFilter#doSorting(List)}, {@code VOFilter#doFilteringAndSorting(List)}
 */
public class VOFilter<T> {
	private Class<T> clazz;
	private ItemFilterMaker<T> filterMaker;
	private List<ItemSorter<T>> sorterList = new ArrayList<>();
	private List<ItemFilter<T>> filterList = new ArrayList<>();
	
	public VOFilter(Class<T> c) {
		this.clazz = c;
		filterMaker = new ItemFilterMaker<T>(clazz);
	}
	
	public List<T> doFiltering(List<T> sourceData) {
		if(filterList == null || filterList.size() == 0) {
			return sourceData;
		}
		List<T> result = new ArrayList<T>(sourceData);
		for(ItemFilter<T> filter : filterList) {
			result = this.applyFilter(result, filter);
		}
		return result;
	}
	
	public List<T> doSorting(List<T> sourceData) {
		if(sorterList == null || sorterList.size() == 0) {
			return sourceData;
		}
		List<T> list = new ArrayList<T>(sourceData);
		Comparator<T> comparator = this.getChainedComparator(sorterList);
		return list.stream().sorted(comparator).collect(Collectors.toList());
	}
	
	public List<T> doFilteringAndSorting(List<T> sourceData) {
		List<T> result = new ArrayList<T>(sourceData);
		result = this.doFiltering(result);
		result = this.doSorting(result);
		return result;
	}
	
	public VOFilter<T> addSorter(String fieldName, boolean descendingOrder) {
		if (sorterList == null) {
			sorterList = new ArrayList<ItemSorter<T>>();
		}
		sorterList.add(filterMaker.createItemSorter(fieldName, descendingOrder));
		return this;
	}
	
	public VOFilter<T> addFilter(String fieldName, Object filterValue, FilterOperator operation) {
		if(filterList == null) {
			filterList = new ArrayList<ItemFilter<T>>();
		}
		filterList.add(filterMaker.createItemFilter(fieldName, filterValue, operation));
		return this;
	}
	
	public void resetSorterList() {
		sorterList = null;
	}
	
	public void resetFilterList() {
		filterList = null;
	}
	public List<ItemSorter<T>> getSorterList() {
		return new ArrayList<ItemSorter<T>>(sorterList);
	}
	
	public List<ItemFilter<T>> getFilterList() {
		return new ArrayList<ItemFilter<T>>(filterList);
	}
	
	public List<T> applyFilter(List<T> sourceData, ItemFilter<T> filter) throws IllegalArgumentException {
		List<T> data = new ArrayList<T>(sourceData);
		if(data == null || data.size() ==0) {
			throw new IllegalArgumentException("target list is null or empty");
		}
		Method getter = filter.getGetter();
		Object filterValue = filter.getFilterValue();
		FilterOperator operation = filter.getOperation();
		List<T> result = data.stream().filter(element->{
			boolean filterCheck = false;
			Object targetValue = null;
			try{
				targetValue = getter.invoke(element);
			} catch (IllegalAccessException e){
				//TODO
				
			} catch (IllegalArgumentException e){
				//TODO
			} catch(InvocationTargetException e) {
				//TODO
			}
			switch(operation) {
				case EQUAL:
					filterCheck = targetValue.equals(filterValue);
					break;
				case GREATER_THAN:
					if(Comparable.class.isAssignableFrom(targetValue.getClass())) {
						ComparableField tValue = new ComparableField(targetValue);
						ComparableField fValue = new ComparableField(filterValue);
						filterCheck = (tValue.compareTo(fValue) > 0) ? true: false; 
					} else {
						filterCheck = false;
					}
					break;
				case GREATER_THAN_OR_EQUAL:
					if(targetValue.equals(filterValue)){
						filterCheck = false;
					} else if (Comparable.class.isAssignableFrom(targetValue.getClass())) {
						ComparableField tValue = new ComparableField(targetValue);
						ComparableField fValue = new ComparableField(filterValue);
						filterCheck = (tValue.compareTo(fValue) > 0) ? true:false;
					} else {
						filterCheck = false;
					}
					break;
				case LESS_THAN:
					if(Comparable.class.isAssignableFrom(targetValue.getClass())) {
						ComparableField tValue = new ComparableField(targetValue);
						ComparableField fValue = new ComparableField(filterValue);
						filterCheck = (tValue.compareTo(fValue) < 0) ? true: false; 
					} else {
						filterCheck = false;
					}
					break;
				case LESS_THAN_OR_EQUAL:
					if(targetValue.equals(filterValue)) {
						filterCheck = false;
					} else if (Comparable.class.isAssignableFrom(targetValue.getClass())) {
						ComparableField tValue = new ComparableField(targetValue);
						ComparableField fValue = new ComparableField(filterValue);
						filterCheck = (tValue.compareTo(fValue) < 0) ? true:false;
					} else {
						filterCheck = false;
					}
					break;
				case NOT_EQUAL:
					filterCheck = !(targetValue.equals(filterValue));
					break;
			}
			return filterCheck;
		}).collect(Collectors.toList());
		return result;
	}
	
	Comparator<T> getComparator(ItemSorter<T> sorter) throws IllegalStateException {
		return new Comparator<T>() {
			Method m = sorter.getGetter();
			boolean descendingYn = sorter.isDescending();
			@Override
			public int compare(T o1, T o2){
				ComparableField v1 = null;
				ComparableField v2 = null;
				try {
					v1 = new ComparableField(m.invoke(o1));
					v2 = new ComparableField(m.invoke(o2));
				} catch (IllegalAccessException e) {
					//TODO
				} catch (IllegalArgumentException e) {
					//TODO
				} catch (InvocationTargetException e) {
					//TODO
				}
				if (v1 == null || v2 == null) {
					throw new IllegalStateException("cannot get the field value");
				}
				if(!descendingYn) {
					return v1.compareTo(v2);
				} else {
					return v2.compareTo(v1);
				}
			}
		};
	}

	Comparator<T> getChainedComparator(List<ItemSorter<T>> sorters) {
		BinaryOperator<Comparator<T>> comparatorChaining = new BinaryOperator<Comparator<T>>() {
			@Override
			public Comparator<T> apply(Comparator<T> t, Comparator<T> u) {
			return t.thenComparing(u);
			}
		};
		Optional<Comparator<T>> result = sorters.stream().map(e->this.getComparator(e)).reduce(comparatorChaining);
		return result.orElseThrow(IllegalArgumentException::new);
	}
	
}
