package killercreepr.cruxabyss.lang;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.data.communication.CreateLang;
import killercreepr.crux.data.communication.CreateTitle;
import killercreepr.crux.data.communication.Msg;
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
                "<gray>Hold right click!",
                "     <<progress_color>>{{<progress>*100}[1]}%",
                0, 8, 0
            ))
            //.sound(CreateSound.sound(Sound.BLOCK_NOTE_BLOCK_BANJO))
            .build()
    );

    public static Msg create(@NotNull String id, @NotNull Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@NotNull Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
