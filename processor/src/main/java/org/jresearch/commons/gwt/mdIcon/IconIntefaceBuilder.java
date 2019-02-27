package org.jresearch.commons.gwt.mdIcon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public class IconIntefaceBuilder extends BaseIconBuilder {

	protected static final ParameterizedTypeName LIST_OF_ICON = ParameterizedTypeName.get(List.class, String.class);

	private final Builder poetBuilder;
	private FieldSpec values;
	private ClassName iconInterface;

	private IconIntefaceBuilder(ClassName iconInterface, List<TypeSpec> enums) {
		this.iconInterface = iconInterface;
		ParameterizedTypeName listOfIcon = ParameterizedTypeName.get(ClassName.get(List.class), iconInterface);
		com.squareup.javapoet.CodeBlock.Builder valueInitializer = CodeBlock.builder()
				.add("$T.<$T>builder()", ImmutableList.class, iconInterface);
		enums.forEach(e -> add(valueInitializer, e));
		valueInitializer.add(".build()");
		values = FieldSpec.builder(listOfIcon, "VALUES", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
				.initializer(valueInitializer.build())
				.build();
		poetBuilder = TypeSpec.interfaceBuilder(iconInterface)
				.addField(values)
				.addTypes(enums);

	}

	private static void add(com.squareup.javapoet.CodeBlock.Builder valueInitializer, TypeSpec enumType) {
		valueInitializer.add(".add($L.values())$Z", enumType.name);
	}

	public static IconIntefaceBuilder create(CharSequence packageName, CharSequence baseName, List<TypeSpec> enums) {
		ClassName iconInterfaceName = ClassName.get(packageName.toString(), baseName.toString());
		return new IconIntefaceBuilder(iconInterfaceName, enums);
	}

	public TypeSpec build() {
		addCommonMethods(poetBuilder)
				.addMethod(tags())
				.addMethod(byAlias())
				.addMethod(byTag());

		return poetBuilder.build();
	}

	private MethodSpec tags() {
		return MethodSpec.methodBuilder("tags")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(LIST_OF_STRING)
				.addStatement("return $N.stream().map($T::getTags).flatMap($T::stream).distinct().collect($T.toList())", values, iconInterface, List.class, Collectors.class)
				.build();
	}

	private MethodSpec byAlias() {
		return MethodSpec.methodBuilder("byAlias")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(String.class, "alias")
				.returns(ParameterizedTypeName.get(ClassName.get(Optional.class), iconInterface))
				.addStatement("return $N.stream().filter(i -> i.getAliases().contains(alias)).findAny()", values)
				.build();
	}

	private MethodSpec byTag() {
		return MethodSpec.methodBuilder("byTag")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(String.class, "tag")
				.returns(ParameterizedTypeName.get(ClassName.get(List.class), iconInterface))
				.addStatement("return $N.stream().filter(i -> i.getTags().contains(tag)).collect($T.toList())", values, Collectors.class)
				.build();
	}

	@Override
	protected com.squareup.javapoet.MethodSpec.Builder getter(FieldSpec field) {
		return super.getter(field).addModifiers(Modifier.ABSTRACT);
	}

}
