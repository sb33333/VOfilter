package my_module;

import java.time.LocalDate;
import java.util.List;

public class MockDataGenerator {
	public static LocalDate getRandomDate(LocalDate startDate, int dayOffset) {
		if(startDate.toEpochDay() + dayOffset < 0) {
			throw new IllegalArgumentException("negative offset value is too large");
		}
		int randomOffset = (int) Math.floor(Math.random() * (dayOffset + 1));
		return LocalDate.ofEpochDay(startDate.toEpochDay() + randomOffset);
	}

	public static LocalDate getRandomDate(int dayOffset) {
		return getRandomDate(LocalDate.now(), dayOffset);
	}
	
	public static Object getRandomElementFromList(List<?> list) {
		if(list == null || list.isEmpty()) {
			return null;
		}
		int size = list.size();
		return list.get((int)Math.floor(Math.random()*size));
	}
	
	public static int getRandomInt(int start, int end){
		if(start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		int offset = 0;
		offset = end - start;
		return (int)(Math.floor(Math.random() * (offset+1)) +start);
	}

	public static int getRandomInt(int end) {
		return getRandomInt(0, end);
	}
	
	public static char[] getRandomCharacters() {
		return getRandomCharacters(10);
	}
	
	public static char[] getRandomCharacters(int length) {
		if(length < 1) {
			throw new IllegalArgumentException("length must be larger than 0");
		}
		char[] result = new char[length];
		for (int i = 0; i <length; i++) {
			char c = 0;
			double selector = Math.random();
			if (selector < 0.3){
				c= (char)getRandomInt(48, 57);
			} else if (selector < 0.4){
				c= (char)getRandomInt(65, 90);
			} else if (selector < 0.8) {
				c=(char)getRandomInt(97, 122);
			} else if (selector < 1) {
				c= (char)getRandomInt(44032, 55203);
			}
		result[i] = c;
		}
		return result;
	}
}
