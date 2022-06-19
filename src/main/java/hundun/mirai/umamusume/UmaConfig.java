package hundun.mirai.umamusume;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import hundun.simulationgame.umamusume.horse.HorsePrototype;
import hundun.simulationgame.umamusume.horse.HorsePrototypeFactory;
import hundun.simulationgame.umamusume.horse.RunStrategyType;
import hundun.simulationgame.umamusume.race.RaceLengthType;
import hundun.simulationgame.umamusume.race.RacePrototype;
import hundun.simulationgame.umamusume.race.TrackGroundType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hundun
 * Created on 2022/06/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmaConfig {
    List<HorsePrototype> playerHorses;
    List<RacePrototype> races;
    List<HorsePrototype> rivalHorses;
    
    public static Supplier<UmaConfig> defaultValue() {
        return () -> {
            UmaConfig config = new UmaConfig();
            config.setPlayerHorses(new ArrayList<>());
            config.setRivalHorses(new ArrayList<>());
            config.setRaces(new ArrayList<>());
            HorsePrototype horsePrototype;
            
            horsePrototype = new HorsePrototype();
            horsePrototype.setName("特别周001");
            horsePrototype.setBaseSpeed((int) (600* 1.05));
            horsePrototype.setBaseStamina((int) (600* 1.05));
            horsePrototype.setBasePower((int) (600* 1.05));
            horsePrototype.setBaseGuts((int) (600* 1.05));
            horsePrototype.setBaseWisdom((int) (200* 1.05));
            horsePrototype.setDefaultRunStrategyType(RunStrategyType.FRONT);
            HorsePrototypeFactory.fillDefaultFields(horsePrototype);
            horsePrototype.setCharImage("🏇📆");
            config.getPlayerHorses().add(horsePrototype);
            
            NumberFormat formatter = new DecimalFormat("#000");
            for (int i = 1; i <= 5; i++) {
                horsePrototype = new HorsePrototype();
                horsePrototype.setName("无声铃鹿" + formatter.format(i));
                horsePrototype.setBaseSpeed(600 + 20 * i);
                horsePrototype.setBaseStamina(600 - 10 * i);
                horsePrototype.setBasePower(600 - 10 * i);
                horsePrototype.setBaseGuts(600);
                horsePrototype.setBaseWisdom(200);
                horsePrototype.setDefaultRunStrategyType(RunStrategyType.FIRST);
                HorsePrototypeFactory.fillDefaultFields(horsePrototype);
                horsePrototype.setCharImage("🏇🦌");
                config.getRivalHorses().add(horsePrototype);
            }
            for (int i = 1; i <= 5; i++) {
                horsePrototype = new HorsePrototype();
                horsePrototype.setName("草上飞" + formatter.format(i));
                horsePrototype.setBaseSpeed(600 - 20 * i);
                horsePrototype.setBaseStamina(600 + 10 * i);
                horsePrototype.setBasePower(600 + 10 * i);
                horsePrototype.setBaseGuts(600);
                horsePrototype.setBaseWisdom(200);
                horsePrototype.setDefaultRunStrategyType(RunStrategyType.BACK);
                HorsePrototypeFactory.fillDefaultFields(horsePrototype);
                horsePrototype.setCharImage("🏇🌱");
                config.getRivalHorses().add(horsePrototype);
            }
            for (int i = 1; i <= 5; i++) {
                horsePrototype = new HorsePrototype();
                horsePrototype.setName("黄金船" + formatter.format(i));
                horsePrototype.setBaseSpeed(600 - 20 * i);
                horsePrototype.setBaseStamina(600 + 5 * i);
                horsePrototype.setBasePower(600 + 15 * i);
                horsePrototype.setBaseGuts(600);
                horsePrototype.setBaseWisdom(200);
                horsePrototype.setDefaultRunStrategyType(RunStrategyType.TAIL);
                HorsePrototypeFactory.fillDefaultFields(horsePrototype);
                horsePrototype.setCharImage("🏇🚢");
                config.getRivalHorses().add(horsePrototype);
            }
            
            RacePrototype racePrototype;
            
            racePrototype = new RacePrototype();
            racePrototype.setName("短距离训练场");
            racePrototype.setGroundType(TrackGroundType.TURF);
            racePrototype.setLength(1200);
            racePrototype.setLengthType(RaceLengthType.MILE);
            racePrototype.setDefaultHorseNum(4);
            config.getRaces().add(racePrototype);
            
            racePrototype = new RacePrototype();
            racePrototype.setName("英里训练场");
            racePrototype.setGroundType(TrackGroundType.TURF);
            racePrototype.setLength(1600);
            racePrototype.setLengthType(RaceLengthType.MILE);
            racePrototype.setDefaultHorseNum(4);
            config.getRaces().add(racePrototype);
            
            racePrototype = new RacePrototype();
            racePrototype.setName("中距离训练场");
            racePrototype.setGroundType(TrackGroundType.TURF);
            racePrototype.setLength(2000);
            racePrototype.setLengthType(RaceLengthType.MEDIUM);
            racePrototype.setDefaultHorseNum(4);
            config.getRaces().add(racePrototype);
            
            racePrototype = new RacePrototype();
            racePrototype.setName("长距离训练场");
            racePrototype.setGroundType(TrackGroundType.TURF);
            racePrototype.setLength(3000);
            racePrototype.setLengthType(RaceLengthType.LONG);
            racePrototype.setDefaultHorseNum(4);
            config.getRaces().add(racePrototype);
            
            return config;
        };
    }
}
