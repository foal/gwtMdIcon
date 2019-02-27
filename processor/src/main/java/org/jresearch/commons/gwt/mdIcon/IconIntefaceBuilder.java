package org.jresearch.commons.gwt.mdIcon;

import java.util.List;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public class IconIntefaceBuilder extends BaseIconBuilder {

	private final Builder poetBuilder;
//	private final ClassName enumName;
//	private final FieldSpec allTags;

	private IconIntefaceBuilder(ClassName iconInterface, List<TypeSpec> enums) {
//		this.enumName = enumName;
//		allTags = FieldSpec.builder(LIST_OF_STRING, "ALL_TAGS", Modifier.PRIVATE, Modifier.STATIC).build();
		poetBuilder = TypeSpec.interfaceBuilder(iconInterface).addTypes(enums);
	}

	public static IconIntefaceBuilder create(CharSequence packageName, CharSequence baseName, List<TypeSpec> enums) {
		ClassName iconInterfaceName = ClassName.get(packageName.toString(), baseName.toString());
		return new IconIntefaceBuilder(iconInterfaceName, enums);
	}

	public TypeSpec build() {
		addCommonMethods(poetBuilder);
//				.addMethod(tags())
//				.addMethod(byAlias())
//				.addMethod(byTag());

		return poetBuilder.build();
	}

//	private MethodSpec tags() {
//		return MethodSpec.methodBuilder("tags")
//				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//				.returns(LIST_OF_STRING)
//				.beginControlFlow("if ($N == null)", allTags)
//				.addStatement("$N = $T.of(values()).map($T::getTags).flatMap($T::stream).distinct().collect($T.toList())", allTags, Stream.class, enumName, List.class, Collectors.class)
//				.endControlFlow()
//				.addStatement("return $N", allTags)
//				.build();
//	}

//	private MethodSpec byAlias() {
//		return MethodSpec.methodBuilder("byAlias")
//				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//				.addParameter(String.class, "alias")
//				.returns(ParameterizedTypeName.get(ClassName.get(Optional.class), enumName))
//				.addStatement("return $T.of(values()).filter(i -> i.getAliases().contains(alias)).findAny()", Stream.class)
//				.build();
//	}

//	private MethodSpec byTag() {
//		return MethodSpec.methodBuilder("byTag")
//				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//				.addParameter(String.class, "tag")
//				.returns(ParameterizedTypeName.get(ClassName.get(List.class), enumName))
//				.addStatement("return $T.of(values()).filter(i -> i.getTags().contains(tag)).collect($T.toList())", Stream.class, Collectors.class)
//				.build();
//	}

	@Override
	protected com.squareup.javapoet.MethodSpec.Builder getter(FieldSpec field) {
		return super.getter(field).addModifiers(Modifier.ABSTRACT);
	}

}
