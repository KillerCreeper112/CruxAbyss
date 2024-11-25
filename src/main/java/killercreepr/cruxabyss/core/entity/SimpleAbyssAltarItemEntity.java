package killercreepr.cruxabyss.core.entity;

import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.model.ActiveModel;
import killercreepr.cruxabyss.api.entity.AbyssAltarItemEntity;
import killercreepr.cruxentities.modelengine.wrapper.ModelEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public class SimpleAbyssAltarItemEntity implements AbyssAltarItemEntity {
    protected final Entity entity;
    protected final ModelEntity designEntity;
    public SimpleAbyssAltarItemEntity(Entity entity) {
        this.entity = entity;
        this.designEntity = new ModelEntity(entity);
        designEntity.getOrAddModelAsync("abyss_altar_item");
    }

    @Override
    public AbyssAltarItemEntity spin(boolean value) {
        if(value) designEntity.playAnimation("spin", true);
        else designEntity.stopAnimation("spin");
        return this;
    }

    @Override
    public AbyssAltarItemEntity bob(boolean value) {
        if(value) designEntity.playAnimation("float", true);
        else designEntity.stopAnimation("float");
        return this;
    }

    @Override
    public AbyssAltarItemEntity size(boolean value) {
        if(value) designEntity.playAnimation("size", true);
        else designEntity.stopAnimation("size");
        return this;
    }

    @Override
    public AbyssAltarItemEntity display(ItemStack item) {
        designEntity.applyModel(model ->{
            model.getBone("base").orElseThrow().setModel(item);
        });
        return this;
    }

    @Override
    public AbyssAltarItemEntity spinSpeed(double speed) {
        designEntity.applyModel(model ->{
            IAnimationProperty property = model.getAnimationHandler().getAnimation("spin");
            if(property == null){
                model.getAnimationHandler().playAnimation(
                    "spin", 0D, 0D, speed, true
                );
                return;
            }
            property.setSpeed(speed);
        });
        return this;
    }

    @Override
    public CompletableFuture<ActiveModel> model() {
        return designEntity.model();
    }

    @Override
    public Entity entity() {
        return entity;
    }
}
