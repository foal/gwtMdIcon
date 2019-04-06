package org.jresearch.commons.gwt.mdIcon;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
@Documented
public @interface MdIcon {
	Mode value() default Mode.FULL_CLIENT;
}
