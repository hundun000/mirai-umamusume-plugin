package hundun.mirai.umamusume;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author hundun
 * Created on 2021/08/09
 */
public class UmaPlugin extends JavaPlugin {
    public static final UmaPlugin INSTANCE = new UmaPlugin(); 

    public UmaPlugin() {
        super(new JvmPluginDescriptionBuilder(
                "hundun.mirai.umamusume",
                "0.2.1"
            )
            .build());
    }
    
    @Override
    public void onEnable() {
        var botLogic = new UmaBotLogic(this);
        botLogic.onBotLogicEnable();
    }

}
