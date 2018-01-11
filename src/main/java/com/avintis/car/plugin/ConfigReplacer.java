package com.avintis.car.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Properties;

public class ConfigReplacer
{
	/**
	 * 
	 * @param 	fSource		file from which is read
	 * @param 	props		filtermap
	 * @param 	fDest		filtered file
	 * @throws IOException
	 */
	public static void replace(File fSource, Properties props, File fDest) throws IOException
	{
		
		boolean sameFile = false;
		if (fSource == fDest || fDest == null)
		{
			fSource.renameTo(new File(fSource.getAbsolutePath() + ".orig"));
			fDest = new File(fSource.getAbsolutePath());
			fSource = new File(fDest.getAbsolutePath() + ".orig");
			sameFile = true;
		}
		
		ArrayList<String> keys = new ArrayList<String>();
		for (Object o : props.keySet())
		{
			keys.add((String) o);
		}

		//streams need to be explicitly read & written with specific encoding
		InputStreamReader isr = new InputStreamReader(new FileInputStream(fSource), "UTF-8");
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fDest), "UTF-8");

		//no streaming (yet?)
		char[] readBuffer = new char[(int) fSource.length()];
		isr.read(readBuffer);
		String file = new String(readBuffer);
		for (String key : keys)
		{
			file = file.replace(key, props.getProperty(key));
		}

		//write final string
		osw.write(file);
		osw.flush();
		
		//close
		osw.close();
		isr.close();

		//the source file needs to be deleted in the end
		if (sameFile)
		{
			fSource.delete();
		}
	}

	/**
	 * 
	 * @param 	props	Property-File submitted by maven param
	 * @return			true or false if one key is a genuine subset of another key
	 */
	public static boolean checkKeyNested(Properties props)
	{
		ArrayList<String> keys = new ArrayList<String>();
		for (Object o : props.keySet())
		{
			keys.add((String) o);
		}

		//cross-checked because A could be part of B as well as B could be part of A
		for (int i = 0; i < keys.size(); i++)
		{
			String k1 = keys.get(i);
			String k2 = keys.get((keys.size() - 1) - i);
			if (k1.equals(k2))
			{
				continue;
			}
			if (k1.contains(k2))
			{
				return true;
			}
		}
		return false;
	}
}
