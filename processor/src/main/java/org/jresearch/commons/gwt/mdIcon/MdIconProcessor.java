package org.jresearch.commons.gwt.mdIcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.JavaFile.Builder;

import one.util.streamex.StreamEx;

@AutoService(Processor.class)
@SuppressWarnings("nls")
public class MdIconProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MdIconProcessor.class);

	private static final String META_JSON = "meta.json";
	private static final String CLASS_NAME = "MdIcons";

	@SuppressWarnings("null")
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of(MdIcon.class.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// get any available annotated package and ignore others
		StreamEx.of(roundEnv.getElementsAnnotatedWith(MdIcon.class))
				.filterBy(Element::getKind, ElementKind.PACKAGE)
				.findAny()
				.map(PackageElement.class::cast)
				.map(PackageElement::getQualifiedName)
				.ifPresent(this::generateIconEnumeration);
		return true;
	}

	private void generateIconEnumeration(Name packageName) {
		List<MetaIconInfo> infos = loadIconMetaInfo();
		IconEnumBuilder enumBuilder = IconEnumBuilder.create(packageName, CLASS_NAME);
		infos.forEach(enumBuilder::addEnumConstant);
		Builder javaFileBuilder = JavaFile
				.builder(packageName.toString(), enumBuilder.build())
				.indent("\t")
				.skipJavaLangImports(true);
		enumBuilder.getStaticImports().forEach(c -> javaFileBuilder.addStaticImport(c, "*"));
		JavaFile javaFile = javaFileBuilder
				.build();

		try {
			JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName.toString() + "." + CLASS_NAME);
			try (Writer wr = jfo.openWriter()) {
				javaFile.writeTo(wr);
			}
		} catch (IOException e) {
			LOGGER.error("Can't generate icon enumeration", e);
		}

	}

	private List<MetaIconInfo> loadIconMetaInfo() {
		try {
			FileObject resource = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", META_JSON);
			try (InputStream meta = resource.openInputStream()) {
				CollectionType infoList = TypeFactory.defaultInstance().constructCollectionType(List.class, ImmutableMetaIconInfo.class);
				return new ObjectMapper()
						.registerModule(new GuavaModule())
						.registerModule(new Jdk8Module())
						.readValue(meta, infoList);
			}
		} catch (IOException e) {
			LOGGER.error("Can't load icon meta info", e);
			return ImmutableList.of();
		}
	}
}
