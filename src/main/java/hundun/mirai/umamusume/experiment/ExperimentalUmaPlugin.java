package hundun.mirai.umamusume.experiment;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author hundun
 * Created on 2021/08/09
 */
public class ExperimentalUmaPlugin extends JavaPlugin {
    public static final ExperimentalUmaPlugin INSTANCE = new ExperimentalUmaPlugin(); 

    public ExperimentalUmaPlugin() {
        super(new JvmPluginDescriptionBuilder(
                "hundun.mirai.experiment-umamusume",
                "0.1.0"
            )
            .build());
    }
    
    @Override
    public void onEnable() {
        var botLogic = new ExperimentalUmaBotLogic(this);
        botLogic.onBotLogicEnable();
    }

}
