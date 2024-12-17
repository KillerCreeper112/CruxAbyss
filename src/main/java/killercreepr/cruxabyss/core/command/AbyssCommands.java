package killercreepr.cruxabyss.core.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxteleport.api.teleport.world.RandomWorldTP;
import killercreepr.usurvive.USurvivePlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AbyssCommands {
    protected final @NotNull CruxPlugin plugin;

    public AbyssCommands(@NotNull CruxPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(){
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->{
            final Commands commands = event.registrar();
            LiteralCommandNode<CommandSourceStack> cmd = build(Commands.literal("cruxabyss")
                .requires(source -> source.getSender().hasPermission("cruxabyss.cmds.cruxabyss.use")),
                plugin.getLifecycleManager());
            commands.register(cmd, List.of("cabyss"));
        });
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                                  LifecycleEventManager<?> manager){
        //cruxclaim view (player)
        dispatcher.then(
            Commands.literal("world")
                .then(
                    Commands.literal("create")
                        .executes(ctx ->{
                            getExecutor(ctx.getSource()).sendMessage("Creating or getting existing world_abyss world...");
                            AbyssWorld.getOrCreate(plugin, "world_abyss");
                            return 1;
                        })
                        .then(
                            Commands.literal("overwrite")
                                .executes(ctx ->{
                                    CommandSender sender = getExecutor(ctx.getSource());
                                    String lobbyName = USurvivePlugin.inst().values().SPAWN_WORLD().valueOr("world");
                                    World world = Crux.getServer().getWorld(lobbyName);
                                    if(world == null){
                                        sender.sendMessage("No lobby world found from uSurvive.");
                                        return 0;
                                    }
                                    World active = Crux.getServer().getWorld("world_abyss");
                                    Location spawn = world.getSpawnLocation();

                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            if(active != null){
                                                if(plugin.getServer().isTickingWorlds()){
                                                    sender.sendMessage("Server is ticking worlds, waiting...");
                                                    return;
                                                }
                                                active.getPlayers().forEach(p -> p.teleport(spawn));
                                                sender.sendMessage("Deleting existing world...");
                                                //Crux.getServer().unloadWorld(active, false);
                                            }
                                            cancel();
                                            CruxCore.inst().worldManager().deleteWorld("world_abyss");
                                            //CruxWorldUtil.deleteWorld("world_abyss");
                                            sender.sendMessage("Creating new world...");
                                            AbyssWorld.getOrCreate(plugin, "world_abyss");
                                            sender.sendMessage("Abyss world created!");
                                        }
                                    }.runTaskTimer(plugin, 0L, 1L);
                                    return 1;
                                })
                        )
                ).then(
                    Commands.literal("tp")
                        .then(
                            Commands.argument("targets", ArgumentTypes.entities())
                                .executes(ctx ->{
                                    CommandSender sender = getExecutor(ctx.getSource());
                                    Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());

                                    AbyssWorld world = AbyssWorld.getOrCreate(plugin, "world_abyss");
                                    Location l = world.toBukkitWorld().getSpawnLocation();
                                    targets.forEach(e -> e.teleportAsync(l));

                                    sender.sendMessage("Teleported " + targets.size() + " to world_abyss.");
                                    return 1;
                                })
                        )
                ).then(
                    Commands.literal("rtp")
                        .then(
                            Commands.argument("targets", ArgumentTypes.entities())
                                .executes(ctx ->{
                                    CommandSender sender = getExecutor(ctx.getSource());
                                    Collection<Entity> targets = ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                        .resolve(ctx.getSource());

                                    AbyssWorld world = AbyssWorld.getOrCreate(plugin, "world_abyss");
                                    RandomWorldTP tp = RandomWorldTP.tp(world.toBukkitWorld());
                                    targets.forEach(tp::randomlyTeleportAsync);

                                    sender.sendMessage("Randomly teleported " + targets.size() + " to world_abyss.");
                                    return 1;
                                })
                        )
                )
        )
        ;
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source) {
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

}
