package hundun.mirai.template;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author hundun
 * Created on 2021/08/09
 */
public class TemplatePlugin extends JavaPlugin {
    public static final TemplatePlugin INSTANCE = new TemplatePlugin(); 

    public TemplatePlugin() {
        super(new JvmPluginDescriptionBuilder(
                "hundun.mirai.template",
                "0.1.0"
            )
            .build());
    }
    
    @Override
    public void onEnable() {
        getLogger().info("TemplatePlugin onEnable");
    }

}
