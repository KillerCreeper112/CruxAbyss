package killercreepr.cruxabyss.core.text.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.entity.memory.AbyssHolder;
import killercreepr.cruxabyss.core.entity.memory.AbyssWorldDwellerHolder;
import killercreepr.cruxabyss.core.structure.outpost.upgrade.AbyssOutpostUpgrades;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AbyssPlayerTags implements ObjectTag<OfflinePlayer> {
    @NotNull
    @Override
    public Class<OfflinePlayer> getObjectType() {
        return OfflinePlayer.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull OfflinePlayer object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("longest_abyss_outpost_control_duration",(args, ctx) ->{
                if(!object.isOnline()) return "0";
                return AbyssHolder.abyssHolder(object.getPlayer()).getLongestAbyssOutpostControlDuration() + "";
            }))
            .add(Tag.string("owned_abyss_outposts", (args, ctx) ->
                CruxAbyss.inst().getAbyssOutpostManager().getAllOwnedAbyssOutposts(object.getUniqueId()).size() + ""))
            .add(Tag.string("friendly_abyss_outposts", (args, ctx) ->
                CruxAbyss.inst().getAbyssOutpostManager().getAllFriendlyAbyssOutposts(object.getUniqueId()).size() + ""))
            .add(Tag.string("owned_abyss_outposts_with_relay", (args, ctx) -> {
                UUID uuid = object.getUniqueId();
                return CruxAbyss.inst().getAbyssOutpostManager().getAbyssOutposts(e ->{
                    return e.getUpgradeLevel(AbyssOutpostUpgrades.ABYSSAL_RELAY) > 0 && e.isOwner(uuid);
                }).size() + "";
            }))
            .add(Tag.string("friendly_abyss_outposts_with_relay", (args, ctx) ->{
                UUID uuid = object.getUniqueId();
                return CruxAbyss.inst().getAbyssOutpostManager().getAbyssOutposts(e ->{
                    return e.getUpgradeLevel(AbyssOutpostUpgrades.ABYSSAL_RELAY) > 0 && e.isMemberOrOwner(uuid);
                }).size() + "";
            }))
            .add(Tag.string("has_friendly_abyss_outposts_with_relay", (args, ctx) ->{
                UUID uuid = object.getUniqueId();
                return CruxAbyss.inst().getAbyssOutpostManager().checkFirstTrue(e ->{
                    return e.getUpgradeLevel(AbyssOutpostUpgrades.ABYSSAL_RELAY) > 0 && e.isMemberOrOwner(uuid);
                }) + "";
            }))
            .add(Tag.string("abyss_world_dwell_ticks", (args, ctx) ->{
                if(!object.isOnline()) return "0";
                return AbyssWorldDwellerHolder.getAbyssDwellTicks(object.getPlayer()) + "";
            }))
            ;
    }
}
