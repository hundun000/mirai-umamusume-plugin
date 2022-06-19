package hundun.mirai.umamusume;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.botlogic.BaseJavaBotLogic;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JavaPluginScheduler;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;

/**
 * @author hundun
 * Created on 2022/06/22
 */
public class UmaBotLogic extends BaseJavaBotLogic {

    public UmaBotLogic(JavaPlugin plugin) {
        super(plugin, "赛马");
        
        
        var function = new UmaFunction(this, plugin, characterName, "赛马");
        function.setSkipRegisterCommand(false);
        registerFunction(function);
    }

    

}
