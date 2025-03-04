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
import killercreepr.cruxabyss.core.world.abyss.event.OutpostInvasionEvent;
import killercreepr.usurvive.api.entity.player.UPlayer;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StoredAbyssOutpostTags implements ObjectTag<AbyssOutpostData> {
    @NotNull
    @Override
    public Class<AbyssOutpostData> getObjectType() {
        return AbyssOutpostData.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("abyss_outpost_data_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull AbyssOutpostData object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("upgrade_level", (args, ctx) ->{
                Key upgradeKey = Crux.key(args.get(0));
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(upgradeKey);
                return object.getUpgradeLevel(upgrade) + "";
            }))
            .add(Tag.string("has_event", (args, ctx) ->{
                String type = ctx.deserializeString(args.get(0));
                switch (type.toLowerCase()){
                    case "outpost_invasion" ->{
                        return object.hasWorldEvent(OutpostInvasionEvent.class, null) + "";
                    }
                }
                return "false";
            }))
            .add(Tag.string("owner", (args, ctx) -> object.owner + ""))
            .add(Tag.string("owner_name", (args, ctx) ->{
                UUID uuid = object.owner;
                if(uuid==null) return "null";
                var uPlay = UPlayer.getPlayer(uuid);
                if(uPlay==null) return "null";
                return uPlay.getLastKnownName() + "";
            }))
            .add(Tag.string("is_owner", (args, ctx) ->{
                UUID uuid = UUID.fromString(ctx.deserializeString(args.get(0)));
                return object.isOwner(uuid) + "";
            }))
            .add(Tag.string("is_member", (args, ctx) ->{
                UUID uuid = UUID.fromString(ctx.deserializeString(args.get(0)));
                return object.isExplicitMember(uuid) + "";
            }))
            .add(Tag.string("is_member_or_owner", (args, ctx) ->{
                UUID uuid = UUID.fromString(ctx.deserializeString(args.get(0)));
                return object.isMemberOrOwner(uuid) + "";
            }))
            .add(Tag.string("time_captured", (args, ctx) ->{
                return (object.timeCaptured == null ? 0L : object.timeCaptured) + "";
            }))
            .add(Tag.string("time_invaded", (args, ctx) ->{
                return (object.timeInvaded == null ? 0L : object.timeInvaded) + "";
            }))
            .add(Tag.string("time_last_invasion", (args, ctx) ->{
                return (object.timeLastInvasion == null ? 0L : object.timeLastInvasion) + "";
            }))
            ;
    }
}
