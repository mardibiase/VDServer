package fileHandlers;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.FileResourceMapping;


public class QueryXMLUsers {
	
	public String login(String eMail, String pw) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException,
			TransformerException {
		// standard for reading an XML file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		XPathExpression expr = null;
		builder = factory.newDocumentBuilder();
		doc = builder.parse(FileResourceMapping.users_filePath);

		// create an XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// create an XPath object
		XPath xpath = xFactory.newXPath();

		// compile the XPath expression
		// this expression looks for an already registered eMail
		expr = xpath.compile("count(//user[mail='" + eMail + "']) > 0");
		// run the query and get a result
		// as the query already contains the check to be done we map it to a boolean condition
		Boolean check = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);
		System.out.println(check);
		if (!check) {
			// register the user
			Element user = doc.createElement("user");
			doc.getDocumentElement().appendChild(user);

			Element mail = doc.createElement("mail");
			mail.appendChild(doc.createTextNode(eMail));
			user.appendChild(mail);

			Element password = doc.createElement("pw");
			password.appendChild(doc.createTextNode(pw));
			user.appendChild(password);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FileResourceMapping.users_filePath));
			transformer.transform(source, result);
			System.out.println("user " + eMail + " registered");
			return "REG";
		} else {
			// the user exists, let's check his password
			// compile the XPath expression
			expr = xpath.compile("//user[mail='" + eMail + "']/pw/text()");
			// run the query and get a nodeset
			Object result = expr.evaluate(doc, XPathConstants.NODESET);

			// cast the result to a DOM Node passing through a NodeList
			Node user = ((NodeList) result).item(0);
			// check his password
			if (pw.equalsIgnoreCase(user.getNodeValue())) {
				System.out.println("THE PASSWORD IS CORRECT!");
				return "OK";
			} else {
				System.out.println("THE PASSWORD IS WRONG!");
				return "NO";
			}

		}

	}

	public String delete(String email) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
		String toRet = "";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;
		XPathExpression expr = null;
		builder = factory.newDocumentBuilder();
		doc = builder.parse(FileResourceMapping.users_filePath);
		// create an XPathFactory
		XPathFactory xFactory = XPathFactory.newInstance();

		// create an XPath object
		XPath xpath = xFactory.newXPath();

		// compile the XPath expression
		// this expression looks for an already registered eMail
		expr = xpath.compile("count(//user[mail='" + email + "']) > 0");
		// run the query and get a result
		// as the query already contains the check to be done we map it to a boolean condition
		Boolean check = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);
		System.out.println(check);
		
		if (check) { //the user exists

			//questo codice funziona ma lascia i tag <user><mail><pw> vuoti
			expr = xpath.compile("//user[mail='" + email + "']/pw/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			Node user = ((NodeList) result).item(0);
			user.getParentNode().removeChild(user);
			expr = xpath.compile("//user[mail='" + email + "']/mail/text()");
			result = expr.evaluate(doc, XPathConstants.NODESET);
			user = ((NodeList) result).item(0);
			user.getParentNode().removeChild(user);
			
			//questa espressione cancella tutti i tag <user><mail><pw> vuoti
			expr = xpath.compile("//user[mail!=normalize-space(.) = '']");  
			NodeList emptyTextNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			      Node emptyTextNode = emptyTextNodes.item(i);
			      emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
			
			TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer t = tf.newTransformer();
	        t.transform(new DOMSource(doc), new StreamResult(FileResourceMapping.users_filePath));
			
			toRet = "DELETED";
		}

		else toRet = "NOT FOUND";
		return toRet;

	}
}