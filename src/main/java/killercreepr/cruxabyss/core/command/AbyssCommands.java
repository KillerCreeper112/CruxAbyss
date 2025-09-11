package killercreepr.cruxabyss.core.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.plugin.CruxPlugin;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.cruxabyss.core.challenge.AbyssChallengeManager;
import killercreepr.cruxabyss.core.component.AbyssComponents;
import killercreepr.cruxabyss.core.world.abyss.AbyssWorld;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxquestline.api.quest.QuestCategory;
import killercreepr.cruxquestline.api.quest.QuestLineHolder;
import killercreepr.cruxquestline.core.command.argument.QuestLineArgs;
import killercreepr.cruxquestline.core.quest.participant.DummyParticipantData;
import killercreepr.cruxstructures.api.structure.StoredStructure;
import killercreepr.cruxstructures.api.world.module.StructureWorldModule;
import killercreepr.cruxstructures.core.structure.component.StoredStructureComponents;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.cruxteleport.api.teleport.world.RandomWorldTP;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
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
            Commands.literal("challenges")
                .then(
                    Commands.literal("roll")
                        .executes(ctx ->{
                            var sender = getExecutor(ctx.getSource());
                            if(AbyssChallengeManager.getMain() == null){
                                sender.sendMessage("No abyss challenge manager.");
                                return -1;
                            }
                            AbyssChallengeManager.getMain().roll();
                            sender.sendMessage("Scheduled challenges");
                            return 1;
                        })
                )
        ).then(
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
                                            CruxCore.inst().worldManager().deleteWorld(Key.key("world_abyss"));
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
                                    CruxTeleport tp = CruxTeleport.teleport(RandomWorldTP.worldRandom(world.toBukkitWorld()));
                                    targets.forEach(target ->{
                                        CruxTeleporter.teleporter().scheduleTeleport(target, tp);
                                    });
                                    //RandomWorldTP tp = RandomWorldTP.tp(world.toBukkitWorld());
                                    //targets.forEach(tp::randomlyTeleportAsync);

                                    sender.sendMessage("Randomly teleported " + targets.size() + " to world_abyss.");
                                    return 1;
                                })
                        )
                )
        ).then(
            Commands.literal("cquestcomplete")
                .then(
                    Commands.argument("targets", ArgumentTypes.players())
                        .then(
                            Commands.argument("key", ArgumentTypes.key())
                                .then(
                                    Commands.argument("category", QuestLineArgs.QUEST_CATEGORY)
                                        .then(
                                            Commands.argument("streak", IntegerArgumentType.integer())
                                                .then(
                                                    Commands.argument("questsCompletedToday", IntegerArgumentType.integer())
                                                        .executes(ctx ->{
                                                            var sender = getExecutor(ctx.getSource());
                                                            var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                                .resolve(ctx.getSource());
                                                            Key key = ctx.getArgument("key", Key.class);
                                                            var cat = ctx.getArgument("category", QuestCategory.class);
                                                            int streak = ctx.getArgument("streak", Integer.class);
                                                            int questsCompletedToday = ctx.getArgument("questsCompletedToday", Integer.class);
                                                            for(var p : targets){
                                                                CruxWorld crux = CruxCore.core().worldManager().getWorld(p.getWorld().key());
                                                                if(crux==null) continue;
                                                                StructureWorldModule module = crux.getModule(StructureWorldModule.class);
                                                                if(module==null) continue;

                                                                Vector vec = p.getLocation().toVector();

                                                                StoredStructure stored = CruxCollection.getFirst(module.getStored(
                                                                    StoredStructure.class, check ->{
                                                                        if(!check.has(AbyssComponents.ABYSS_OUTPOST_DATA)) return false;
                                                                        BoundingBox box = check.getOrDefault(StoredStructureComponents.OUTER_BOX, check.getBoundingBox());
                                                                        return box.contains(vec);
                                                                    }
                                                                ));
                                                                if(stored == null) continue;
                                                                var data = stored.get(AbyssComponents.ABYSS_OUTPOST_DATA);

                                                                QuestLineHolder holder = data.questLineHolder;
                                                                var progress = holder.getQuestLineProgress(key);
                                                                if(progress == null) continue;
                                                                progress.setQuestCategory(cat);
                                                                progress.setStreak(streak);
                                                                progress.setParticipant(p.getUniqueId(), new DummyParticipantData());
                                                                progress.setQuestsCompletedToday(questsCompletedToday);
                                                                progress.onQuestCompleted();

                                                                sender.sendMessage("Completed quest for " + p.getName() + " with parameters: " + "category=" + cat.key() + ", streak=" + streak + ", questsCompletedToday=" + questsCompletedToday);
                                                            }
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
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
