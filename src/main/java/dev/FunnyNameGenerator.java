package dev;

import java.util.*;

public class FunnyNameGenerator {

    private final List<String> names = List.of(
            "Grumpy Panda", "Sleepy Cactus", "Angry Pineapple",
            "Ninja Squirrel", "Sad Broccoli", "Hyper Banana"
    );
    private final Set<String> used = new HashSet<>();
    private final Random rand = new Random();

    public String next() {
        while (true) {
            String name = names.get(rand.nextInt(names.size()));
            if (used.add(name)) return name;
        }
    }
}

