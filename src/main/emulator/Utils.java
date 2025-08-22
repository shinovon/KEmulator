package emulator;

public class Utils {

	// copied from JDK 24
	public static String translateEscapes(String input) {
		if (input.isEmpty()) {
			return "";
		} else {
			char[] chars = input.toCharArray();
			int length = chars.length;
			int from = 0;
			int to = 0;

			while(from < length) {
				char ch = chars[from++];
				if (ch == '\\') {
					ch = from < length ? chars[from++] : 0;
					switch (ch) {
						case '\n':
							continue;
						case '\r':
							if (from < length && chars[from] == '\n') {
								++from;
							}
							continue;
						case '"':
						case '\'':
						case '\\':
							break;
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
							int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);

							int code;
							for(code = ch - 48; from < limit; code = code << 3 | ch - 48) {
								ch = chars[from];
								if (ch < '0' || '7' < ch) {
									break;
								}

								++from;
							}

							ch = (char)code;
							break;
						case 'b':
							ch = '\b';
							break;
						case 'f':
							ch = '\f';
							break;
						case 'n':
							ch = '\n';
							break;
						case 'r':
							ch = '\r';
							break;
						case 's':
							ch = ' ';
							break;
						case 't':
							ch = '\t';
							break;
						default:
							throw new IllegalArgumentException();
					}
				}

				chars[to++] = ch;
			}

			return new String(chars, 0, to);
		}
	}
}
