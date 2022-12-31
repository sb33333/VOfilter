package my_module;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import my_module.entity.Car;

public class FilterTest {
	
	List<Car> carList;
	VOFilter<Car> listFilter = new VOFilter<>(Car.class);

	@Before
	public void setup() {
		String[] companies = {"c-1", "c-2", "c-3"};
		carList = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			Car c = new Car();
			c.setCompany((String) MockDataGenerator.getRandomElementFromList(Arrays.asList(companies)));
			c.setName(String.format("name-%d", i));
			c.setProductionDate(MockDataGenerator.getRandomDate(-100));
			carList.add(c);
		}

		// for(Car cc : carList) {
		// 	System.out.println(cc.toString());
		// }
	}

	@After
	public void reset() {
		carList = null;
	}

	@Test
	public void filterTest1() {
		System.out.println("filterTest1()");
		listFilter.addFilter("company", "c-1", FilterOperator.EQUAL);

		List<Car> result = listFilter.doFiltering(carList);
		boolean check = false;
		for (Car c : result) {
			System.out.println(c);
			if (!"c-1".equals(c.getCompany())) {
				check = true;
			}
		}
		Assert.assertFalse(check);
	}

	@Test
	public void filterTest2() {
		System.out.println("filterTest2()");
		listFilter.addFilter("company", "c-1", FilterOperator.NOT_EQUAL);

		List<Car> result = listFilter.doFiltering(carList);
		boolean check = false;
		for (Car c : result) {
			System.out.println(c);
			if ("c-1".equals(c.getCompany())) {
				check = true;
			}
		}
		Assert.assertFalse(check);
	}

	@Test
	public void filterTest3() {
		LocalDate targetDate = MockDataGenerator.getRandomDate(-30);
		System.out.println("filterTest3() " + targetDate);
		listFilter.addFilter("productionDate", targetDate, FilterOperator.LESS_THAN_OR_EQUAL);

		List<Car> result = listFilter.doFiltering(carList);
		boolean check = false;
		for (Car c : result) {
			System.out.println(c);
			if (c.getProductionDate().isAfter(targetDate)) {
				check = true;
			}
		}
		Assert.assertFalse(check);
	}

	@Test
	public void sortingTest1() {
		System.out.println("sortingTest1()");
		listFilter.addSorter("company", false);

		List<Car> result = listFilter.doSorting(carList);
		for (Car c : result) {
			System.out.println(c);
		}
	}

	@Test
	public void sortingTest2() {
		System.out.println("sortingTest2()");
		listFilter.addSorter("company", false);
		listFilter.addSorter("productionDate", true);

		List<Car> result = listFilter.doSorting(carList);
		for (Car c : result) {
			System.out.println(c);
		}
	}

	@Test
	public void filteringAndSortingTest1() {
		LocalDate targetDate = MockDataGenerator.getRandomDate(-100);
		System.out.println("filteringAndSortingTest1() " + targetDate);
		listFilter.addFilter("productionDate", targetDate, FilterOperator.GREATER_THAN);
		listFilter.addSorter("company", false);
		listFilter.addSorter("productionDate", true);

		List<Car> result = listFilter.doFilteringAndSorting(carList);
		for (Car c : result) {
			System.out.println(c);
		}

	}
}
