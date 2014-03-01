package edu.toronto.cs.xbrl2rdf.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class RunConfig {

    private Configuration config;

    // From setting file
    final String resourceUriBase;
    final String typeResourceUriBase;
    final String typeResourcePrefix;
    final String propertyResourceUriBase;
    final String propertyResourcePrefix;

    // From run time setting
    String domain;
    String tdbDirectory;

    public RunConfig(String domain) {
        try {
            config = new PropertiesConfiguration("setting.properties");
        } catch (ConfigurationException ex) {
            Logger.getLogger(RunConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        // We have to check if this domain is a valid uri before initializing
        // the config
        this.domain = domain.endsWith("/") ? 
                domain.substring(0, domain.length()-1) :
                domain;
        
        resourceUriBase = this.domain + config.getString("rdf.uribase", "/resource/");

        String typeUriBase = config.getString("rdf.type.uribase",
                "/resource/class/");
        typeResourceUriBase = this.domain + (typeUriBase.endsWith("/")
                ? typeUriBase : typeUriBase + "/");
        
        typeResourcePrefix = config.getString("rdf.type.prefix",
                "class");

        String propertyUriBase = config.getString("rdf.property.uribase",
                "/resource/property/");
        propertyResourceUriBase = this.domain + (propertyUriBase.endsWith("/")
                ? propertyUriBase : propertyUriBase + "/");
        
        propertyResourcePrefix = config.getString("rdf.property.prefix",
                "property");
    }

    public String getResourceUriBase() {
        return resourceUriBase;
    }
    
    public String getTypeResourceUriBase() {
        return typeResourceUriBase;
    }
    
    public String getPropertyResourceUriBase() {
        return propertyResourceUriBase;
    }

    public String getTypeResourcePrefix() {
        return typeResourcePrefix;
    }
    
    public String getPropertyResourcePrefix() {
        return propertyResourcePrefix;
    }

    public String getTdbDirectory() {
        return tdbDirectory;
    }

    public void setTdbDirectory(String tdbDirectory) {
        this.tdbDirectory = tdbDirectory;
    }

}