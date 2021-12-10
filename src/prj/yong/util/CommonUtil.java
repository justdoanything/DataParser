package prj.yong.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class CommonUtil {
	
	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	
	/**
	 * 
	 * @param str
	 */
	public void copyClipboard(String str) {
		StringSelection contents = new StringSelection(str);
	    clipboard.setContents(contents, contents);
	}
}
