package org.jresearch.commons.gwt.mdIcon;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableMetaIconInfo.class)
@JsonDeserialize(as = ImmutableMetaIconInfo.class)
public interface MetaIconInfo {
	String id();

	String name();

	String codepoint();

	List<String> aliases();

	List<String> tags();

	String author();

	String version();
}
