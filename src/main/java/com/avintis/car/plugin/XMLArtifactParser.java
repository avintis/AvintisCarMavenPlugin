package com.avintis.car.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;

public class XMLArtifactParser
{
	/**
	 * 
	 * @param 	file	the file to parse. File needs to be a artifact config or a jar file.
	 * @param 	version	the version needs to be defined explicitly
	 * @return			parsed file as Artifact. Content is read to memory
	 * @throws Exception
	 */
	public Artifact parse(File file, String version) throws Exception
	{
		try
		{
			//file is previsously listed using the parent folder. If the file does not exists or is not readable, the build process needs to be stopped to prevent a faulty deployment 
			if(!(file.exists() || file.canRead()))
			{
				throw new FileNotFoundException("File: " + file.getPath() + " does not exist or is not readble!");
			}
			
			//check extension for different preparation
			String extension = FilenameUtils.getExtension(file.getName());
			boolean isLookup = extension != null && extension.equalsIgnoreCase("lookup");
			boolean isMediator = extension != null && extension.equalsIgnoreCase("jar");
			
			//Members needed to create an Artifact-Object
			//String name = file.getName().lastIndexOf(".") != -1 ? file.getName().substring(0, file.getName().lastIndexOf(".")) : file.getName(); //--> replaced by Fileutils.getBaseName();
			String name = FilenameUtils.getBaseName(file.getName());
			String fileName = file.getName();
			ArtifactType type;
			byte[] fileContent;
			
			if(isMediator)
			{
				//maven compiler adds version to name. At this point, we need the name without the version
				name = name.endsWith(version) ? name.substring(0, name.length() - (version.length()+ 1)) : name;
				type = ArtifactType.MEDIATOR;
				
				//read the whole file into memory as member to Artifact
				fileContent = new byte[(int)file.length()];
				FileInputStream fis = new FileInputStream(file);
				fis.read(fileContent);
				fis.close();
			}
			else
			{
				//lookup needs to be put into an xml file, specifically a localEntry. 
				if(isLookup)
				{
					//read content and save to tmp var
					FileInputStream fis = new FileInputStream(file);
					byte[] tmp = new byte[(int) file.length()];
					fis.read(tmp);
					fis.close();
					
					//build the xml using a String builder
					StringBuilder sb = new StringBuilder();
					sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					sb.append(System.lineSeparator());
					
					String fileBaseName = FilenameUtils.getBaseName(file.getName());
					String localEntry = "<localEntry xmlns=\"http://ws.apache.org/ns/synapse\" key=\"" + fileBaseName + "\">";
					
					sb.append(localEntry);
					sb.append(System.lineSeparator());
					sb.append(new String(tmp));
					sb.append(System.lineSeparator());
					sb.append("</localEntry>");
		
					fileContent = sb.toString().getBytes();
					
					type = ArtifactType.LOCALENTRY;
				}
				
				//all other than lookup or mediators are parsed the same way
				else
				{
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(file);
					
					String rootElementName = doc.getDocumentElement().getTagName();
					
					// enum for known types
					type = getArtifactType(rootElementName);
					if(type == null)
					{
						throw new Exception("Archtype unknown: \"" + rootElementName + "\"");
					}
					
					//wrap xlst in local-entry
					if(rootElementName.equalsIgnoreCase("xsl:stylesheet") || rootElementName.equals("schema"))
					{
						
						doc = ArtifactXMLUtil.wrapDocument(db, doc, "localEntry", "http://ws.apache.org/ns/synapse", name); //overwrite
					}
					fileContent = ArtifactXMLUtil.transform(doc);
				}
			}
			
			Artifact artifact = new Artifact(name, version, type, type.getServerRole(), fileName, fileContent);
			
			return artifact;
		}
		catch(FileNotFoundException e)
		{
			throw new Exception("File not found! Further execution is prohibited!", e);
		}
	}
	
	/**
	 * 
	 * @param 	rootElementName		the name of the rootElements indicates the type. The maven source plugin only copies defined file (extensions)
	 * @return						null when unknown
	 * @see							pom.xml
	 */
	private ArtifactType getArtifactType(String rootElementName)
	{
		//remove ':' from name (xslt)
		String normalizedName = rootElementName.contains(":") ? String.join("", rootElementName.split(":")) : rootElementName;
		
		ArtifactType[] types = ArtifactType.values();
		for(int i = 0; i < types.length; i++)
		{
			if(types[i].toString().equalsIgnoreCase(normalizedName))
			{
				return types[i];
			}
		}
		return null; //type unknown
	}
}