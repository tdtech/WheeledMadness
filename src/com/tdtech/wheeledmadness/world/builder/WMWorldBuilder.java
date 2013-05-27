package com.tdtech.wheeledmadness.world.builder;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.tdtech.wheeledmadness.log.WMLog;
import com.tdtech.wheeledmadness.world.WMWorld;

public class WMWorldBuilder extends DefaultHandler {
    
    private WMWorld mWorld;
    
    private SAXParser mXMLParser;
    private int mDeep;
    
    private WMWorldElementFactory mElementFactory;
    private Stack<IWMWorldElement> mElementStack;
    
    private boolean mBuilt;
    
    public WMWorldBuilder(WMWorld world) {
        try {
            mXMLParser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException e) {
            WMLog.e("Error while creating SAX parser: " + e.toString());
        } catch (SAXException e) {
            WMLog.e("Error while creating SAX parser: " + e.toString());
        }
        
        mWorld = world;
        mElementFactory = new WMWorldElementFactory();
        mElementStack = new Stack<IWMWorldElement>();
        
        // avoid EmptyStackException
        mElementStack.push(mElementFactory.getStubElement());
    }
    
    public boolean buildFromXML(String xmlPath) {
        if (mXMLParser == null) {
            // something really wrong here
            return false;
        }
        
        mBuilt = true;
        
        try {
            mXMLParser.parse(new File(xmlPath), this);
        } catch (SAXException e) {
            WMLog.e("Error while parsing XML file: " + e.toString());
            mBuilt = false;
        } catch (IOException e) {
            WMLog.e("Error while parsing XML file: " + e.toString());
            mBuilt = false;
        }
        
        return mBuilt;
    }
    
    @Override
    public void startDocument() {
        mWorld.clear();
        mDeep = 0;
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        IWMWorldElement element = mElementFactory.getElementByName(localName, mDeep);
        
        element.parse(attributes, mElementStack.peek());
        
        mElementStack.push(element);
        mDeep++;
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) {
        IWMWorldElement element = mElementStack.pop();
        
        // pass element to its parent
        mElementStack.peek().parseInnerElement(element);
        element.postToWorld(mWorld);
        
        mDeep--;
    }
    
    @Override
    public void error(SAXParseException e) {
        WMLog.w("Recoverable error while parsing world: " + e.toString());
    }
    
    @Override
    public void fatalError(SAXParseException e) {
        WMLog.e("Error while parsing world: " + e.toString());
        mWorld.clear();
        mBuilt = false;
    }
    
    @Override
    public void warning(SAXParseException e) {
        WMLog.w("Warning notification while parsing world: " + e.toString());
    }
}