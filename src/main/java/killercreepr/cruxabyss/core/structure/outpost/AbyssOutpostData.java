package killercreepr.cruxabyss.core.structure.outpost;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.structure.outpost.OutpostData;
import killercreepr.cruxabyss.api.structure.outpost.OutpostUpgrade;
import killercreepr.cruxabyss.api.structure.outpost.TickedOutpostUpgrade;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.entity.mob.AbyssMob;
import killercreepr.cruxabyss.core.world.abyss.event.OutpostInvasionEvent;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.world.entity.NaturalCruxMobSpawn;
import killercreepr.cruxstructures.api.component.StoredStructureComponent;
import killercreepr.cruxstructures.api.component.TickedStoredComponent;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.cruxworlds.api.world.entity.SpawnContext;
import killercreepr.cruxworlds.core.world.entity.SimpleNaturalEntitySpawnGroup;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbyssOutpostData implements StoredStructureComponent, TickedStoredComponent, OutpostData {
    public UUID owner;
    public Long timeCaptured;
    public Long timeInvaded;
    protected final Map<OutpostUpgrade, Integer> upgrades = new HashMap<>();
    protected final Map<OutpostUpgrade, TickedOutpostUpgrade> storedUpgrades = new ConcurrentHashMap<>();
    protected static final int tickRate = 1;
    protected final StoredStructure stored;

    public AbyssOutpostData(StoredStructure stored) {
        this.stored = stored;
    }

    public void invasion(){
        owner = null;
        timeInvaded = System.currentTimeMillis();
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
    }

    public boolean wasInvadedWithin(int ticks){
        return timeInvaded != null && CruxMath.hasOccurredWithin(timeInvaded, ticks);
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
        CruxWorld world = CruxCore.core().worldManager().getWorld(stored.getChunk().worldUUID());
        if(world == null) return;
        WorldEventsModule events = world.getModule(WorldEventsModule.class);
        if(events == null) return;
        if(events.hasApplicableWorldEvents(OutpostInvasionEvent.class, e -> e.getTargetStructure().equals(stored))) return;

        NaturalEntitySpawnGroup spawnGroup = new SimpleNaturalEntitySpawnGroup(
            0, 0f, Set.of(
            new NaturalCruxMobSpawn(10, 0f, AbyssMob.TOXICATOR) {
                @Override
                public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                    return true;
                }
            },
            new NaturalCruxMobSpawn(6, 0f, AbyssMob.SCOURGER) {
                @Override
                public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                    return true;
                }
            },
            new NaturalCruxMobSpawn(3, 0f, AbyssMob.PLAGUEWING_MOUNT_SCOURGER) {
                @Override
                public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                    return true;
                }
            }
        )
        ) {
            @Override
            public boolean canSpawn(@NotNull SpawnContext spawnContext) {
                return true;
            }
        };

        events.addWorldEvent(new OutpostInvasionEvent(
            world, stored, spawnGroup, 3,
            CruxMath.random(3000, 4200) //2.5 min - 3.5 min
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

        if(CruxMath.testChance(0.1)){
            if(!wasInvadedWithin(1200*5)){
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

        /*Player p = Crux.getServer().getPlayer(owner);
        if(p == null) return;*/
        Crux.scheduler().runTask(() ->{
            ValuesProvider cfg = CruxAbyss.inst().values();
            cfg.ABYSS_OUTPOST_TAKE_OVER_EFFECTS().valueOr(Set.of()).forEach(pot ->{
                p.addPotionEffect(pot);
            });
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

        ActiveStructure active = CruxCore.core().worldManager().getWorld(stored.getChunk().worldUUID())
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
