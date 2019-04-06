package org.jresearch.commons.gwt.mdIcon;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public class IconEnumBuilder extends BaseIconBuilder {
	/**
	 * <pre>
	 * ICON(MdiIcon.create("name"), "id", "codepoint", ImmutableList.of("aliases"), ImmutableList.of("tags"), "author", "version");
	 * </pre>
	 */
	private static final String ENUM_PARAMS = "\"mdi-$L\", $S, $S, $T.of($L), $T.of($L), $S, $S";
	private static final Collector<CharSequence, ?, String> VAR_JOINER = Collectors.joining("\", \"", "\"", "\"");

	private final Builder poetBuilder;

	private IconEnumBuilder(final ClassName enumName, final ClassName iconInterface) {
		poetBuilder = TypeSpec
				.enumBuilder(enumName)
				.addSuperinterface(iconInterface)
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
	}

	public static IconEnumBuilder create(final CharSequence packageName, final CharSequence baseName, final char groupChar) {
		final ClassName iconInterfaceName = ClassName.get(packageName.toString(), baseName.toString());
		final ClassName enumClassName = ClassName.get(packageName.toString(), baseName.toString(), String.valueOf(groupChar));
		return new IconEnumBuilder(enumClassName, iconInterfaceName);
	}

	public IconEnumBuilder addEnumConstant(final MetaIconInfo info) {
		poetBuilder.addEnumConstant(toJava(info.name()), generateParameters(info));
		return this;
	}

	private static TypeSpec generateParameters(final MetaIconInfo info) {
		return TypeSpec.anonymousClassBuilder(ENUM_PARAMS, info.name(), info.id(), info.codepoint(), ImmutableList.class, var(info.aliases()), ImmutableList.class, var(info.tags()), info.author(), info.version()).build();
	}

	private static String var(final List<String> args) {
		return args.isEmpty() ? "" : args.stream().collect(VAR_JOINER);
	}

	public TypeSpec build() {
		poetBuilder
				.addField(getIcon())
				.addField(getId())
				.addField(getCodepoint())
				.addField(getAliases())
				.addField(getTags())
				.addField(getAuthor())
				.addField(getVersion())
				.addMethod(constructor());
		addCommonMethods(poetBuilder);

		return poetBuilder.build();
	}

	private MethodSpec constructor() {
		final com.squareup.javapoet.MethodSpec.Builder result = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE);
		addField(result, getIcon());
		addField(result, getId());
		addField(result, getCodepoint());
		addField(result, getAliases());
		addField(result, getTags());
		addField(result, getAuthor());
		addField(result, getVersion());
		return result.build();
	}

	private static void addField(final com.squareup.javapoet.MethodSpec.Builder result, final FieldSpec field) {
		result
				.addParameter(field.type, field.name)
				.addStatement("this.$N = $N", field.name, field.name);
	}

	@Override
	protected com.squareup.javapoet.MethodSpec.Builder getter(final FieldSpec field) {
		return super.getter(field).addStatement("return $N", field).addAnnotation(Override.class);
	}

	@Override
	protected com.squareup.javapoet.MethodSpec.Builder createIcon() {
		return super.createIcon().addStatement("return $T.create(iconName)", MDI_ICON).addAnnotation(Override.class);
	}

}
