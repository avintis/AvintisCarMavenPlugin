package com.avintis.car.plugin;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author hauensteina
 * 
 * ArtifactXMLUtil creates two types of XML documents returned as a ByteArray
 * artifact.xml and artifacts.xml (note: artifact singular and plural!)
 *
 */

public class ArtifactXMLUtil
{
	/**
	 * @param	artifact	the artifact from which the artifact.xml(singular) should be built
	 * @return 				the artifact.xml as byte Array
	 * @throws 				ParserConfigurationException
	 * @see 				http://synapse.apache.org/userguide/template_library.html
	 */
	public static byte[] createArtifactXML(Artifact artifact) throws ParserConfigurationException
	{
		//new empty document
		Document artifactXML = createDocument();
		
		Element rootElement = artifactXML.createElement("artifact");
		rootElement.setAttribute("name", artifact.getName());
		rootElement.setAttribute("version", artifact.getVersion());
		rootElement.setAttribute("type", artifact.getType().getArtifactTypeName());
		rootElement.setAttribute("serverRole", artifact.getType().getServerRole().getRole());
		
		artifactXML.appendChild(rootElement);
		
		Element file = artifactXML.createElement("file");
		file.setTextContent(artifact.getFinalFileName());
		rootElement.appendChild(file);
		
		//transformed for a friendly and human readable output
		return transform(artifactXML);
	}
	
	/**
	 * 
	 * @param	artifacts		ist from which the artifacts.xml(plural) should be built	
	 * @param 	projectName		name of the project
	 * @param 	projectVersion	version of the project
	 * @param 	projectType		ProjectType as String
	 * @return					the artifact.xml as byte Array
	 * @throws 					ParserConfigurationException
	 * @see 					http://synapse.apache.org/userguide/template_library.html
	 */
	public static byte[] createArtifactsXML(List<Artifact> artifacts, String projectName, String projectVersion, String projectType) throws ParserConfigurationException
	{
		//new empty document
		Document artifactsXML = createDocument();
		
		Element rootElement = artifactsXML.createElement("artifacts");
		artifactsXML.appendChild(rootElement);
		
		Element artifact = artifactsXML.createElement("artifact");
		artifact.setAttribute("name", projectName);
		artifact.setAttribute("version", projectVersion);
		artifact.setAttribute("type", projectType);
		
		rootElement.appendChild(artifact);
		
		for(Artifact a: artifacts)
		{
			Element e = artifactsXML.createElement("dependency");
			e.setAttribute("artifact", a.getName());
			e.setAttribute("version", a.getVersion());
			e.setAttribute("include", "true");
			e.setAttribute("serverRole", a.getServerRole().getRole());
			artifact.appendChild(e);
		}
		
		//transformed for a friendly and human readable output
		return transform(artifactsXML);
	}
	
	/**
	 * 
	 * @param 	doc		document to transform
	 * @return			document as byteArray
	 */
	public static byte[] transform(Document doc)
	{
		Transformer transformer;
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (Exception e)
		{
			// log error here
			return null;
		}
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
		DOMSource source = new DOMSource(doc);
		
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		 StreamResult result=new StreamResult(bos);
		 try
		{
			transformer.transform(source, result);
		} catch (TransformerException e)
		{
			//log error here
			return null;
		}
		 return bos.toByteArray();
	}
	
	/**
	 * Some filetypes, e.g. otd and lookups need to be wrapped into a localEntry. Otherwise they cannot be deployed as an artifact
	 * 
	 * @param db			DocumentBuilder from which the document is created
	 * @param baseDoc		the document to wrap
	 * @param element		the element used to wrap
	 * @param nameSpace		the namespace in which the element should be created in
	 * @param name			the value of the attribute "key"
	 * @return				document as Document
	 */
	public static Document wrapDocument(DocumentBuilder db, Document baseDoc, String element, String nameSpace, String name)
	{	
		Document wrappedDoc = db.newDocument();
		Element wrapper;
		if(nameSpace != null && !nameSpace.equals(""))
		{
			wrapper = wrappedDoc.createElementNS(nameSpace, element);
		}
		else
		{
			wrapper = wrappedDoc.createElement(element);
		}
		wrapper.setAttribute("key", name);
		wrappedDoc.appendChild(wrapper);
		
		Node importedNode = wrappedDoc.importNode(baseDoc.getDocumentElement(), true);
		wrapper.appendChild(importedNode);
		
		return wrappedDoc;
	}
	
	/**
	 * 
	 * @return	new empty document
	 * @throws 	ParserConfigurationException
	 */
	private static Document createDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.newDocument();
	}
}
