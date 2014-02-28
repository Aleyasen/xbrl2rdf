package edu.toronto.cs.xbrl2rdf.mapping;

import edu.toronto.cs.xcurator.discoverer.BasicEntitiesDiscovery;
import edu.toronto.cs.xcurator.discoverer.DataDocument;
import edu.toronto.cs.xcurator.discoverer.MappingDiscoverer;
import edu.toronto.cs.xcurator.discoverer.SerializeMapping;
import edu.toronto.cs.xcurator.mapping.Mapping;
import edu.toronto.cs.xcurator.mapping.XmlBasedMapping;
import edu.toronto.cs.xcurator.xml.UriBuilder;
import edu.toronto.cs.xcurator.xml.XmlDocumentBuilder;
import edu.toronto.cs.xcurator.xml.XmlParser;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import edu.toronto.cs.xbrl2rdf.config.RunConfig;
import org.w3c.dom.Document;

public class MappingFactory {
    
    private final RunConfig config;
    
    public MappingFactory(RunConfig config) {
        this.config = config;
    }
    
    public Mapping createInstance(Document xbrlDocument) {
        List<Document> docList = new ArrayList<>();
        docList.add(xbrlDocument);
        return createInstance(docList);
    }
    
    public Mapping createInstance(List<Document> xbrlDocuments) {
        Mapping mapping = buildXmlBasedMapping();
        MappingDiscoverer discoverer = buildBasicDiscoverer(xbrlDocuments, mapping);
        discoverer.discoverMapping();
        return mapping;
    }
    
    public Mapping createInstance(List<Document> xbrlDocuments, String fileName) 
            throws TransformerConfigurationException, FileNotFoundException {
        Mapping mapping = buildXmlBasedMapping();
        MappingDiscoverer discoverer = buildBasicDiscoverer(xbrlDocuments, mapping);
        
        String saveFilePath = config.getMappingSerializedFilePath(fileName);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        discoverer.addStep(new SerializeMapping(new XmlDocumentBuilder(), 
                new FileOutputStream(saveFilePath), 
                transformer));
        
        discoverer.discoverMapping();
        return mapping;
    }
    
    private MappingDiscoverer buildBasicDiscoverer(List<Document> xbrlDocuments, 
            Mapping mapping) {
        MappingDiscoverer discoverer = new MappingDiscoverer(mapping);
        
        String resourceUriPattern = config.getEntityResourceUriBase() + "${UUID}";
        for (Document document : xbrlDocuments) {
            discoverer.addDataDocument(new DataDocument(document, resourceUriPattern));
        }
        
        discoverer.addStep(new BasicEntitiesDiscovery(
                new XmlParser(), 
                new UriBuilder(config.getDefaultEntityResourceTypeUri(), 
                    config.getDefaultEntityResourceTypePrefix()), true));
        discoverer.addStep(new XbrlRelationDiscovery());
        
        return discoverer;
        
    }
    
    private Mapping buildXmlBasedMapping() {
        return new XmlBasedMapping();
    }
}
