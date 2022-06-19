package hundun.mirai.umamusume;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;
import hundun.miraifleet.framework.core.function.BaseFunction.AbstractCompositeCommandFunctionComponent;
import hundun.miraifleet.framework.helper.repository.SingletonDocumentRepository;
import hundun.simulationgame.umamusume.display.text.CharImageDisplayer;
import hundun.simulationgame.umamusume.horse.HorsePrototype;
import hundun.simulationgame.umamusume.horse.HorsePrototypeFactory;
import hundun.simulationgame.umamusume.race.Race;
import hundun.simulationgame.umamusume.race.RacePrototype;
import hundun.simulationgame.umamusume.race.RacePrototypeFactory;
import hundun.simulationgame.umamusume.race.TrackWetType;
import lombok.Getter;
import net.mamoe.mirai.console.command.AbstractCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.CompositeCommand;
import net.mamoe.mirai.console.command.descriptor.CommandArgumentContext;
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin;

/**
 * @author hundun
 * Created on 2022/06/22
 */
public class UmaFunction extends BaseFunction<Void> {
    @Getter
    private final CompositeCommandFunctionComponent commandComponent;
    private final SingletonDocumentRepository<UmaConfig> repository;
    
    Map<String, HorsePrototype> nameToPlayerHorseMap = new HashMap<>();
    HorsePrototypeFactory factory;

    
    public UmaFunction(BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName) {
        super(
                baseBotLogic,
                plugin,
                characterName,
                "UmaCommand",
                null
            );
        this.commandComponent = new CompositeCommandFunctionComponent(plugin, characterName, functionName);
        this.repository = new SingletonDocumentRepository<UmaConfig>(
                plugin, 
                resolveConfigRepositoryFile("UmaConfig.json"), 
                UmaConfig.class,
                UmaConfig.defaultValue()
                );
        init();
    }
    
    private void init() {
        factory = new HorsePrototypeFactory();
        UmaConfig config = repository.findSingleton();
        config.getPlayerHorses().forEach(item -> nameToPlayerHorseMap.put(item.getName(), item));
        config.getRivalHorses().forEach(item -> factory.register(item));
        log.info("PlayerHorses: " + nameToPlayerHorseMap.keySet() + ", RivalHorses size = " + config.getRivalHorses().size());
    }

    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }
    
    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent(JvmPlugin plugin, String characterName, String functionName) {
            super(plugin, characterName, functionName, functionName);
        }
        
        @SubCommand("开始比赛")
        public void fakeImage(CommandSender sender, 
                String playerHorseName
                ) {
            HorsePrototype base = nameToPlayerHorseMap.get(playerHorseName);
            if (base == null) {
                sender.sendMessage("playerHorseName = " + playerHorseName + " 未找到");
                return;
            }
            try {
                CharImageDisplayer displayer = new CharImageDisplayer();
                displayer.setPrintOutputBufferWhenFinish(false);
                Race race = new Race(displayer, RacePrototypeFactory.OKA_SHO, TrackWetType.GOOD);
                
                race.addHorse(base, base.getDefaultRunStrategyType());
                List<HorsePrototype> randomRivals = factory.getRandomRivals(3, base, 0.2);
                randomRivals.forEach(item -> {
                    race.addHorse(item, item.getDefaultRunStrategyType());
                });
                race.calculateResult();
                
                List<String> raceResult = displayer.getOutputBuffer();
                ShowRaceResultTask task = new ShowRaceResultTask(sender, raceResult);
                botLogic.getPluginScheduler().repeating(2000L, task);
            } catch (Exception e) {
                sender.sendMessage("比赛过程异常： " + e.getMessage());
                log.error(e);
                return;
            }
        }
    }
    
    private class ShowRaceResultTask extends TimerTask {
        CommandSender commandSender;
        List<String> raceResult;
        
        public ShowRaceResultTask(CommandSender commandSender, List<String> raceResult) {
            super();
            this.commandSender = commandSender;
            this.raceResult = raceResult;
        }

        @Override
        public void run() {
            if (raceResult.size() > 0) {
                commandSender.sendMessage(raceResult.remove(0));
            } else {
                this.cancel();
            }
        }
        
    }
    
}
