package org.jresearch.commons.gwt.mdIcon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.dominokit.domino.ui.icons.MdiIcon;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("nls")
public enum IconEnumPrototype {

	ICON(MdiIcon.create(""), "id", "codepoint", ImmutableList.of("aliases"), ImmutableList.of("tags"), "author", "version");

	private static List<String> ALL_TAGS;

	private final MdiIcon icon;
	private final String id;
	private final String codepoint;
	private final List<String> aliases;
	private final List<String> tags;
	private final String author;
	private final String version;

	private IconEnumPrototype(MdiIcon icon, String id, String codepoint, @Nonnull List<String> aliases, @Nonnull List<String> tags, String author, String version) {
		this.icon = icon;
		this.id = id;
		this.codepoint = codepoint;
		this.aliases = ImmutableList.copyOf(aliases);
		this.tags = ImmutableList.copyOf(tags);
		this.author = author;
		this.version = version;
	}

	public MdiIcon getIcon() {
		return icon;
	}

	public String getId() {
		return id;
	}

	public String getCodepoint() {
		return codepoint;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getAuthor() {
		return author;
	}

	public String getVersion() {
		return version;
	}

	public static List<String> tags() {
		if (ALL_TAGS == null) {
			ALL_TAGS = Stream.of(values()).map(IconEnumPrototype::getTags).flatMap(List::stream).distinct().collect(Collectors.toList());
		}
		return ALL_TAGS;
	}

	public static Optional<IconEnumPrototype> byAlias(String alias) {
		return Stream.of(values()).filter(i -> i.getAliases().contains(alias)).findAny();
	}

	public static List<IconEnumPrototype> byTag(String tag) {
		return Stream.of(values()).filter(i -> i.getTags().contains(tag)).collect(Collectors.toList());
	}
}
