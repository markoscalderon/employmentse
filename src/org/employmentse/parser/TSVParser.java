package org.employmentse.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

public class TSVParser extends AbstractParser {

	private static final long serialVersionUID = 1L;

	private static final Set<MediaType> SUPPORTED_TYPES =
	        Collections.singleton(MediaType.TEXT_PLAIN);

    private static final ServiceLoader LOADER =
            new ServiceLoader(TSVParser.class.getClassLoader());

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler,
            Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
		
		// Automatically detect the character encoding
        AutoDetectReader reader = new AutoDetectReader(
                new CloseShieldInputStream(stream), metadata,
                context.get(ServiceLoader.class, LOADER));
        try {
            Charset charset = reader.getCharset();
            MediaType type = new MediaType(MediaType.TEXT_PLAIN, charset);
            metadata.set(Metadata.CONTENT_TYPE, type.toString());
            // deprecated, see TIKA-431
            metadata.set(Metadata.CONTENT_ENCODING, charset.name());

            XHTMLContentHandler xhtml =
                    new XHTMLContentHandler(handler, metadata);

            xhtml.startDocument();

            xhtml.startElement("table");
            
            xhtml.startElement("tr");
            
            xhtml.startElement("th");
            xhtml.characters("column1");
            xhtml.endElement("th");

            xhtml.startElement("th");
            xhtml.characters("column2");
            xhtml.endElement("th");
            
            xhtml.endElement("tr");
            
            xhtml.startElement("tr");

            xhtml.startElement("td");
            xhtml.characters("row1col1");
            xhtml.endElement("td");
            
            xhtml.startElement("td");
            xhtml.characters("row1col2");
            xhtml.endElement("td");
            
            xhtml.endElement("tr");
            
            
            xhtml.startElement("tr");

            xhtml.startElement("td");
            xhtml.characters("row2col1");
            xhtml.endElement("td");
            
            xhtml.startElement("td");
            xhtml.characters("row2col2");
            xhtml.endElement("td");
            
            xhtml.endElement("tr");
            
            xhtml.endElement("table");

            xhtml.endDocument();
            
            System.out.println(xhtml.toString());
        } finally {
            reader.close();
        }
	}

}
