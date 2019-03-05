package org.jresearch.commons.gwt.mdIcon;

import java.util.List;

import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public abstract class BaseIconBuilder {

	protected static final ParameterizedTypeName LIST_OF_STRING = ParameterizedTypeName.get(List.class, String.class);
	protected static final ClassName MDI_ICON = ClassName.get("org.dominokit.domino.ui.icons", "MdiIcon");

	private final FieldSpec icon;
	private final FieldSpec id;
	private final FieldSpec codepoint;
	private final FieldSpec aliases;
	private final FieldSpec tags;
	private final FieldSpec author;
	private final FieldSpec version;
	private final List<Class<?>> staticImports;

	protected BaseIconBuilder() {
		icon = FieldSpec.builder(String.class, "iconName", Modifier.PRIVATE, Modifier.FINAL).build();
		id = FieldSpec.builder(String.class, "id", Modifier.PRIVATE, Modifier.FINAL).build();
		codepoint = FieldSpec.builder(String.class, "codepoint", Modifier.PRIVATE, Modifier.FINAL).build();
		aliases = FieldSpec.builder(LIST_OF_STRING, "aliases", Modifier.PRIVATE, Modifier.FINAL).build();
		tags = FieldSpec.builder(LIST_OF_STRING, "tags", Modifier.PRIVATE, Modifier.FINAL).build();
		author = FieldSpec.builder(String.class, "author", Modifier.PRIVATE, Modifier.FINAL).build();
		version = FieldSpec.builder(String.class, "version", Modifier.PRIVATE, Modifier.FINAL).build();
		// static imports
		staticImports = ImmutableList.of(ImmutableList.class);
	}

	protected Builder addCommonMethods(Builder typeBuilder) {
		return typeBuilder
				.addMethod(createIcon().build())
				.addMethod(getter(icon).build())
				.addMethod(getter(id).build())
				.addMethod(getter(codepoint).build())
				.addMethod(getter(aliases).build())
				.addMethod(getter(tags).build())
				.addMethod(getter(author).build())
				.addMethod(getter(version).build());
	}

	@SuppressWarnings("static-method")
	protected com.squareup.javapoet.MethodSpec.Builder createIcon() {
		return MethodSpec.methodBuilder("icon")
				.addModifiers(Modifier.PUBLIC)
				.returns(MDI_ICON);
	}

	@SuppressWarnings("static-method")
	protected com.squareup.javapoet.MethodSpec.Builder getter(FieldSpec field) {
		return MethodSpec.methodBuilder(getterName(field))
				.addModifiers(Modifier.PUBLIC)
				.returns(field.type);
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

	public FieldSpec getIcon() {
		return icon;
	}

	public FieldSpec getId() {
		return id;
	}

	public FieldSpec getCodepoint() {
		return codepoint;
	}

	public FieldSpec getAliases() {
		return aliases;
	}

	public FieldSpec getTags() {
		return tags;
	}

	public FieldSpec getAuthor() {
		return author;
	}

	public FieldSpec getVersion() {
		return version;
	}

}
