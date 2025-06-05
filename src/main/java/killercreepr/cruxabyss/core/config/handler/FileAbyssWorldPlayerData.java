package killercreepr.cruxabyss.core.config.handler;

public class FileAbyssWorldPlayerData /*implements FileObjectHandler<AbyssWorld.PlayerData>*/ {
    /*@Override
    public @NotNull FileElement serializeToFile(@NotNull FileContext<?> ctx, @NotNull AbyssWorld.PlayerData data) {
        FileRegistry reg = ctx.getRegistry();
        return new FileObject()
            .add("claimed_outposts", reg.serializeToFile(data.getClaimedOutposts()))
            ;
    }

    @Nullable
    @Override
    public AbyssWorld.PlayerData deserializeFromFile(@NotNull FileContext<?> ctx, @NotNull FileElement e) {
        if(!(e instanceof FileObject o)) return null;
        FileRegistry reg = ctx.getRegistry();
        Collection<BlockPos> claimedOutposts = reg.deserializeFromFile(
            new TypeToken<Set<BlockPos>>(){}.getType(),
            o.get("claimed_outposts")
        );
        if(claimedOutposts == null || claimedOutposts.isEmpty()) return null;
        var data = new AbyssWorld.PlayerData();
        data.setClaimedOutposts(claimedOutposts);
        return data;
    }*/
}
