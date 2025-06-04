package killercreepr.cruxabyss.core.lang;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.communication.boss.CreateBossBar;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.core.communication.lang.Msg;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public class Lang {
    private static CreateLang lang;
    public static CreateLang lang(){
        return lang;
    }
    public static CreateLang setLang(CreateLang l){
        lang = l;
        return l;
    }
    public static final Msg ABYSS_CONQUEST_NODE_DEACTIVATE_START = create(
        Communicator.builder()
            .chat("<#F0D941>You are deactivating <active_abyss_outpost_data/owner_name>'s outpost.")
            //.sound(CreateSound.sound(Sound.BLOCK_NOTE_BLOCK_BANJO))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_TAKING = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "<white><latinfont:Hold right click>!",
                "<progress_bar:-:10:<aqua>:<gray>:<progress>:1>",
                0, 8, 0
            ))
            //.sound(CreateSound.sound(Sound.BLOCK_NOTE_BLOCK_BANJO))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_TAKE_OVER = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "",
                "",
                0, 2, 0
            ))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_DEACTIVATE = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "",
                "",
                0, 2, 0
            ))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_SHIFT_INFO = create(
        Communicator.builder()
            .chat("<white>Want to take back your experience and forfeit ownership of the outpost? Hold shift right click.")
            .sound(CreateSound.sound(Sound.BLOCK_COPPER_BREAK, 1.9f))
            .build()
    );


    public static final Msg ABYSS_CONQUEST_NODE_NOT_ENOUGH_EXPERIENCE = create(
        Communicator.builder()
            .chat("<red>You are not powerful enough! You need at least {{<exp_points>}[0]} experience points!")
            .sound(CreateSound.sound(Sound.BLOCK_COPPER_BREAK, 1.5f))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_TOO_FAR = create(
        Communicator.builder()
            .chat("<red>You are too far away to interact with this.")
            .sound(CreateSound.sound(Sound.BLOCK_COPPER_BREAK, 1.5f))
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_PLAYER_CAPTURING = create(
        Communicator.builder()
            .chat("<#F08A4F><node_user_name> is taking over an outpost at <block_x>, <block_z>!")
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_PLAYER_DEACTIVATING = create(
        Communicator.builder()
            .chat("<#F0D941><node_user__name> is deactivating an outpost at <block_x>, <block_z>!")
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_CANNOT_CAPTURE_FROM_INVADE = create(
        Communicator.builder()
            .chat("<red>This outpost was invaded <duration:<invade_time>> ago and cannot be interacted with right now.")
            .build()
    );

    public static final Msg ABYSS_CONQUEST_NODE_CANNOT_CAPTURE_FROM_ACTIVE_INVADE = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>This outpost is being invaded!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_START_OWNER = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>One of your abyss outposts is being invaded at <white><crux_pos_x></white>, <white><crux_pos_z></white>! Hurry and defend it before it is overtaken!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_START_MEMBER = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>An abyss outpost that you are a member of is being invaded at <white><crux_pos_x></white>, <white><crux_pos_z></white>!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_OVERTOOK = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>Your abyss outpost at <white><crux_pos_x></white>, <white><crux_pos_z></white> has been overtaken by the toxicators!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_DEFEATED = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <green>The invasion at <white><crux_pos_x></white>, <white><crux_pos_z></white> has been defeated! Good job!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_DEFEATED_OVERTIME = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <yellow>The invasion at <white><crux_pos_x></white>, <white><crux_pos_z></white> has failed to take over the outpost!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_TICK = create(
        Communicator.builder()
            .bossBar(CreateBossBar.bossBar(
                "abyss_outpost_invasion",
                "Mobs alive: <spawned_entities>",
                "{{1 - (<capture_time> / <max_capture_time>)}}",
                "red",
                "notched_12",
                "40",
                null
            ))
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_WAVE_SPAWNING = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <reset>Reinforcements incoming! <wave>/<max_wave>")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_REACHED_50 = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>The outpost at <white><crux_pos_x></white>, <white><crux_pos_z></white> has lost 50% of its health! Hurry before they capture the outpost!")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_INVASION_REACHED_75 = create(
        Communicator.builder()
            .chat("<gold><latinfont:Outpost invasion> <red>The outpost at <white><crux_pos_x></white>, <white><crux_pos_z></white> has lost 75% of its health! Hurry before they capture the outpost!")
            .build()
    );

    public static final Msg PLAGUE_WING_GLIDER_FALL_DISTANCE_INSUFFICIENT = create(
        Communicator.builder()
            .chat("<red>You must be falling to deploy this item.")
            .build()
    );

    public static final Msg PLAGUE_WING_GLIDER_BLOCK_DISTANCE_INSUFFICIENT = create(
        Communicator.builder()
            .chat("<red>You must be falling from a greater height to deploy this item.")
            .build()
    );

    public static final Msg PLAGUE_WING_DURABILITY_WARNING = create(
        Communicator.builder()
            .chat("<red>Your plague wing glider is about to break! <durability> durability...")
            .actionBar("<red><latinfont:\"<durability> durability...\">")
            .sound(CreateSound.sound(Sound.ITEM_WOLF_ARMOR_CRACK, 1.5f))
            .build()
    );

    public static final Msg PLAGUE_WING_DURABILITY_HIT = create(
        Communicator.builder()
            .actionBar("<red><latinfont:\"<durability> durability...\">")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_UPGRADE_RECALL_CANNOT_TELEPORT = create(
        Communicator.builder()
            .chat("<red>Teleportation failed.")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_UPGRADE_RECALL_CAN_BE_RECALL_ANCHOR = create(
        Communicator.builder()
            .chat("<yellow>This respawn anchor may be turned into a <gold>recall anchor</gold>. Insert at least one glowstone and then right click on it to convert it into a recall anchor.")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_UPGRADE_RECALL_REACHED_MAX = create(
        Communicator.builder()
            .chat("<red>You may not have any more recall anchors connected at once.")
            .build()
    );

    public static final Msg ABYSS_OUTPOST_UPGRADE_RECALL_REMOVED = create(
        Communicator.builder()
            .chat("<red>Disconnected abyss recall anchor.")
            .sound(CreateSound.sound(Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2f))
            .build()
    );

    public static final Msg ABYSS_OUTPOST_UPGRADE_RECALL_ADDED = create(
        Communicator.builder()
            .chat("<yellow>Abyss recall anchor has been linked to the outpost!")
            .sound(CreateSound.sound(Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN))
            .build()
    );

    public static final Msg ABYSS_REQUIRE_OUTPOST_PLACE_BLOCK = create(
        Communicator.builder()
            .chat("<red>This block may only be placed in an abyss outpost.")
            .build()
    );

    public static final Msg ABYSS_REQUIRE_OUTPOST_PLACE_BLOCK_FRIENDLY = create(
        Communicator.builder()
            .chat("<red>This block may only be placed in an abyss outpost that you own or are a member of.")
            .build()
    );

    public static final Msg CANNOT_MINE_ANCIENT_DEBRIS = create(
        Communicator.builder()
            .chat("<red>WARNING! Ancient debris can no longer be mined with diamond! You must travel to the abyss and upgrade your tools to .")
            .build()
    );


    public static Msg create(@NotNull String id, @NotNull Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@NotNull Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
