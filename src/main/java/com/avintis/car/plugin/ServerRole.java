package com.avintis.car.plugin;

public enum ServerRole
{
	
	ENTERPRISESERVICEBUS("EnterpriseServiceBus"),
	DATASERVICESERVER("DataServiceServer");

	private String role;
	
	private ServerRole(String role)
	{
		this.role = role;
	}
	
	public String getRole()
	{
		return role;
	}
}
