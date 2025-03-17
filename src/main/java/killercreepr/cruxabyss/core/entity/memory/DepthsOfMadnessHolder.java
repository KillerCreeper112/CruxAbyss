package killercreepr.cruxabyss.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.EntityTickedDataHolder;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DepthsOfMadnessHolder extends EntityTickedDataHolder {
    public static final Key KEY = Crux.key("advancement/depths_of_madness");

    protected final Consumer<Entity> onComplete;

    public DepthsOfMadnessHolder(@NotNull Key key, @NotNull EntityMemory parent, Consumer<Entity> onComplete) {
        super(key, parent);
        this.onComplete = onComplete;
    }
    public DepthsOfMadnessHolder(@NotNull EntityMemory parent, Consumer<Entity> onComplete) {
        this(KEY, parent, onComplete);
    }

    protected boolean remove = false;
    public void markForRemoval(){
        remove = true;
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Entity e) {
        if(remove) return true;
        if(super.shouldRemoveFromMemory(e)) return true;
        if(e == null) return true;
        if(duration % 20 != 0) return false;
        return !isInDepths(e);
    }

    public static boolean isInDepths(Entity e){
        if(e instanceof Player d){
            switch (d.getGameMode()){
                case CREATIVE, SPECTATOR -> { return false; }
            }
        }
        double y = e.getLocation().getY();
        if(y >= -66 && y <= -63){
            return true;
        }
        return false;
    }

    protected int duration = 0;
    @Override
    public void tick(@NotNull Entity entity) {
        duration++;
        if(duration >= 600){
            markForRemoval();
            if(!isInDepths(entity)) return;
            onComplete.accept(entity);
        }
    }
}
