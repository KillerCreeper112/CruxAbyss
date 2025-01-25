package killercreepr.cruxabyss.core.structure.safezone;

import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.data.world.StoredChunk;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxstructures.api.component.StructureComponent;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.core.structure.component.StructureTickedStoredComponent;
import org.jetbrains.annotations.NotNull;

public class AbyssSafeZone extends StructureTickedStoredComponent implements StructureComponent {
    //todo safe zone
    @Override
    public void onCreated(@NotNull StoredChunk chunk, @NotNull CruxPosition center, double rotation, @NotNull StoredStructure stored) {
        stored.set(AbyssComponents.ABYSS_SAFE_ZONE_DATA, new AbyssSafeZoneData(stored));
    }

    @Override
    public void onFileLoad(@NotNull FileContext<?> context, @NotNull FileObject o, @NotNull StoredStructure structure) {
        FileRegistry reg = context.getRegistry();
        AbyssSafeZoneData outpostData = new AbyssSafeZoneData(structure);
       /* outpostData.owner = reg.deserializeFromFile(UUID.class, o.get("owner"));
        Number time = reg.deserializeFromFile(Number.class, o.get("time_captured"));
        if(time != null){
            outpostData.timeCaptured = time.longValue();
        }
        if(o.get("upgrades") instanceof FileArray a){
            a.forEach(ele ->{
                if(!(ele instanceof FileObject oo)) return;
                Key key = reg.deserializeFromFile(Key.class, oo.get("key"));
                if(key == null) return;
                Number level = reg.deserializeFromFile(Number.class, oo.get("level"));
                if(level == null) return;
                OutpostUpgrade upgrade = AbyssRegistries.OUTPOST_UPGRADE.get(key);
                if(upgrade == null){
                    Crux.log(Level.SEVERE, "OutpostUpgrade of " + key + " not found! Skipping...");
                    return;
                }
                outpostData.upgrades.put(upgrade, level.intValue());
            });
        }*/

        structure.set(AbyssComponents.ABYSS_SAFE_ZONE_DATA, outpostData);
    }
    /*protected final Vector expandBox;
    public AbyssSafezone(@NotNull Key key, @NotNull ClipboardHolder holder, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, holder, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    public AbyssSafezone(@NotNull Key key, @NotNull String filename, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, filename, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    public AbyssSafezone(@NotNull Key key, @NotNull File schematicFile, boolean persistent, @Nullable List<StructureModule> beforePlacementModules, @NotNull List<StructureModule> modules, Vector expandBox) {
        super(key, schematicFile, persistent, beforePlacementModules, modules);
        this.expandBox = expandBox;
    }

    @Override
    public @Nullable StoredStructure buildStored(@NotNull Location center, double rotation) {
        CfgStoredStructure built = (CfgStoredStructure) super.buildStored(center, rotation);
        Objects.requireNonNull(built, "Abyss Safezone built stored is null :/");
        return new StoredAbyssSafezone(
            built.getStructureKey(), built.getChunk(), built.getPosition(), built.getBoundingBox(), built.getRotation(), built.getInnerBox()
        );
        *//*return StoredAbyssSafezone.createNew(
            this, StoredChunk.from(center), CruxPosition.block(center), rotation, expandBox
        );*//*
    }*/
}
