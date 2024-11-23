package killercreepr.cruxabyss.core.altar;

import killercreepr.cruxabyss.api.altar.AltarEntity;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SimpleAltarEntity implements AltarEntity {
    protected final @NotNull Entity entity;

    public SimpleAltarEntity(@NotNull Entity entity) {
        this.entity = entity;
    }

    @NotNull
    @Override
    public Entity bukkitEntity() {
        return entity;
    }
}
