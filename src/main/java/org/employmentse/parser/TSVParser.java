package org.employmentse.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.config.ServiceLoader;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TSVParser extends AbstractParser 
{
	public TSVParser(String[] headLine)
	{
		tableHeaders=headLine;
	}
	
	public Set<MediaType> getSupportedTypes(ParseContext context) 
    {
        return SUPPORTED_TYPES;
    }
	
	private XHTMLContentHandler xhtml;
	
	private static String[] tableHeaders; 
	
	private static final long serialVersionUID = 1L;
	
	private static final ServiceLoader LOADER = new ServiceLoader(TSVParser.class.getClassLoader());

    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.text("tsv"));  
	
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException 
    {
        AutoDetectReader reader = new AutoDetectReader(new CloseShieldInputStream(stream), metadata,context.get(ServiceLoader.class, LOADER));         
        try 
        {	        	            
            Charset charset = reader.getCharset();
            MediaType type = new MediaType(MediaType.text("tsv"), charset);
            metadata.set(Metadata.CONTENT_TYPE, type.toString());
            metadata.set(Metadata.CONTENT_ENCODING, charset.name());
            
            String tablineTag = "[#tab#]";
            String newlineTag = "[#new#]";
            
            xhtml = new XHTMLContentHandler(handler, metadata);
            xhtml.startDocument();
            xhtml.startElement("table");            
            xhtml.startElement("tr");
            
            for (String element: tableHeaders)
            {
            	xhtml.startElement("th");
            	xhtml.characters(element.trim());
            	xhtml.endElement("th");
            }
            xhtml.endElement("tr");
            
            int bufferSize = 4096;
            char[] bufferCharacters = new char[bufferSize];
            int n = reader.read(bufferCharacters);
            
            int bufferBlock=1;                               
            while (n!=-1) 
            {
            	String bufferString = new String(bufferCharacters)
            	.replaceAll("[\\t]",	"|"+tablineTag+"|")
            	.replaceAll("[\\r\\n]", "|"+newlineTag+"|")
            	.replaceAll("Ã©","é").replaceAll("√©","é")
            	.replaceAll("Ã³","ó").replaceAll("√≥","ó")
            	.replaceAll("Ã¡","á").replaceAll("√°","á")
            	.replaceAll("Ãº","ú").replaceAll("√∫","ú")
            	.replaceAll("Ã­" ,"í").replaceAll("√≠","í")
            	.replaceAll("Ã€","à").replaceAll("√§","à")
            	.replaceAll("ÂŽ","'").replaceAll("Ã¤","à")
            	.replaceAll("Â´","'").replaceAll("¬¥","'")
            	.replaceAll("Â°","°").replaceAll("¬∞","°")
            	.replaceAll("Ã±","ñ").replaceAll("√±","ñ");

            	if 	(n<bufferSize) //last bufferBlock
            	{	            	
            		while (bufferString.trim().endsWith("|") || bufferString.trim().endsWith(tablineTag) || bufferString.trim().endsWith(newlineTag))
            		{
            			if (bufferString.trim().endsWith("|"))        {bufferString=bufferString.substring(0,bufferString.trim().length()-1);}
            			if (bufferString.trim().endsWith(tablineTag)) {bufferString=bufferString.substring(0,bufferString.trim().lastIndexOf(tablineTag));}
            			if (bufferString.trim().endsWith(newlineTag)) {bufferString=bufferString.substring(0,bufferString.trim().lastIndexOf(newlineTag));}
            		}
            	}         			            	
            	             	
            	if (bufferBlock==1) //first bufferBlock 
            	{xhtml.startElement("tr"); xhtml.startElement("td");}
            	String[] bufferElements = bufferString.split("[|]+");
            	
            	for (int i=0; i<bufferElements.length; i++)          
            	{            		            	
            		String currElement = bufferElements[i];
            		String nextElement = (i==bufferElements.length-1) ?"" :bufferElements[i+1];            		
            		
            		if (currElement.equals(tablineTag)){xhtml.startElement("td");}           			            		
            		else if (currElement.equals(newlineTag)){xhtml.startElement("tr"); xhtml.startElement("td");}            		
            		else{xhtml.characters(currElement.replaceAll("\\p{C}",""));}            			
            		            		            	        			
            		if (nextElement.equals(tablineTag)){xhtml.endElement("td");}
            		else if (nextElement.equals(newlineTag)){xhtml.endElement("td"); xhtml.endElement("tr");}            		
            	}
            	bufferBlock++;            	
            	bufferCharacters = new char[bufferSize];        		
                n = reader.read(bufferCharacters);
            }            
            xhtml.endElement("td");
            xhtml.endElement("tr");
            xhtml.endElement("table");
            xhtml.endDocument();            
        }        
        catch (Exception e){e.printStackTrace();} 
        finally {reader.close();}        
    }
}