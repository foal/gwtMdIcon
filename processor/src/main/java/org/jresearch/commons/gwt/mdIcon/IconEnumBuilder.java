package org.jresearch.commons.gwt.mdIcon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public class IconEnumBuilder {
	// ICON(MdiIcon.create("name"), "id", "codepoint", ImmutableList.of("aliases"),
	// ImmutableList.of("tags"), "author", "version");
	private static final String ENUM_PARAMS = "$T.create($S), $S, $S, $T.of($L), $T.of($L), $S, $S";
	private static final Collector<CharSequence, ?, String> VAR_JOINER = Collectors.joining("\", \"", "\"", "\"");

	private final Builder poetBuilder;
	private final ClassName mdiIcon;
	private final FieldSpec icon;
	private final FieldSpec id;
	private final FieldSpec codepoint;
	private final FieldSpec aliases;
	private final FieldSpec tags;
	private final FieldSpec author;
	private final FieldSpec version;
	private final FieldSpec allTags;
	private final ParameterizedTypeName listOfString;
	private final ClassName enumName;
	private final List<Class<?>> staticImports;

	private IconEnumBuilder(ClassName enumName) {
		this.enumName = enumName;
		poetBuilder = TypeSpec.enumBuilder(enumName).addModifiers(Modifier.PUBLIC);
		listOfString = ParameterizedTypeName.get(List.class, String.class);
		mdiIcon = ClassName.get("org.dominokit.domino.ui.icons", "MdiIcon");
		allTags = FieldSpec.builder(listOfString, "ALL_TAGS", Modifier.PRIVATE, Modifier.STATIC).build();
		icon = FieldSpec.builder(mdiIcon, "icon", Modifier.PRIVATE, Modifier.FINAL).build();
		id = FieldSpec.builder(String.class, "id", Modifier.PRIVATE, Modifier.FINAL).build();
		codepoint = FieldSpec.builder(String.class, "codepoint", Modifier.PRIVATE, Modifier.FINAL).build();
		aliases = FieldSpec.builder(listOfString, "aliases", Modifier.PRIVATE, Modifier.FINAL).build();
		tags = FieldSpec.builder(listOfString, "tags", Modifier.PRIVATE, Modifier.FINAL).build();
		author = FieldSpec.builder(String.class, "author", Modifier.PRIVATE, Modifier.FINAL).build();
		version = FieldSpec.builder(String.class, "version", Modifier.PRIVATE, Modifier.FINAL).build();

		// static imports
		staticImports = ImmutableList.of(ImmutableList.class);
	}

	public static IconEnumBuilder create(CharSequence packageName, CharSequence simpleName) {
		return new IconEnumBuilder(ClassName.get(packageName.toString(), simpleName.toString()));
	}

	public IconEnumBuilder addEnumConstant(MetaIconInfo info) {
		poetBuilder.addEnumConstant(toJava(info.name()), generateParameters(info));
		return this;
	}

	private static String toJava(String name) {
		String identifier = toJavaIdentifier(name);
		return SourceVersion.isKeyword(identifier) ? '_' + identifier : identifier;
	}

	private static String toJavaIdentifier(String name) {
		StringBuilder sb = new StringBuilder(toFirstJavaCharacter(name.charAt(0)));
		for (int i = 1; i < name.length(); i++) {
			sb.append(toJavaCharacter(name.charAt(i)));
		}
		return sb.toString();
	}

	private static char toJavaCharacter(char nameCharacter) {
		return Character.isJavaIdentifierPart(nameCharacter) ? nameCharacter : '_';
	}

	private static String toFirstJavaCharacter(char firstNameCharacter) {
		return Character.isJavaIdentifierStart(firstNameCharacter) ? String.valueOf(firstNameCharacter) : "_" + toJavaCharacter(firstNameCharacter);
	}

	private TypeSpec generateParameters(MetaIconInfo info) {
		return TypeSpec.anonymousClassBuilder(ENUM_PARAMS, mdiIcon, info.name(), info.id(), info.codepoint(), ImmutableList.class, var(info.aliases()), ImmutableList.class, var(info.tags()), info.author(), info.version()).build();
	}

	private static String var(List<String> args) {
		return args.isEmpty() ? "" : args.stream().collect(VAR_JOINER);
	}

	public TypeSpec build() {
		return poetBuilder
				.addField(allTags)
				.addField(icon)
				.addField(id)
				.addField(codepoint)
				.addField(aliases)
				.addField(tags)
				.addField(author)
				.addField(version)
				.addMethod(constructor())
				.addMethod(getter(icon))
				.addMethod(getter(id))
				.addMethod(getter(codepoint))
				.addMethod(getter(aliases))
				.addMethod(getter(tags))
				.addMethod(getter(author))
				.addMethod(getter(version))

				.addMethod(tags())
				.addMethod(byAlias())
				.addMethod(byTag())

				.build();
	}

	private MethodSpec tags() {
		return MethodSpec.methodBuilder("tags")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(listOfString)
				.beginControlFlow("if ($N == null)", allTags)
				.addStatement("$N = $T.of(values()).map($T::getTags).flatMap($T::stream).distinct().collect($T.toList())", allTags, Stream.class, enumName, List.class, Collectors.class)
				.endControlFlow()
				.addStatement("return $N", allTags)
				.build();
	}

	private MethodSpec byAlias() {
		return MethodSpec.methodBuilder("byAlias")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(String.class, "alias")
				.returns(ParameterizedTypeName.get(ClassName.get(Optional.class), enumName))
				.addStatement("return $T.of(values()).filter(i -> i.getAliases().contains(alias)).findAny()", Stream.class)
				.build();
	}

	private MethodSpec byTag() {
		return MethodSpec.methodBuilder("byTag")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(String.class, "tag")
				.returns(ParameterizedTypeName.get(ClassName.get(List.class), enumName))
				.addStatement("return $T.of(values()).filter(i -> i.getTags().contains(tag)).collect($T.toList())", Stream.class, Collectors.class)
				.build();
	}

	private MethodSpec constructor() {
		com.squareup.javapoet.MethodSpec.Builder result = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);
		addField(result, icon);
		addField(result, id);
		addField(result, codepoint);
		addField(result, aliases);
		addField(result, tags);
		addField(result, author);
		addField(result, version);
		return result.build();
	}

	private static void addField(com.squareup.javapoet.MethodSpec.Builder result, FieldSpec field) {
		result
				.addParameter(field.type, field.name)
				.addStatement("this.$N = $N", field.name, field.name);
	}

	private static MethodSpec getter(FieldSpec field) {
		return MethodSpec.methodBuilder(getterName(field))
				.returns(field.type)
				.addStatement("return $N", field)
				.build();
	}

	private static String getterName(FieldSpec field) {
		boolean bool = TypeName.BOOLEAN.equals(field.type);
		return (bool ? "is" : "get") + cap(field.name);
	}

	private static String cap(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public List<Class<?>> getStaticImports() {
		return staticImports;
	}

}
