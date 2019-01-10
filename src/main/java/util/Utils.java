package util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;


public class Utils {
    public static final Random RANDOM = new Random();

    public static <T> T pickRandomElementFromList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        return list.get(RANDOM.nextInt(list.size()));
    }

    public static <T> T filterAndPickRandomElementFromList(List<T> list, Predicate<T> filteringCondition) {
        if (CollectionUtils.isEmpty(list)) return null;

        List<T> filteredList = new ArrayList<>();
        for (T el : list) {
            if (filteringCondition.test(el)) filteredList.add(el);
        }

        return pickRandomElementFromList(filteredList);
    }

}
