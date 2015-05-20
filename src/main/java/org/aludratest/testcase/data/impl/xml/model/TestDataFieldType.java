package org.aludratest.testcase.data.impl.xml.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TestDataFieldType {

    @XmlEnumValue("STRING")
    STRING, 
    
    @XmlEnumValue("DATE")
    DATE, 
    
    @XmlEnumValue("NUMBER")
    NUMBER, 
    
    @XmlEnumValue("BOOLEAN")
    BOOLEAN, 
    
    @XmlEnumValue("OBJECT")
    OBJECT, 
    
    @XmlEnumValue("OBJECT_LIST")
    OBJECT_LIST, 
    
    @XmlEnumValue("STRING_LIST")
    STRING_LIST;

}
