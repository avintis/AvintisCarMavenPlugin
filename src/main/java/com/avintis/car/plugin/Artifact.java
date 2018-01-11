package com.avintis.car.plugin;

public class Artifact
{
	private String name;
	private String version;
	private ArtifactType type;
	private ServerRole serverRole;
	private String fileName;
	private byte[] fileContent;
	
	public Artifact(String name, String version, ArtifactType type, ServerRole serverRole, String fileName, byte[] fileContent)
	{
		this.name = name;
		this.version = version;
		this.type = type;
		this.serverRole = serverRole;
		this.fileName = fileName;
		this.fileContent = fileContent;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public ArtifactType getType()
	{
		return type;
	}

	public void setType(ArtifactType type)
	{
		this.type = type;
	}

	public ServerRole getServerRole()
	{
		return serverRole;
	}

	public void setRole(ServerRole serverRole)
	{
		this.serverRole = serverRole;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public byte[] getFileContent()
	{
		return fileContent;
	}

	public void setFileContent(byte[] fileContent)
	{
		this.fileContent = fileContent;
	}
	
	public String getFinalFileName()
	{
		if(type == ArtifactType.MEDIATOR) //mediator filename is already correct due maven compiling
		{
			return fileName;
		}
		else
		{
			return (name + "-" + version + ".xml");
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + name);
		sb.append(System.getProperty("line.separator"));
		sb.append("Version: " + version);
		sb.append(System.getProperty("line.separator"));
		sb.append("Type: " + type);
		sb.append(System.getProperty("line.separator"));
		sb.append("ServerRole: " + serverRole);
		sb.append(System.getProperty("line.separator"));
		sb.append("FileName: " + fileName);
		sb.append(System.getProperty("line.separator"));
		sb.append("FinalFileName: " + getFinalFileName());
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}
}
