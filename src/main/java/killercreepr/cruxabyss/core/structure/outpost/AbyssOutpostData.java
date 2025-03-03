package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostData;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.values.AbyssOutpostInvasionCfg;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.game.entity.MobWaveGroup;
import killercreepr.cruxabyss.core.world.abyss.event.OutpostInvasionEvent;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.component.TickedStoredComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssOutpostData implements StoredStructureComponent, TickedStoredComponent, OutpostData {
    public UUID owner;
    public Long timeCaptured;
    public Long timeInvaded;
    public Long timeLastInvasion;
    protected final Map<OutpostUpgrade, Integer> upgrades = new HashMap<>();
    protected final Map<OutpostUpgrade, TickedOutpostUpgrade> storedUpgrades = new ConcurrentHashMap<>();
    protected static final int tickRate = 1;
    protected final StoredStructure stored;

    public AbyssOutpostData(StoredStructure stored) {
        this.stored = stored;
    }

    public AbyssOutpostInvasionCfg cfg(){
        return CruxAbyss.inst().worldEventCfgs().ABYSS_OUTPOST_INVASION;
    }

    public void invasion(){
        owner = null;
        timeInvaded = System.currentTimeMillis();
    }

    public void invasionStarted(){
        timeLastInvasion = System.currentTimeMillis();
    }

    public Collection<UUID> getMembers(){
        if(owner == null) return Set.of();
        UPlayer uPlayer = USurvivePlugin.inst().getPlayerManager().getPlayer(owner);
        Collection<UUID> list = new HashSet<>();
        list.addAll(uPlayer.getFriends());
        list.addAll(uPlayer.getPartyMembers());
        return list;
    }

    public Collection<UUID> getMembersAndOwner(){
        if(owner == null) return Set.of();
        Collection<UUID> list = getMembers();
        list.add(owner);
        return list;
    }

    public boolean isMemberOrOwner(UUID uuid){
        if(owner == null) return false;
        return uuid.equals(owner) || isMember(uuid);
    }

    public boolean isMemberOrOwner(UUID uuid, UPlayer owner){
        if(this.owner == null) return false;
        return uuid.equals(this.owner) || isMember(uuid, owner);
    }

    public boolean isMember(UUID uuid){
        if(owner == null) return false;
        return isMember(uuid, USurvivePlugin.inst().getPlayerManager().getPlayer(owner));
    }

    public boolean isMember(UUID uuid, UPlayer owner){
        return uuid.equals(this.owner) || owner.isApartOfParty(uuid) || owner.hasFriend(uuid);
    }

    @Override
    public void onFileSave(@NotNull FileContext<?> ctx, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = ctx.getRegistry();
        if(owner != null){
            o.add("owner", reg.serializeToFile(owner));
        }
        if(timeCaptured != null){
            o.addProperty("time_captured", timeCaptured);
        }
        if(!upgrades.isEmpty()){
            FileArray a = new FileArray(upgrades.size());
            upgrades.forEach((upgrade, level) ->{
                FileObject obj = new FileObject();
                obj.add("key", reg.serializeToFile(upgrade.key()));
                obj.addProperty("level", level);
                a.add(obj);
            });
            o.add("upgrades", a);
        }
        if(timeInvaded != null){
            o.addProperty("time_invaded", timeInvaded);
        }
        if(timeLastInvasion != null){
            o.addProperty("time_last_invasion", timeLastInvasion);
        }
    }

    public boolean wasInvadedWithin(int ticks){
        return timeInvaded != null && CruxMath.hasOccurredWithin(timeInvaded, ticks);
    }

    public boolean hadInvasionStartedWithin(int ticks){
        return timeLastInvasion != null && CruxMath.hasOccurredWithin(timeLastInvasion, ticks);
    }

    @Override
    public void onActiveCreated(@NotNull ActiveStructure structure) {
        structure.set(AbyssComponents.ACTIVE_ABYSS_OUTPOST, new ActiveAbyssOutpost(structure));
    }

    @Override
    public void storedStarted(StructureWorldModule module) {
        initiateUpgrades();
    }

    @Override
    public void storedStopped(StructureWorldModule module) {
        storedUpgrades.values().forEach(t -> t.stopped(tick, tickRate));
    }

    public void initiateUpgrades(){
        storedUpgrades.clear();
        upgrades.forEach((upgrade, level) ->{
            TickedOutpostUpgrade stored = upgrade.createStored(this, level);
            if(stored == null) return;
            storedUpgrades.put(upgrade, stored);
        });
        storedUpgrades.values().forEach(t -> t.started(tick, tickRate));
    }

    public void attemptInvasion(){
        CruxWorld world = CruxCore.core().worldManager().getWorld(stored.getChunk().worldKey());
        if(world == null) return;
        WorldEventsModule events = world.getModule(WorldEventsModule.class);
        if(events == null) return;
        if(events.hasApplicableWorldEvents(OutpostInvasionEvent.class, e -> e.getTargetStructure().equals(stored))) return;

        AbyssOutpostInvasionCfg cfg = CruxAbyss.inst().worldEventCfgs().ABYSS_OUTPOST_INVASION;
        MobWaveGroup waveGroup = cfg.ABYSS_OUTPOST_INVASION_WAVES().valueOrThrow().populateLoot(
            LootContext.empty()
        ).getFirst();

        events.addWorldEvent(new OutpostInvasionEvent(cfg,
            world, stored, waveGroup, cfg.ABYSS_OUTPOST_INVASION_MAX_CAPTURE_TIME().value().intValue()
        ));
    }

    protected int tick = 0;
    @Override
    public void storedTick(StructureWorldModule module) {
        if(owner == null || module.isActive(stored)) return;
        Player p = Crux.getServer().getPlayer(owner);
        if(p == null) return;
        tick++;
        if(tick < 200) return;
        tick = 0;

        AbyssOutpostInvasionCfg cfg = cfg();
        if(CruxMath.testChance(cfg.ABYSS_OUTPOST_INVASION_STORED_CHANCE().value().doubleValue())){
            if(!hadInvasionStartedWithin(cfg.ABYSS_OUTPOST_INVASION_COOLDOWN().value().intValue())){
                attemptInvasion();
            }
        }

        storedUpgrades.values().removeIf(upgrade ->{
            if(upgrade.shouldStop(tick, tickRate)){
                upgrade.stopped(tick, tickRate);
                return true;
            }
            upgrade.tick(tick, tickRate);
            return false;
        });
    }

    @Override
    public Map<OutpostUpgrade, Integer> getUpgrades() {
        return upgrades;
    }

    @Override
    public boolean hasUpgrade(OutpostUpgrade upgrade) {
        return upgrades.containsKey(upgrade);
    }

    @Override
    public int getUpgradeLevel(OutpostUpgrade upgrade) {
        return upgrades.getOrDefault(upgrade, 0);
    }

    @Override
    public void setUpgradeLevel(OutpostUpgrade upgrade, int level) {
        int previousLevel = getUpgradeLevel(upgrade);
        if(previousLevel == level) return;

        ActiveStructure active = CruxCore.core().worldManager().getWorld(stored.getChunk().worldKey())
                .getModule(StructureWorldModule.class)
                    .getActiveStructures().get(stored.getChunk().getChunkKey(), stored.getPosition());
        if(active != null && active.has(AbyssComponents.ACTIVE_ABYSS_OUTPOST)){
            ActiveAbyssOutpost abyss = active.get(AbyssComponents.ACTIVE_ABYSS_OUTPOST);
            TickedOutpostUpgrade stored = abyss.activeUpgrades.get(upgrade);
            if(stored == null){
                stored = upgrade.createActive(abyss, level);
                if(stored != null){
                    abyss.activeUpgrades.put(upgrade, stored);
                    stored.started(tick, tickRate);
                }
            }else stored.setLevel(level);
        }

        upgrades.put(upgrade, level);

        TickedOutpostUpgrade stored = storedUpgrades.get(upgrade);
        if(stored == null){
            stored = upgrade.createStored(this, level);
            if(stored != null){
                storedUpgrades.put(upgrade, stored);
                stored.started(tick, tickRate);
            }
            return;
        }
        stored.setLevel(level);
    }

    @Override
    public void removeUpgrade(OutpostUpgrade upgrade) {
        upgrades.remove(upgrade);
        TickedOutpostUpgrade stored = storedUpgrades.remove(upgrade);
        if(stored != null) stored.stopped(tick, tickRate);
    }
}
