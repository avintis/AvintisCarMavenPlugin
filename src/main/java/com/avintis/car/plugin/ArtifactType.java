package com.avintis.car.plugin;

public enum ArtifactType
{
	PROXY(				"synapse/proxy-service", 		ServerRole.ENTERPRISESERVICEBUS), 
	ENDPOINT(			"synapse/endpoint", 			ServerRole.ENTERPRISESERVICEBUS), 
	INBOUNDENDPOINT(	"synapse/inbound-endpoint",		ServerRole.ENTERPRISESERVICEBUS),
	LOCALENTRY(			"synapse/local-entry", 			ServerRole.ENTERPRISESERVICEBUS), 
	MESSAGEPROCESSOR(	"synapse/message-processors", 	ServerRole.ENTERPRISESERVICEBUS),
	MESSAGESTORE(		"synapse/message-store", 		ServerRole.ENTERPRISESERVICEBUS),
	API(				"synapse/api", 					ServerRole.ENTERPRISESERVICEBUS),
	SEQUENCE(			"synapse/sequence",				ServerRole.ENTERPRISESERVICEBUS),
	TASK(				"synapse/task", 				ServerRole.ENTERPRISESERVICEBUS),
	TEMPLATE(			"synapse/template", 			ServerRole.ENTERPRISESERVICEBUS),
	XSLSTYLESHEET(		"synapse/local-entry", 			ServerRole.ENTERPRISESERVICEBUS),
	SCHEMA(				"synapse/local-entry", 			ServerRole.ENTERPRISESERVICEBUS),
	LOOKUP(				"synapse/local-entry", 			ServerRole.ENTERPRISESERVICEBUS),
	DATA(				"service/dataservice", 			ServerRole.DATASERVICESERVER),
	MEDIATOR(			"lib/synapse/mediator", 		ServerRole.ENTERPRISESERVICEBUS);
	
	//OTD?
	
	private String artifactTypeName;
	private ServerRole serverRole;

	private ArtifactType(String artifactTypeName, ServerRole serverRole) {
		this.artifactTypeName = artifactTypeName;
		this.serverRole = serverRole;
	}

	public String getArtifactTypeName() {
		return artifactTypeName;
	}
	
	public ServerRole getServerRole()
	{
		return serverRole;
	}
}
