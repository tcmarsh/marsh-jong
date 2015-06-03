

public class Helper {
	public static boolean isInteger(String testString) {
		try {
			Integer.parseInt(testString);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
}
