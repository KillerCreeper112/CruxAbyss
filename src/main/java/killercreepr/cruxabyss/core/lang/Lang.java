package killercreepr.cruxabyss.core.lang;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
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

    public static Msg create(@NotNull String id, @NotNull Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@NotNull Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
