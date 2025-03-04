package killercreepr.cruxabyss.core.text.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.SimpleObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssRecallAnchor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class AbyssalRecallAnchorTags implements SimpleObjectTag<AbyssRecallAnchor> {
    @NotNull
    @Override
    public Class<AbyssRecallAnchor> getObjectType() {
        return AbyssRecallAnchor.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("abyss_recall_anchor_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull AbyssRecallAnchor anchor, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("can_respawn_at", (args, ctx) -> anchor.canRespawnAt() + ""))
            .add(Tag.string("charges", (args, ctx) -> anchor.getCharges() + ""))
            .add(Tag.string("is_destroyed", (args, ctx) -> anchor.isDestroyed() + ""))
            ;
    }

    @Override
    public @Nullable Map<Object, FormatPrefix> hookObjects(AbyssRecallAnchor object) {
        return Map.of(
            object.getPosition(), FormatPrefix.simple("pos/")
        );
    }
}
