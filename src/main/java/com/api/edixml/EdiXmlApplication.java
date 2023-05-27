package com.api.edixml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.io.StreamUtils;
import org.milyn.smooks.edi.unedifact.UNEdifactReaderConfigurator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SpringBootApplication
public class EdiXmlApplication {
	
	 protected static String runSmooksTransform() throws IOException, SAXException, SmooksException {

	        // Configure Smooks using a Smooks config...
	        //Smooks smooks = new Smooks("smooks-config.xml");
	        
	        // Or, configure Smooks programmatically...
	        Smooks smooks = new Smooks();
	        smooks.setReaderConfig(new UNEdifactReaderConfigurator("urn:org.milyn.edi.unedifact:d96a-mapping:1.7.1.0"));

	        try {
	            StringWriter writer = new StringWriter();

	            smooks.filterSource(new StreamSource(new FileInputStream("PAXLST.edi")), new StreamResult(writer));
	            System.out.println("-.-.-.-.-.-..-.-.-.-.-.-.-..-.-.-.-.-.-.-..-.\n" + writer.toString());
	            return writer.toString();
	        } finally {
	            smooks.close();
	        }
	    }

	public static void main(String[] args) throws IOException, SmooksException, SAXException, Exception {
		//SpringApplication.run(EdiXmlApplication.class, args);
		
		System.out.println("\n\n==============Message In==============");
        System.out.println(readInputMessage());
        System.out.println("======================================\n");

        String messageOut = EdiXmlApplication.runSmooksTransform();
        System.out.println("())))))))))))))))))))))))))" + messageOut);

        System.out.println("==============Message Out=============");
        System.out.println(messageOut);
        System.out.println("======================================\n\n");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(messageOut)));

        // Write the parsed document to an xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result =  new StreamResult(new File("my-file.xml"));
        transformer.transform(source, result);
	}
	
	private static String readInputMessage() throws IOException {
        return StreamUtils.readStreamAsString(new FileInputStream("PAXLST.edi"));
    }
}
