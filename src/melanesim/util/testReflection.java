package melanesim.util;

import java.lang.reflect.*;

public class testReflection {
	public static void main(String args[]) {
		try {
			Class<?> c = Class.forName("thing.ground.C_SoilCellMarine");
			Method m[] = c.getDeclaredMethods();
			for (int i = 0; i < m.length; i++)
				System.out.println(m[i].toString());
		} catch (Throwable e) {
			System.err.println(e);
		}
	}
}
