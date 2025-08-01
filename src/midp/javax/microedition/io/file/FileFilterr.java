package javax.microedition.io.file;

import java.io.File;
import java.io.FileFilter;

public final class FileFilterr implements FileFilter {
	private boolean includeHidden;
	private String filter;

	public FileFilterr(String filter, boolean includeHidden) {
		super();
		this.includeHidden = includeHidden;
		this.filter = filter;
	}

	public boolean accept(File file) {
		if (filter.equalsIgnoreCase("*")) {
			return includeHidden || !file.isHidden();
		}
		return (includeHidden || !file.isHidden()) && matchString(file.getName(), filter);
	}

	private static String trimFilter(String aFilter)
	{
		String filter = aFilter;
		// If the name contains escaped sequences, remove them: %20
//		filter = FileUTF8Handler.decode(aFilter);
		filter = filter.trim();
		return filter;
	}

	public static boolean matchString(String aFilter, String aName)
	{

		String filter = aFilter;
		filter = trimFilter(filter);

		if (filter.equals("*"))
		{
			return true;
		}

		// Since only "*" is used as wild card, we split the strings taking
		// "*" as delimiter.
		String[] tokens = filter.split("\\*");
		int prevTokenEndIndex = 0;
		boolean matchFlag = true;

		prevTokenEndIndex = 0;
		matchFlag = true;

		int j = 0;
		// Check if the name contains all the tokens in the filter string.
		for (j = 0; j < tokens.length; j++)
		{
			if (tokens[j].equals(""))
			{
				// happens if filter starts and ends with "*"
				continue;
			}

			// Check if all the tokens are present

			int index = aName.indexOf(tokens[j], prevTokenEndIndex);
			if (-1 != index)
			{
				if (index >= prevTokenEndIndex)
				{
					prevTokenEndIndex = aName.indexOf(tokens[j])
							+ tokens[j].length();

					matchFlag = true;
				}
				else
				{
					matchFlag = false;
					break;
				}
			}
			else
			{
				matchFlag = false;
				break;
			}
		}

		// We now need to check if the start and end of the name match that of
		// the filter given. In case the filter does not start and end with "*",
		// we must ensure that the name starts and ends with the same tokens as
		// specified in the filter.
		if (matchFlag) // We proceed only if all the tokens are present in name
		{
			if (!filter.startsWith("*"))
			{
				// IF the filter does not start with "*" then name should start
				// with first token
				if (!aName.startsWith(tokens[0]))
				{
					matchFlag = false;
				}
			}

			if (!filter.endsWith("*"))
			{
				// IF the filter does not end with "*" then name should end
				// with last token
				if (!aName.endsWith(tokens[tokens.length - 1]))
				{
					matchFlag = false;
				}
			}
		}


		return matchFlag;
	}
}
