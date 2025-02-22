package killercreepr.cruxabyss.core.block.active;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxColor;
import killercreepr.crux.core.util.CruxLoc;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxabyss.api.event.AbyssOutpostCaptureEvent;
import killercreepr.cruxabyss.api.values.ValuesProvider;
import killercreepr.cruxabyss.api.world.module.WorldEventsModule;
import killercreepr.cruxabyss.core.CruxAbyss;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.component.impl.AbyssConquestNode;
import killercreepr.cruxabyss.core.lang.Lang;
import killercreepr.cruxabyss.core.structure.outpost.ActiveAbyssOutpost;
import killercreepr.cruxabyss.core.world.abyss.event.OutpostInvasionEvent;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxInteractable;
import killercreepr.cruxblocks.api.block.active.ActiveCruxTickedBlock;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import killercreepr.cruxblocks.core.block.data.CustomBlockData;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxstructures.api.structure.ActiveStructure;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ActiveAbyssConquestNode extends SimpleActiveCruxBlock implements ActiveCruxTickedBlock, ActiveCruxInteractable {
    protected final int requiredExperience;
    protected int storedExperience;
    protected final int deactivateTime;
    protected final AbyssConquestNode node;
    public ActiveAbyssConquestNode(@NotNull Block block, @NotNull CruxBlock cruxBlock, AbyssConquestNode node) {
        super(block, cruxBlock);
        this.requiredExperience = node.getRequiredExperience().value().intValue();
        this.takeOverTime = node.getTakeOverTime().value().intValue();
        this.deactivateTime = node.getDeactivateTime().value().intValue();
        this.node = node;
    }

    public AbyssConquestNode getNode() {
        return node;
    }

    @Override
    public void stopped() {
        if(!isValid()) return;
        save();
    }

    @Override
    public void started() {
        ActiveCruxTickedBlock.super.started();
        load();
        update();
    }

    public void load(){
        CustomBlockData data = CustomBlockData.wrap(this.getBlock());
        storedExperience = data.get("stored_experience", PersistentDataType.INTEGER, 0);
    }

    public void save(){
        CustomBlockData data = CustomBlockData.wrap(this.getBlock());
        data.set("stored_experience", PersistentDataType.INTEGER, storedExperience);
    }

    public int getRequiredExperience() {
        return requiredExperience;
    }

    public Reference<Player> getUser() {
        return user;
    }

    public long getLastInteract() {
        return lastInteract;
    }

    public int getProgress() {
        return progress;
    }

    public int getTakeOverTime() {
        return takeOverTime;
    }

    public int getVisualTick() {
        return visualTick;
    }

    public long getLastCheckedOutpost() {
        return lastCheckedOutpost;
    }

    protected Reference<Player> user;
    protected long lastInteract;
    protected int progress = 0;
    protected final int takeOverTime;
    protected int experienceGiven;
    protected int expToTakeEachTick;
    protected int cooldown = CruxMath.random(80, 100);
    protected int cooldownTick = 0;

    public CruxBlock getUnpoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            cruxBlock.getGroup().key()
        );
    }

    public CruxBlock getPoweredBlock(){
        return cruxBlock.getGroup().getBlock(
            Key.key(
                cruxBlock.getGroup().key().namespace(), cruxBlock.getGroup().key().value() + "_powered"
            )
        );
    }

    @Override
    public void update() {
        super.update();
        if(!isValid()) return;
        if(outpost() == null) return;
        boolean powered = outpost().getData().owner != null;
        CruxBlock state = powered ? getPoweredBlock() : getUnpoweredBlock();
        if(state.getTextureData().compareTexture(block)) return;
        CustomBlockData data = CustomBlockData.wrap(block);
        PersistentDataContainer pdc = data.getData();
        ActiveCruxBlock active = state.setBlock(BlockContext.context(block, null), true);
        data.setData(pdc);
        if(active instanceof ActiveAbyssConquestNode d){
            d.load();
        }
    }

    public int getMaxTime(Player p, boolean isOutpostOwner){
        return (isDeactivating(p, isOutpostOwner) ? deactivateTime : takeOverTime);
    }

    protected ConquestFireworks visualFireworks;
    protected ConquestMessenger messenger;
    @Override
    public void tick() {
        visualTick();
        if(cooldown > 0){
            cooldownTick++;
            if(cooldownTick >= cooldown){
                cooldownTick = 0;
                cooldown = 0;
            }
            return;
        }
        if(user == null) return;
        if(outpost() == null){
            giveBackExperience();
            reset();
            return;
        }
        if(!CruxMath.hasOccurredWithin(lastInteract, 7)){
            giveBackExperience();
            reset();
            return;
        }
        Player p = user.get();
        if(p == null || !p.isOnline() || !p.isValid()){
            giveBackExperience();
            reset();
            return;
        }
        boolean isOutpostOwner = p.getUniqueId().equals(outpost().getData().owner);
        if(isOutpostOwner && !p.isSneaking()){
            return;
        }
        progress++;
        if(!takeOverLogicTick(p, isOutpostOwner)){
            return;
        }
        if(progress >= getMaxTime(p, isOutpostOwner)){
            reachedMaxProgress(p, isOutpostOwner);
            return;
        }
        takeOverVisualParticlesTick(p, isOutpostOwner);
        takeOverVisualTick(p, isOutpostOwner);
    }

    public boolean isDeactivating(Player p, boolean isOutpostOwner){
        if(outpost().getData().owner != null) return true;
        return false;
    }

    public boolean takeOverLogicTick(Player p, boolean isOutpostOwner){
        if(!isValidInteractor(p)){
            giveBackExperience();
            reset();
            return false;
        }
        if(isOutpostOwner) return true;
        //may want to check if a player has already given the minimum exp amount.
        //but for now, it's preferred to be like this
        int totalExp = p.calculateTotalExperiencePoints();
        int canTake = Math.min(totalExp, expToTakeEachTick);
        p.setExperienceLevelAndProgress(Math.max(0, totalExp - canTake));
        experienceGiven += canTake;
        return true;
    }

    public void reachedMaxProgress(Player p, boolean isOutpostOwner){
        Location center = block.getLocation().toCenterLocation();
        Location spawn = CruxLoc.shiftToward(center, p.getEyeLocation(), .75);
        if(isDeactivating(p, isOutpostOwner)){
            outpost().resetOwner();
            p.setExperienceLevelAndProgress(p.calculateTotalExperiencePoints() + storedExperience);
            storedExperience = 0;
            Lang.ABYSS_CONQUEST_NODE_DEACTIVATE.use(p);
        }else{
            AbyssOutpostCaptureEvent event = new AbyssOutpostCaptureEvent(outpost, p);
            if(!event.callEvent()) return;

            outpost().capture(p);
            storedExperience = experienceGiven;
            Lang.ABYSS_CONQUEST_NODE_TAKE_OVER.use(p);
        }
        cooldown = CruxMath.random(80, 100);
        cooldownTick = 0;
        save();
        reset();
        new ParticleBuilder(Particle.FLASH)
            .location(spawn)
            .spawn()

            .particle(Particle.ELECTRIC_SPARK)
            .count(15)
            .offset(.9, .9, .9)
            .extra(.6)
            .spawn()
        ;
        Crux.scheduler().runTask(task ->{
            update();
            if(isDeactivating(p, isOutpostOwner)){
                block.getWorld().playSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1.4f);
            }else{
                block.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 2f, 1.4f);
            }
            block.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
        });
    }

    public void takeOverVisualParticlesTick(Player p, boolean isOutpostOwner){
        Location spawn = p.getLocation().add(0, p.getHeight()/2, 0);
        Location blockCenter = block.getLocation().toCenterLocation();

        Vector dir = blockCenter.toVector().subtract(spawn.toVector()).multiply(.1);
        if(isOutpostOwner){
            dir = dir.multiply(-1);
            new ParticleBuilder(Particle.TRIAL_SPAWNER_DETECTION)
                .count(0)
                .extra(.8)
                .offset(dir.getX(), dir.getY(), dir.getZ())
                .location(CruxLoc.shiftToward(blockCenter, spawn, .52, 0, 0))
                .spawn()
            ;
        }else{
            new ParticleBuilder(Particle.TRIAL_SPAWNER_DETECTION)
                .count(0)
                .extra(.8)
                .offset(dir.getX(), dir.getY(), dir.getZ())
                .location(p.getLocation().add(0, p.getHeight()/2, 0))
                .spawn()
            ;
        }
    }

    protected int playSoundTick = 0;
    public void takeOverVisualTick(Player p, boolean isOutpostOwner){
        float progressF = ((float)progress/(float)getMaxTime(p, isOutpostOwner));
        String progressColor = CruxColor.colorToHex(CruxColor.hsbToBukkitColor((progressF*100), 80f, 80f));
        Lang.ABYSS_CONQUEST_NODE_TAKING.use(p, TagContainer.merged()
            .add(Tag.parsed("progress", progressF + ""))
            .add(Tag.parsed("progress_color", progressColor))
        );
        Crux.scheduler().runTask(task ->{
            CreateSound s = node.getTakeOverSound();
            if(s != null){
                playSoundTick++;
                if(playSoundTick % 5 == 0){
                    float pitch = isOutpostOwner ? (1f - progressF) : progressF;
                    CreateSound sound = CreateSound.sound(
                        s.getSound().name(), s.getSound().source(), s.getSound().volume(), pitch
                    );
                    sound.playAt(block.getLocation().toCenterLocation());
                    playSoundTick = 0;
                }
            }
            if(isDeactivating(p, isOutpostOwner)){
                visualFireworks.deactivatingTick();
                messenger.deactivatingTick(progressF);
            }else{
                visualFireworks.takingOverTick();
                messenger.takingOverTick(progressF);
            }
        });
    }

    protected int visualTick = 0;
    public void visualTick(){
        if(outpost() == null) return;
        visualTick++;
        if(outpost().getData().owner == null){
            return;
        }
        if(visualTick % 15 != 0) return;
        visualTick = 0;
        new ParticleBuilder(Particle.WAX_ON)
            .offset(.5, .5, .5)
            .extra(.1)
            .count(CruxMath.random(6, 10))
            .location(block.getLocation().toCenterLocation())
            .spawn()
        ;
    }

    public boolean isValidInteractor(Player p){
        if(!p.getWorld().equals(block.getWorld())) return false;
        Location checkLoc = p.getLocation().toCenterLocation();
        checkLoc.setY(p.getY() + p.getHeight()/2D);
        double distance = checkLoc.distanceSquared(block.getLocation().toCenterLocation());
        if(distance > (1.4D*1.4D)){
            Lang.ABYSS_CONQUEST_NODE_TOO_FAR.use(p);
            return false;
        }
        if(outpost() != null && p.getUniqueId().equals(outpost().getData().owner)) return true;
        if(experienceGiven < requiredExperience){
            int cost = requiredExperience - experienceGiven;
            if(p.calculateTotalExperiencePoints() < cost){
                Lang.ABYSS_CONQUEST_NODE_NOT_ENOUGH_EXPERIENCE.use(p, TagContainer.merged().add(Tag.parsed("exp_points", cost + "")));
                return false;
            }
        }
        return true;
    }

    public void reset(){
        user = null;
        lastInteract = 0L;
        progress = 0;
        experienceGiven = 0;
        expToTakeEachTick = 0;
    }

    public void giveBackExperience(){
        if(user == null || experienceGiven < 1) return;
        Player p = user.get();
        if(p == null) return;
        p.setExperienceLevelAndProgress(p.calculateTotalExperiencePoints() + experienceGiven);
        experienceGiven = 0;
    }

    public boolean hasValidUser(){
        if(user == null) return false;
        return user.get() != null;
    }
    private ActiveAbyssOutpost outpost;
    private StoredStructure structure;
    protected long lastCheckedOutpost;
    public ActiveAbyssOutpost outpost(){
        if(outpost == null){
            if(CruxMath.hasOccurredWithin(lastCheckedOutpost, 20)) return outpost;
            StructureWorldModule module = CruxCore.core().worldManager().getWorld(block.getWorld().key())
                .getModule(StructureWorldModule.class);
            ActiveStructure structure = module.getFirstActiveAt(ActiveStructure.class, block,
                check -> check.has(AbyssComponents.ACTIVE_ABYSS_OUTPOST));
            outpost = structure == null ? null : structure.get(AbyssComponents.ACTIVE_ABYSS_OUTPOST);
            this.structure = structure == null ? null : structure.getData();
            lastCheckedOutpost = System.currentTimeMillis();
            visualFireworks = new ConquestFireworks(this, outpost);
            messenger = new ConquestMessenger(this, outpost);
        }
        return outpost;
    }

    public boolean isBeingInvaded(){
        outpost();
        if(structure == null) return false;
        WorldEventsModule module = CruxCore.core().worldManager().getWorld(block.getWorld().key())
            .getModule(WorldEventsModule.class);
        if(module == null) return false;
        return !module.getApplicableWorldEvents(OutpostInvasionEvent.class, e -> e.getTargetStructure().equals(structure)).isEmpty();
    }

    @NotNull
    @Override
    public Event.Result interact(@NotNull PlayerInteractEvent event) {
        if(!event.getAction().isRightClick()) return Event.Result.DENY;
        if(outpost() == null) return Event.Result.DENY;
        Player p = event.getPlayer();
        if(cooldown > 0){
            if(cooldownTick < 15){
                return Event.Result.DENY;
            }
            CreateSound.sound(Sound.BLOCK_CHEST_LOCKED, 1.5f).playFor(p);
            return Event.Result.DENY;
        }

        ValuesProvider cfg = CruxAbyss.inst().values();
        if(outpost().getData().wasInvadedWithin(cfg.ABYSS_OUTPOST_INVADE_CONQUEST_COOLDOWN().value().intValue())){
            long invadeTime = System.currentTimeMillis() - outpost().getData().timeInvaded;
            Lang.ABYSS_CONQUEST_NODE_CANNOT_CAPTURE_FROM_INVADE.use(p, TagContainer.merged(Tag.parsed("invade_time", invadeTime + "")));
            return Event.Result.DENY;
        }
        if(isBeingInvaded()){
            Lang.ABYSS_CONQUEST_NODE_CANNOT_CAPTURE_FROM_ACTIVE_INVADE.use(p);
            return Event.Result.DENY;
        }

        if(!p.isSneaking()){
            if(p.getUniqueId().equals(outpost().getData().owner)){
                CruxCore.core().cruxMenus().menuRegistry().menuHolders().get(Crux.key("abyss/outpost/main"))
                    .open(p, DataExchange.builder()
                        .put(outpost)
                        .build());
                //Lang.ABYSS_CONQUEST_NODE_SHIFT_INFO.use(p);
                return Event.Result.DENY;
            }
        }
        if(hasValidUser()){
            Player user = this.user.get();
            if(user != null){
                if(!p.equals(user) && !user.getUniqueId().equals(outpost().getData().owner)){
                    return Event.Result.DENY;
                }
            }
        }
        if(user == null || !p.equals(user.get())){
            if(!isValidInteractor(p)) return Event.Result.DENY;
            reset();
            outpost();
            if(outpost == null){
                p.sendMessage("Block must be placed in an abyss outpost.");
                return Event.Result.DENY;
            }
            user = new WeakReference<>(p);
            expToTakeEachTick = (int) Math.ceil((float) requiredExperience / (float) getMaxTime(p, p.getUniqueId().equals(outpost().getData().owner)));
        }
        lastInteract = System.currentTimeMillis();
        return Event.Result.DEFAULT;
    }
}
