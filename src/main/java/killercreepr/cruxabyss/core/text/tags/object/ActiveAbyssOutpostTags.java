package killercreepr.cruxabyss.core.text.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.HookedObjectContainer;
import killercreepr.crux.api.text.hook.HookedPrefixBuilder;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.hook.StringHookedObjectTag;
import killercreepr.crux.core.text.hook.StringListHookedObjectTag;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActiveAbyssOutpostTags implements ObjectTag<ActiveAbyssOutpost> {
    @NotNull
    @Override
    public Class<ActiveAbyssOutpost> getObjectType() {
        return ActiveAbyssOutpost.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("active_abyss_outpost");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull ActiveAbyssOutpost object, @NotNull TagParser tags) {
        return null;
    }

    @Override
    public @Nullable HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull ActiveAbyssOutpost object, @NotNull TagParser tags) {
        return HookedObjectContainer.string()
            .addAll(tags.hookStrings(object.getData(), HookedPrefixBuilder.overwrite(
                FormatPrefix.simple("active_abyss_outpost_data/")
            )))
            ;
    }

    @Override
    public @Nullable HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull ActiveAbyssOutpost object, @NotNull TagParser tags) {
        return HookedObjectContainer.stringList()
            .addAll(tags.hookStringLists(object.getData(), HookedPrefixBuilder.overwrite(
                FormatPrefix.simple("active_abyss_outpost_data/")
            )))
            ;
    }
}
