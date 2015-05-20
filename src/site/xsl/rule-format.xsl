<?xml version="1.0" encoding="UTF-8"?>
<!-- Taken from PMD and adjusted for AludraTest. -->
<!--
	BSD-style license; for more info see http://pmd.sourceforge.net/license.html
	
Copyright (c) 2002-2009, InfoEther, Inc
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
    * The end-user documentation included with the redistribution, if
any, must include the following acknowledgement:
      "This product includes software developed in part by support from
the Defense Advanced Research Project Agency (DARPA)"
    * Neither the name of InfoEther, LLC nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.	
 -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<!-- FUTURE: Externalising text to allow i18n generation  -->
	<xsl:variable name="Since"					select="'Since: AludraTest '"/>
	<xsl:variable name="definedByJavaClass"		select="'This rule is defined by the following Java class'"/>
	<xsl:variable name="ExampleLabel"			select="'Example(s)'"/>
	<xsl:variable name="PropertiesLabel"		select="'This rule has the following properties'"/>
	<xsl:variable name="Property.Name"			select="'Name'"/>
	<xsl:variable name="Property.DefaultValue"	select="'Default Value'"/>
	<xsl:variable name="Property.Desc"			select="'Description'"/>
	<xsl:variable name="RuleSet"				select="'Ruleset'"/>

	<xsl:template match="ruleset">
		<document>
			<xsl:variable name="rulesetname" select="@name"/>
			<properties>
				<author>Florian Albrecht, Hamburg Sued</author>
				<title><xsl:value-of select="$RuleSet"/>: <xsl:value-of select="$rulesetname"/></title>
			</properties>
			<body>
				<section>
					<xsl:attribute name="name">
						<xsl:value-of select="$rulesetname"/>
					</xsl:attribute>
					<xsl:apply-templates/>
				</section>
			</body>
		</document>
	</xsl:template>

	<xsl:template match="rule[@name]">
		<xsl:variable name="rulename" select="@name"/>
		<xsl:variable name="classname" select="@class"/>

		<subsection>
			<xsl:attribute name="name">
				<xsl:value-of select="$rulename"/>
			</xsl:attribute>
			<p><xsl:value-of select="$Since"/><xsl:value-of select="@since"/></p>
			<p><xsl:value-of select="description"/></p>
			<xsl:choose>
				<xsl:when test="count(properties/property[@name='xpath']) != 0">
					<source>
						<xsl:value-of select="properties/property/value"/>
					</source>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="classfile">
						<xsl:call-template name="url-maker">
							<xsl:with-param name="classname" select="$classname"/>
						</xsl:call-template>
					</xsl:variable>
					<p><xsl:value-of select="$definedByJavaClass"/>: <a><xsl:attribute name="href"><xsl:value-of select="concat(concat('./xref/',$classfile),'.html')"/></xsl:attribute><xsl:value-of select="@class"/></a>
	    		    </p>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:for-each select="./example">

				<xsl:value-of select="$ExampleLabel"/>:
	        	<source>
	        		<xsl:value-of select="."/>
	        	</source>
	      	</xsl:for-each>

	        <xsl:variable name="hasproperties" select="count(properties/property[@name!='xpath'])"/>
	        <xsl:choose>
	            <xsl:when test="$hasproperties != 0">
	                <p><xsl:value-of select="$PropertiesLabel"/>:</p>
	                <table><th><xsl:value-of select="$Property.Name"/></th><th><xsl:value-of select="$Property.DefaultValue"/></th><th><xsl:value-of select="$Property.Desc"/></th>
	                <xsl:for-each select="properties/property[@name != 'xpath']">
	                    <tr>
		                    <td><xsl:value-of select="@name"/></td>
		                    <td><xsl:value-of select="@value"/></td>
		                    <td><xsl:value-of select="@description"/></td>
	                    </tr>
	                </xsl:for-each>
	                </table>
	            </xsl:when>
	        </xsl:choose>

		</subsection>
	</xsl:template>

	<!-- Watch out, recursing function... -->
	<xsl:template name="url-maker">
        <xsl:param name="classname" select="."/>
        <!--
        <xsl:message>classname is:<xsl:value-of select="$classname"/></xsl:message>
        -->
        <xsl:choose>
        	<xsl:when test="contains($classname,'.')">
            	<xsl:variable name="pre" select="concat(substring-before($classname,'.'),'/')" />
                <xsl:variable name="post" select="substring-after($classname,'.')" />
                <xsl:call-template name="url-maker">
                	<xsl:with-param name="classname"		select="concat($pre,$post)"/>
                </xsl:call-template>
			</xsl:when>
            <xsl:otherwise>
            	<xsl:value-of select="$classname"/>
            </xsl:otherwise>
        </xsl:choose>
	</xsl:template>

</xsl:stylesheet>