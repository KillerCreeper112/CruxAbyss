package killercreepr.cruxabyss.api.entity;

import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.cruxabyss.core.entity.SimpleAbyssAltarItemEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface AbyssAltarItemEntity {
    static AbyssAltarItemEntity wrap(@NotNull Entity e){
        return new SimpleAbyssAltarItemEntity(e);
    }

    AbyssAltarItemEntity size(float size);
    AbyssAltarItemEntity spin(boolean value);
    AbyssAltarItemEntity bob(boolean value);
    AbyssAltarItemEntity size(boolean value);
    AbyssAltarItemEntity display(ItemStack item);
    ItemStack display();
    double spinSpeed();
    AbyssAltarItemEntity spinSpeed(double speed);
    CompletableFuture<ActiveModel> model();
    Entity entity();
}
