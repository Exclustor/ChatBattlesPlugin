package de.levin.chatquizbattles.mathquiz;

import de.levin.chatquizbattles.ChatQuizBattles;
import de.levin.chatquizbattles.language.Translation;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Calculation {

    private Translation translation = new Translation();

    public Number calcString(String calculation) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        Object result = null;
        try {
            if (calculation.contains("√")) {
                String sqrtExpression = calculation.substring(calculation.indexOf("√") + 1);
                Object sqrtResult = engine.eval(sqrtExpression);
                Double sqrtValue;
                if (sqrtResult instanceof Long) {
                    sqrtValue = ((Long) sqrtResult).doubleValue();
                } else {
                    sqrtValue = (Double) sqrtResult;
                }
                result = Math.sqrt(sqrtValue);
                if (result.equals(Math.floor(((Double) result).doubleValue()))) {
                    result = ((Double) result).intValue();
                }
            } else {
                result = engine.eval(calculation);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        if (result instanceof Integer) {
            return (Integer) result;
        } else if (result instanceof Long) {
            return (Long) result;
        } else if (result instanceof Double) {
            return (Double) result;
        } else if (result instanceof Float) {
            return (Float) result;
        }

        return null;
    }

    public Number parseString(String message) {
        try {
            return Integer.parseInt(message);
        } catch (NumberFormatException e) {
            try {
                return Long.parseLong(message);
            } catch (NumberFormatException e1) {
                try {
                    return Float.parseFloat(message);
                } catch (NumberFormatException ex2) {
                    try {
                        return Double.parseDouble(message);
                    } catch (NumberFormatException ex3) {
                        return null;
                    }
                }
            }
        }
    }
    public Integer getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public Integer replaceSecondsMinutes(String configPath){
        String rawDelay = ChatQuizBattles.instance.getConfig().getString(configPath);
        String integerValue = ChatQuizBattles.instance.getConfig().getString(configPath);
        String integer;
        if (integerValue.contains(translation.getTranslationKey())) {
            String[] args = integerValue.split(translation.getTranslationKey());
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].replace("s", "*20").replace("m", "*1200");
            }
            List<String> newArgs = new ArrayList<>();
            for (String arg : args) {
                newArgs.add(calcString(arg).toString().trim());
            }
            Integer zahl1 = Integer.valueOf(newArgs.get(0));
            Integer zahl2 = Integer.valueOf(newArgs.get(1));

            if (zahl2 > zahl1) {
                integer = String.valueOf(getRandomNumber(zahl1, zahl2));
            } else {
                integer = String.valueOf(getRandomNumber(zahl2, zahl1));
            }
        } else {
            integer = rawDelay.replace("s", "*20").replace("m", "*1200");
            integer = calcString(integer).toString();
        }

        return Integer.parseInt(integer);
    }
}
