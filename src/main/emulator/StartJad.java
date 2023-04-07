package emulator;

public class StartJad {
	
	public static void main(String[] args) {
		if(args.length > 0) {
			String s = Emulator.getMidletJarUrl(args[0]);
			System.out.println(args[0]);
			System.out.println(s);
			Emulator.main(new String[] {"-jad", args[0], "-jar", s});
		} else {
			Emulator.main(new String[0]);
		}
	}

}
