/*
* Copyright (c) 2009 Nokia Corporation and/or its subsidiary(-ies).
* All rights reserved.
* This component and the accompanying materials are made available
* under the terms of "Eclipse Public License v1.0"
* which accompanies this distribution, and is available
* at the URL "http://www.eclipse.org/legal/epl-v10.html".
*
* Initial Contributors:
* Nokia Corporation - initial contribution.
*
* Contributors:
*
* Description:
*
*/
package javax.microedition.lcdui;

import emulator.lcdui.c;

import java.util.Arrays;
import java.util.Vector;

/**
 * Utility class used to wrap a string for StringItem layout.
 */
final class StringWrapper
{

    private static final String DELIM_STR = " ,.;:!?-\n";

    private StringWrapper()
    {
        super();
    }

    /**
     * Method to be called by StringItemLayouter to provide correct wrapping of
     * a single String to a Vector of strings.
     *
     * @param inputString Contents of a StringItem
     * @param stringItemFont StringItem's font
     * @param fullWidth Full width of the Form, in pixels
     * @param freeWidth Free width is the current row of Form layout
     * @return Vector of Strings, each element to be placed in one layout row
     */
    static Vector wrapString(String inputString,
                             Font stringItemFont,
                             boolean leftToRightLayout,
                             int fullWidth,
                             int freeWidth)
    {
        if(leftToRightLayout)
        {
            // if left-to-right then just call wrapping method
            return doWrapString(inputString, stringItemFont, fullWidth,
                                freeWidth);
        }
        else
        {
            // if right-to-left then:
            // 1) reverse the string
            // 2) do wrapping
            // 3) reverse each vector element individually

            StringBuffer temp = new StringBuffer(inputString);

            Vector ret = doWrapString(temp.reverse().toString(),
                                      stringItemFont, fullWidth, freeWidth);

            for(int i = 0; i < ret.size(); i++)
            {
                temp.setLength(0);
                temp.append((String) ret.elementAt(i));
                ret.setElementAt(temp.reverse().toString(), i);
            }
            return ret;
        }
    }

    /**
     * Method to wrap a single string into many.
     *
     * @param inputString source string to wrap
     * @param font source string's font
     * @param fullWidth full form width in pixels
     * @param freeWidth free width available in current layout row, in pixels
     * @return Vector of Strings
     */
    private static Vector doWrapString(String inputString,
                                       Font font,
                                       int fullWidth,
                                       int freeWidth)
    {
        Vector result = new Vector();
        // Could happen if StringItem.setText(null) or StringItem.setText("")
        // was called
        if(inputString == null)
        {
            result.addElement("");
            return result;
        }

        if (true) {
            result.addAll(Arrays.asList(c.textArr(inputString, font,
                    Math.max(16, freeWidth - 20), Math.max(16, fullWidth - 8))));

            return result;
        }

        int availableWidth = freeWidth;
        int tokenWidth;
        int nextDelimiterPos = -1;
        int delimiterPos = 0;

        String str = inputString;
        String token = "";

        while(str.length() > 0)
        {
            nextDelimiterPos = findNextDelimiter(str, nextDelimiterPos + 1);
            token = str.substring(0, nextDelimiterPos).trim();
            tokenWidth = font.stringWidth(token);

            if(tokenWidth > availableWidth)
            {
                if(delimiterPos == 0)
                {
                    // even the first word in the string is too long to fit
                    if((availableWidth < fullWidth)
                            && (tokenWidth <= fullWidth))
                    {
                        // the long word would fit to the next full row
                        // result.addElement("");
                        availableWidth = fullWidth;
                        nextDelimiterPos = -1;
                        delimiterPos = 0;
                        continue;
                    }

                    if(tokenWidth > fullWidth)
                    {
                        // the long word does not fit to a full row
                        // do character-boundary wrapping
                        delimiterPos =
                            findWordBreak(token, font, availableWidth);
                        result.addElement(token.substring(0, delimiterPos));
                        str = str.substring(delimiterPos);
                        availableWidth = fullWidth;
                        nextDelimiterPos = -1;
                        delimiterPos = 0;
                        continue;
                    }
                }

                result.addElement(str.substring(0, delimiterPos).trim());
                availableWidth = fullWidth;
                str = str.substring(delimiterPos);
                nextDelimiterPos = -1;
                delimiterPos = 0;
                continue;
            }

            if(nextDelimiterPos == str.length())
            {
                result.addElement(token);
                availableWidth = fullWidth;
                str = str.substring(nextDelimiterPos);
                continue;
            }

            if(nextDelimiterPos < str.length()
                    && str.charAt(nextDelimiterPos) == '\n')
            {
                result.addElement(token);
                availableWidth = fullWidth;
                str = str.substring(nextDelimiterPos + 1);
                nextDelimiterPos = -1;
                delimiterPos = 0;
                continue;
            }

            delimiterPos = nextDelimiterPos;
        }
        return result;
    }

    /**
     * Break a word on character border.
     *
     * @param word - Word to break
     * @return Part of word that fits in the current available space
     */
    private static int findWordBreak(String word, Font font, int width)
    {
        int breakPos = word.length();
        while(font.substringWidth(word, 0, breakPos) >= width)
        {
            breakPos--;
        }
        return breakPos;
    }

    /**
     * The method searches for a delimiter in the String from beginning to end,
     * starting from specified position.
     *
     * @param str String to search a delimiter
     * @param startPos Start position for search
     * @return The first delimiter found
     */
    private static int findNextDelimiter(String str, int startPos)
    {
        int strLen = str.length();
        int pos = startPos;
        for(; pos < strLen; pos++)
        {
            int delimiter = DELIM_STR.indexOf(str.charAt(pos));
            if(delimiter >= 0)
            {
                if(delimiter < 8)
                {
                    // 8 is the '\n' character's index
                    return pos + 1;
                }
                break;
            }
        }
        return pos;
    }

}
