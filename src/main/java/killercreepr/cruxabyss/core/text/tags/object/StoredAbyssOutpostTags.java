package killercreepr.cruxabyss.core.text.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.core.registries.AbyssRegistries;
import killercreepr.cruxabyss.core.structure.outpost.AbyssOutpostData;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoredAbyssOutpostTags implements ObjectTag<AbyssOutpostData> {
    @NotNull
    @Override
    public Class<AbyssOutpostData> getObjectType() {
        return AbyssOutpostData.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("abyss_outpost_data");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull AbyssOutpostData object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("upgrade_level", (args, ctx) ->{
                Key upgradeKey = Crux.key(args.get(0));
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                return object.getUpgradeLevel(upgrade) + "";
            }))
            .add(Tag.string("owner", (args, ctx) -> object.owner + ""))
            .add(Tag.string("time_captured", (args, ctx) ->{
                return (object.timeCaptured == null ? 0L : object.timeCaptured) + "";
            }))
            ;
    }
}
