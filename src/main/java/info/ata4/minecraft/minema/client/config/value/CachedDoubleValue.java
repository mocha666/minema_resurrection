package info.ata4.minecraft.minema.client.config.value;

import info.ata4.minecraft.minema.client.config.MinemaConfig;
import java.util.function.DoubleSupplier;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class CachedDoubleValue extends CachedPrimitiveValue<Double> implements DoubleSupplier {

    private double cachedValue;

    private CachedDoubleValue(MinemaConfig config, ConfigValue<Double> internal) {
        super(config, internal);
    }

    public static CachedDoubleValue wrap(MinemaConfig config, ConfigValue<Double> internal) {
        return new CachedDoubleValue(config, internal);
    }

    public double get() {
        if (!resolved) {
            //If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
            cachedValue = internal.get();
            resolved = true;
        }
        return cachedValue;
    }

    @Override
    public double getAsDouble() {
        return get();
    }

    public void set(double value) {
        internal.set(value);
        cachedValue = value;
    }
}