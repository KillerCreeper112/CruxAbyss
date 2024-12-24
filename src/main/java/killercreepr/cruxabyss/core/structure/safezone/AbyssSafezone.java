package killercreepr.cruxabyss.core.structure.safezone;

public class AbyssSafezone {
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
