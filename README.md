[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) ![version](https://img.shields.io/maven-central/v/org.jresearch.commons.gwt.mdIcon/org.jresearch.commons.gwt.mdIcon.processor.svg)

# GWT MD Icons for Domino UI #

This is a Java annotation processor to generate Domino UI MdiIcons (https://dominokit.github.io/domino-ui-demo/index.html?theme=indigo#mdiicons and https://materialdesignicons.com/) with metainformation. 

* Allows easy update icon set in the application/Domino UI 
* Ptovide icon metainformation: tags, aliases, icon version and author.
* Provide single collection with all icons 
* Allows to select icon(s) by tag or alias
* Provide complete list of tags for current icon set.

### How to Use ###

#### Add annotation processor to your build
to enable annotation processor to generate the icon sources add into pom.xml `<build><plugins>` section 
```xml
	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-compiler-plugin</artifactId>
		<configuration>
			<annotationProcessorPaths>
				<path>
					<groupId>org.jresearch.commons.gwt.mdIcon</groupId>
					<artifactId>org.jresearch.commons.gwt.mdIcon.processor</artifactId>
					<version>1.0.0</version>
				</path>
			</annotationProcessorPaths>
		</configuration>
	</plugin>
```
 
to use `MdIcon` annotation in code of your application add into pom.xml `<dependencies>` section
```xml
	<dependency>
		<groupId>org.jresearch.commons.gwt.mdIcon</groupId>
		<artifactId>org.jresearch.commons.gwt.mdIcon.processor</artifactId>
		<version>1.0.0</version>
		<scope>provided</scope>
	</dependency>
```

#### Add MaterialDesign icon metainformation
Put meta.json (https://github.com/Templarian/MaterialDesign-SVG) to the root of project sources. For current Domino UI it is https://github.com/Templarian/MaterialDesign-SVG/blob/v3.0.39/meta.json. Download it and put into `src\main\resources`
#### Add annotation MdIcon to a package
MdIcon is a package annotation to add it create or update `package-info.java` in the selected package. 
```java
@org.jresearch.commons.gwt.mdIcon.MdIcon
package org.jresearch.gavka.gwt.core.client.module.connection.editor;
```
#### Build it

The `MdIcons` interface will be created in the annotated package. The interface contains all icons from provided `meta.json` separated by alfabet (due to Java limitation on class initialization size)

You can check the gwtMdIcon/icon project as example. 

### How to get sources ###

Main repository (hg): https://bitbucket.org/hortonolite/gwtmdicon

Mirror on GitHub (git): https://github.com/foal/gwtMdIcon


