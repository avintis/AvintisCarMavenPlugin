package com.avintis.car.plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.project.MavenProject;

public class CARBuilder
{
	private final String type = "carbon/application";
	
	private MavenProject project;
	private List<Artifact> artifacts;
	
	public CARBuilder(MavenProject project, List<Artifact> artifacts)
	{
		this.project = project;
		this.artifacts = artifacts;
	}
	
	/**
	 * builds car from instantiated CARBuilder 
	 * @throws Exception 
	 */
	public void build() throws Exception
	{
		//artifactS.xml (plural)
		byte[] artifactsList = ArtifactXMLUtil.createArtifactsXML(artifacts, project.getName(), project.getVersion(), type);
		
		//destination car file
		File carFile = new File(project.getBuild().getDirectory(), project.getBuild().getFinalName() + ".car");
		
		//"mvn clean" deletes the whole target folder
		if(carFile.exists())
		{
			carFile.delete();
		}
		else //existing car file indicates that the folder must exist
		{
			File targetFolder = new File(project.getBuild().getDirectory());
			if(!targetFolder.exists())
			{
				targetFolder.mkdirs();
			}
		}
		
		try
		{
			//car file is an archive like a jar or a zip. Zip-Stream is the built in java way to create an archive
			FileOutputStream fos = new FileOutputStream(carFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos);
			
			for(Artifact  artifact: artifacts)
			{
				//artifact folder needs to be created first. Name needs to end with a "/"
				String artifactFolder = artifact.getName() + "_" + artifact.getVersion() + "/";
				zos.putNextEntry(new ZipEntry(artifactFolder));
				
				//artifact.xml
				String artifactXML = artifactFolder + "artifact.xml";
				zos.putNextEntry(new ZipEntry(artifactXML));
				byte[] artifactXMLContent = ArtifactXMLUtil.createArtifactXML(artifact);
				zos.write(artifactXMLContent);
				//close the current entry and move to the next
				zos.closeEntry();
				
				//"the" artifact content (e.g. proxy.xml etc.)
				String artifactComponentXML = artifactFolder + artifact.getFinalFileName();
				byte[] artifactComponentXMLContent = artifact.getFileContent();
				
				zos.putNextEntry(new ZipEntry(artifactComponentXML));
				zos.write(artifactComponentXMLContent);
				//close the current entry and move to the next
				zos.closeEntry();
			}
			
			//put the artifacts.xml into the car
			zos.putNextEntry(new ZipEntry("artifacts.xml"));
			zos.write(artifactsList);
			zos.closeEntry();
			
			//close all streams
			zos.close();
			bos.close();
			fos.close();
			
		} 
		catch(Exception e)
		{
			throw new Exception("Exception thrown during CAR Creation", e);
		}
	}
}
