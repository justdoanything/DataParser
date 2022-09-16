package prj.yong.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CommonUtil {
	
	/**
	 * Copy to Clipboard 
	 * @param str
	 */
	public void copyClipboard(String str) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection contents = new StringSelection(str);
	    clipboard.setContents(contents, contents);
	}
}
