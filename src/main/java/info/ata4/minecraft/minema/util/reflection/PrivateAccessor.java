package info.ata4.minecraft.minema.util.reflection;

import java.lang.reflect.Field;
import java.util.Optional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper.UnableToFindFieldException;

public final class PrivateAccessor {

    // These classes might not be able to be loaded by the JVM at this point
    // (Mod classes of which the corresponding mod is not yet loaded)
    private static Optional<Field> Shaders_frameTimeCounter;

    private static void lateLoadFrameTimeCounterField() {
        if (Shaders_frameTimeCounter == null) {
            Shaders_frameTimeCounter = Optional.ofNullable(getAccessibleField("net.optifine.shaders.Shaders", "frameTimeCounter"));
        }
    }

    public static float getFrameTimeCounter() {
        lateLoadFrameTimeCounterField();

        if (Shaders_frameTimeCounter.isPresent()) {
            try {
                // this field is static, just using null as the object
                return Shaders_frameTimeCounter.get().getFloat(null);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }

        // just a default
        return 0;
    }

    public static void setFrameTimeCounter(float frameTimerCounter) {
        lateLoadFrameTimeCounterField();

        if (Shaders_frameTimeCounter.isPresent()) {
            try {
                // this field is static, just using null as the object
                Shaders_frameTimeCounter.get().setFloat(null, frameTimerCounter);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
    }

    /*
     * Utility methods
     */

    private static Field getAccessibleField(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                return ObfuscationReflectionHelper.findField(clazz, name);
            } catch (UnableToFindFieldException ignored) {
            }
        }
        return null;
    }

    private static Field getAccessibleField(String clazz, String... names) {
        try {
            return getAccessibleField(Class.forName(clazz), names);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
