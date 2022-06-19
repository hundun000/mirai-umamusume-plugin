package hundun.mirai.umamusume;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.stream.Collectors;

import hundun.miraifleet.framework.core.botlogic.BaseBotLogic;
import hundun.miraifleet.framework.core.function.BaseFunction;
import hundun.miraifleet.framework.core.function.FunctionReplyReceiver;
import hundun.miraifleet.framework.core.function.BaseFunction.AbstractCompositeCommandFunctionComponent;
import hundun.miraifleet.framework.helper.repository.SingletonDocumentRepository;
import hundun.simulationgame.umamusume.horse.HorsePrototype;
import hundun.simulationgame.umamusume.horse.HorsePrototypeFactory;
import hundun.simulationgame.umamusume.race.RacePrototype;
import hundun.simulationgame.umamusume.race.RacePrototypeFactory;
import hundun.simulationgame.umamusume.race.RaceSituation;
import hundun.simulationgame.umamusume.race.TrackWetType;
import hundun.simulationgame.umamusume.record.BotTextCharImageRender;
import hundun.simulationgame.umamusume.record.CharImageRecorder;
import hundun.simulationgame.umamusume.record.RecordPackage;
import hundun.simulationgame.umamusume.record.RecordPackage.RecordNode;
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
    HorsePrototypeFactory horsePrototypeFactory;
    RacePrototypeFactory racePrototypeFactory;
    
    public UmaFunction(BaseBotLogic baseBotLogic,
            JvmPlugin plugin,
            String characterName,
            String forceCommandName) {
        super(
                baseBotLogic,
                plugin,
                characterName,
                "UmaCommand",
                null
            );
        this.commandComponent = new CompositeCommandFunctionComponent(plugin, characterName, functionName, forceCommandName);
        this.repository = new SingletonDocumentRepository<UmaConfig>(
                plugin, 
                resolveConfigRepositoryFile("UmaConfig.json"), 
                UmaConfig.class,
                UmaConfig.defaultValue()
                );
        init();
    }
    
    private void init() {
        horsePrototypeFactory = new HorsePrototypeFactory();
        racePrototypeFactory = new RacePrototypeFactory();
        UmaConfig config = repository.findSingleton();
        config.getPlayerHorses().forEach(item -> nameToPlayerHorseMap.put(item.getName(), item));
        config.getRivalHorses().forEach(item -> horsePrototypeFactory.register(item));
        config.getRaces().forEach(item -> racePrototypeFactory.register(item));
        log.info("PlayerHorses: " + nameToPlayerHorseMap.keySet() + ", RivalHorses size = " + config.getRivalHorses().size());
    }

    @Override
    public AbstractCommand provideCommand() {
        return commandComponent;
    }
    
    public class CompositeCommandFunctionComponent extends AbstractCompositeCommandFunctionComponent {
        public CompositeCommandFunctionComponent(JvmPlugin plugin, String characterName, String functionName, String forceCommandName) {
            super(plugin, characterName, functionName, forceCommandName);
        }
        
        
        @SubCommand("随机比赛")
        public void randomRace(CommandSender sender, 
                String playerHorseName
                ) {
            if (!checkCosPermission(sender)) {
                return;
            }
            HorsePrototype base = nameToPlayerHorseMap.get(playerHorseName);
            if (base == null) {
                sender.sendMessage("playerHorseName = " + playerHorseName + " 未找到");
                return;
            }
            RacePrototype racePrototype = racePrototypeFactory.getRandom();
            if (racePrototype == null) {
                sender.sendMessage("随机比赛 未找到");
                return;
            }
            generalRace(sender, racePrototype, base);
        }
        
        
        
        @SubCommand("指定比赛")
        public void specifiedRace(CommandSender sender, 
                String raceName,
                String playerHorseName
                ) {
            if (!checkCosPermission(sender)) {
                return;
            }
            HorsePrototype base = nameToPlayerHorseMap.get(playerHorseName);
            if (base == null) {
                sender.sendMessage("playerHorseName = " + playerHorseName + " 未找到");
                return;
            }
            RacePrototype racePrototype = racePrototypeFactory.get(raceName);
            if (racePrototype == null) {
                sender.sendMessage("raceName = " + raceName + " 未找到");
                return;
            }
            generalRace(sender, racePrototype, base);
        }
    }
    
    
    private void generalRace(CommandSender sender, 
            RacePrototype racePrototype,
            HorsePrototype base) {
        
        
        
        try {
            CharImageRecorder recorder = new CharImageRecorder();
            RaceSituation race = new RaceSituation(recorder, racePrototype, TrackWetType.GOOD);
            
            race.addHorse(base, base.getDefaultRunStrategyType());
            List<HorsePrototype> randomRivals = horsePrototypeFactory.getRandomRivals(racePrototype.getDefaultHorseNum() - 1, base, 0.2);
            randomRivals.forEach(item -> {
                race.addHorse(item, item.getDefaultRunStrategyType());
            });
            race.calculateResult();
            
            RecordPackage<String> raceResult = recorder.getRecordPackage();
            ShowRaceResultTask task = new ShowRaceResultTask(sender, raceResult);
            botLogic.getPluginScheduler().repeating(ShowRaceResultTask.TASK_DELAY, task);
        } catch (Exception e) {
            sender.sendMessage("比赛过程异常： " + e.getMessage());
            log.error(e);
            return;
        }
    }
    
    private class ShowRaceResultTask extends TimerTask {
        private static final double GAME_SECOND_TO_REAL_SECOND = 15.0 / 80.0;
        private static final int REAL_SECOND_TO_TASK_COUNT = 2;
        public static final long TASK_DELAY = 1000 / REAL_SECOND_TO_TASK_COUNT;
        
        CommandSender commandSender;
        int count = 0;
        Map<Integer, String> countToShowTextMap = new HashMap<>();
        
        public ShowRaceResultTask(CommandSender commandSender, RecordPackage<String> recordPackage) {
            super();
            this.commandSender = commandSender;
            init(recordPackage);
        }
        
        private void init(RecordPackage<String> recordPackage) {
            int currentCount = 0;
            if (recordPackage.getNodes().size() < 2) {
                throw new UnsupportedOperationException("recordPackage.size不应少于2");
            }
            countToShowTextMap.put(0, recordPackage.getNodes().get(0).getContent());
            for (int i = 0; i < recordPackage.getNodes().size() - 1; i++) {
                RecordNode<String> first = recordPackage.getNodes().get(i);
                RecordNode<String> second = recordPackage.getNodes().get(i + 1);
                int deltaTick = second.getTick() - first.getTick();
                double deltaGameSecond = RaceSituation.tickCountToSecond(deltaTick);
                double deltaRealSecond = deltaGameSecond * GAME_SECOND_TO_REAL_SECOND;
                int deltaCount = 
                        Math.min(
                                Math.max(
                                        (int)(deltaRealSecond * REAL_SECOND_TO_TASK_COUNT),
                                        1
                                ),
                                2 * REAL_SECOND_TO_TASK_COUNT
                        );
                currentCount += deltaCount;
                countToShowTextMap.put(currentCount, second.getContent());
            }
            //log.info("countToShowTextMap = " + countToShowTextMap);
            
        }
        
        

        @Override
        public void run() {
            if (countToShowTextMap.containsKey(count)) {
                commandSender.sendMessage(countToShowTextMap.remove(count));
            }
            if (countToShowTextMap.size() == 0) {
                this.cancel();
            } else {
                count++;
            }
        }
        
    }
    
}
