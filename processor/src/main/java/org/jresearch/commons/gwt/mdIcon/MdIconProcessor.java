package org.jresearch.commons.gwt.mdIcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
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
import com.squareup.javapoet.TypeSpec;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

@AutoService(Processor.class)
@SuppressWarnings("nls")
public class MdIconProcessor extends AbstractProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MdIconProcessor.class);

	private static final String META_JSON = "meta.json";
	private static final String CLASS_NAME = "MdIcons";
	private static final String LIGHT_CLASS_NAME = "MdiIcons";

	private Elements elements;

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
	public synchronized void init(final ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);
		elements = processingEnvironment.getElementUtils();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		// get first available annotated package and ignore others
		StreamEx.of(roundEnv.getElementsAnnotatedWith(MdIcon.class))
				.filterBy(Element::getKind, ElementKind.PACKAGE)
				.findAny()
				.map(PackageElement.class::cast)
				.ifPresent(this::generateIconEnumerations);
		return true;
	}

	private void generateIconEnumerations(final PackageElement annotatedPackage) {
		final Mode generationMode = StreamEx
				.of(annotatedPackage.getAnnotationMirrors())
				.filterBy(this::getClassName, MdIcon.class.getName())
				.findAny()
				.flatMap(this::getGenerationMode)
				.orElse(Mode.FULL_CLIENT);
		if (generationMode == Mode.FULL_CLIENT) {
			generateIconEnumerations(annotatedPackage.getQualifiedName());
		} else if (generationMode == Mode.ORIG) {
			generateIconLightInterface(loadIconMetaInfo(), annotatedPackage.getQualifiedName());
		}
	}

	private String getClassName(final AnnotationMirror annotation) {
		return annotation.getAnnotationType().toString();
	}

	public Optional<Mode> getGenerationMode(final AnnotationMirror mirror) {
		return EntryStream
				.of(elements.getElementValuesWithDefaults(mirror))
				.filterKeys(k -> k.getSimpleName().toString().equals("value"))
				.values().findAny()
				.map(AnnotationValue::getValue)
				.map(Object::toString)
				.map(Mode::valueOf);
	}

	private Void generateIconLightInterface(final List<MetaIconInfo> infos, final Name packageName) {
		final IconLightIntefaceBuilder lightBuilder = IconLightIntefaceBuilder.create(packageName, LIGHT_CLASS_NAME);
		infos.forEach(lightBuilder::addIconMethod);
		final Builder javaFileBuilder = JavaFile
				.builder(packageName.toString(), lightBuilder.build())
				.indent("\t")
				.skipJavaLangImports(true);
		final JavaFile javaFile = javaFileBuilder.build();

		try {
			final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName.toString() + "." + LIGHT_CLASS_NAME);
			try (Writer wr = jfo.openWriter()) {
				javaFile.writeTo(wr);
			}
		} catch (final IOException e) {
			LOGGER.error("Can't generate icon light interface", e);
		}
		return null;
	}

	@SuppressWarnings("boxing")
	private void generateIconEnumerations(final Name packageName) {
		// To generate set of enumeration grouped by first letter (one is too big for
		// Java)

		StreamEx.of(loadIconMetaInfo())
				.mapToEntry(MdIconProcessor::alfabetClassifier, Function.identity())
				.collapseKeys()
				.mapKeyValue((k, v) -> generateIconEnumeration(v, packageName, k))
				.toListAndThen(l -> generateIconInterface(l, packageName));
	}

	private static char alfabetClassifier(final MetaIconInfo info) {
		final char firstChar = info.name().charAt(0);
		return Character.isJavaIdentifierStart(firstChar) ? Character.toUpperCase(firstChar) : '_';
	}

	private static TypeSpec generateIconEnumeration(final List<MetaIconInfo> infos, final Name packageName, final char groupLetter) {
		final IconEnumBuilder enumBuilder = IconEnumBuilder.create(packageName, CLASS_NAME, groupLetter);
		infos.forEach(enumBuilder::addEnumConstant);
		return enumBuilder.build();
	}

	private Void generateIconInterface(final List<TypeSpec> enums, final Name packageName) {
		final IconIntefaceBuilder builder = IconIntefaceBuilder.create(packageName, CLASS_NAME, enums);
		final Builder javaFileBuilder = JavaFile
				.builder(packageName.toString(), builder.build())
				.indent("\t")
				.skipJavaLangImports(true);
		builder.getStaticImports().forEach(c -> javaFileBuilder.addStaticImport(c, "*"));
		final JavaFile javaFile = javaFileBuilder
				.build();

		try {
			final JavaFileObject jfo = processingEnv.getFiler().createSourceFile(packageName.toString() + "." + CLASS_NAME);
			try (Writer wr = jfo.openWriter()) {
				javaFile.writeTo(wr);
			}
		} catch (final IOException e) {
			LOGGER.error("Can't generate icon enumeration", e);
		}
		return null;
	}

	private List<MetaIconInfo> loadIconMetaInfo() {
		try {
			final FileObject resource = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", META_JSON);
			try (InputStream meta = resource.openInputStream()) {
				final CollectionType infoList = TypeFactory.defaultInstance().constructCollectionType(List.class, ImmutableMetaIconInfo.class);
				return new ObjectMapper()
						.registerModule(new GuavaModule())
						.registerModule(new Jdk8Module())
						.readValue(meta, infoList);
			}
		} catch (final IOException e) {
			LOGGER.error("Can't load icon meta info.", e);
			return ImmutableList.of();
		}
	}

}
