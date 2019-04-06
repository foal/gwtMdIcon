package org.jresearch.commons.gwt.mdIcon;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;

@SuppressWarnings("nls")
public class IconLightIntefaceBuilder extends BaseIconBuilder {

	private final Builder poetBuilder;

	private IconLightIntefaceBuilder(final ClassName interfaceName) {
		poetBuilder = TypeSpec.interfaceBuilder(interfaceName).addModifiers(Modifier.PUBLIC);

	}

	public IconLightIntefaceBuilder addIconMethod(final MetaIconInfo info) {
		poetBuilder.addMethod(iconMethod(info.name()));
		return this;
	}

	public static IconLightIntefaceBuilder create(final CharSequence packageName, final CharSequence baseName) {
		final ClassName iconInterfaceName = ClassName.get(packageName.toString(), baseName.toString());
		return new IconLightIntefaceBuilder(iconInterfaceName);
	}

	public TypeSpec build() {
		return poetBuilder.build();
	}

	private static MethodSpec iconMethod(final String iconName) {
		return MethodSpec.methodBuilder(toJava(iconName) + "_mdi")
				.addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				.returns(MDI_ICON)
				.addStatement("return $T.create(\"mdi-$L\")", MDI_ICON, iconName)
				.build();
	}

}
