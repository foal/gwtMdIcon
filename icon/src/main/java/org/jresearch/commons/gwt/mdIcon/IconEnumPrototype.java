package org.jresearch.commons.gwt.mdIcon;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.dominokit.domino.ui.icons.MdiIcon;

import com.google.common.collect.ImmutableList;

public interface IconEnumPrototype {

	List<IconEnumPrototype> VALUES = ImmutableList.<IconEnumPrototype>builder()
			.add(IconEnumPrototype1.values())
			.add(IconEnumPrototype2.values())
			.build();

	enum IconEnumPrototype1 implements IconEnumPrototype {

		ICON1(MdiIcon.create(""), "id", "codepoint", ImmutableList.of("aliases"), ImmutableList.of("tags"), "author", "version");

		private final MdiIcon icon;
		private final String id;
		private final String codepoint;
		private final List<String> aliases;
		private final List<String> tags;
		private final String author;
		private final String version;

		private IconEnumPrototype1(MdiIcon icon, String id, String codepoint, @Nonnull List<String> aliases, @Nonnull List<String> tags, String author, String version) {
			this.icon = icon;
			this.id = id;
			this.codepoint = codepoint;
			this.aliases = ImmutableList.copyOf(aliases);
			this.tags = ImmutableList.copyOf(tags);
			this.author = author;
			this.version = version;
		}

		@Override
		public MdiIcon getIcon() {
			return icon;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getCodepoint() {
			return codepoint;
		}

		@Override
		public List<String> getAliases() {
			return aliases;
		}

		@Override
		public List<String> getTags() {
			return tags;
		}

		@Override
		public String getAuthor() {
			return author;
		}

		@Override
		public String getVersion() {
			return version;
		}

	}

	enum IconEnumPrototype2 implements IconEnumPrototype {

		ICON2(MdiIcon.create(""), "id", "codepoint", ImmutableList.of("aliases"), ImmutableList.of("tags"), "author", "version");

		private final MdiIcon icon;
		private final String id;
		private final String codepoint;
		private final List<String> aliases;
		private final List<String> tags;
		private final String author;
		private final String version;

		private IconEnumPrototype2(MdiIcon icon, String id, String codepoint, @Nonnull List<String> aliases, @Nonnull List<String> tags, String author, String version) {
			this.icon = icon;
			this.id = id;
			this.codepoint = codepoint;
			this.aliases = ImmutableList.copyOf(aliases);
			this.tags = ImmutableList.copyOf(tags);
			this.author = author;
			this.version = version;
		}

		@Override
		public MdiIcon getIcon() {
			return icon;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getCodepoint() {
			return codepoint;
		}

		@Override
		public List<String> getAliases() {
			return aliases;
		}

		@Override
		public List<String> getTags() {
			return tags;
		}

		@Override
		public String getAuthor() {
			return author;
		}

		@Override
		public String getVersion() {
			return version;
		}

	}

	MdiIcon getIcon();

	String getId();

	String getCodepoint();

	List<String> getAliases();

	List<String> getTags();

	String getAuthor();

	String getVersion();

	public static List<String> tags() {
		return VALUES.stream().map(IconEnumPrototype::getTags).flatMap(List::stream).distinct().collect(Collectors.toList());
	}

	public static Optional<IconEnumPrototype> byAlias(String alias) {
		return VALUES.stream().filter(i -> i.getAliases().contains(alias)).findAny();
	}

	public static List<IconEnumPrototype> byTag(String tag) {
		return VALUES.stream().filter(i -> i.getTags().contains(tag)).collect(Collectors.toList());
	}
}
