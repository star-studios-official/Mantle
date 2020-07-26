package slimeknights.mantle.registration.adapter;

import net.minecraft.util.IStringSerializable;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IRegistryDelegate;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.function.Function;

/**
 * Adapter that allows certain registry types to register from a list of values
 * @param <T>  Registry type
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class EnumRegistryAdapter<T extends ForgeRegistryEntry<T>> extends RegistryAdapter<T> {

  public EnumRegistryAdapter(IForgeRegistry<T> registry) {
    super(registry);
  }

  public EnumRegistryAdapter(IForgeRegistry<T> registry, String modId) {
    super(registry, modId);
  }

  /**
   * Registers an entry with multiple variants, prefixing the name with the value name
   * @param mapper    Function to get a block for the given enum value
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @return  EnumObject mapping between different block types
   */
  @SuppressWarnings("unchecked")
  public <E extends Enum<E> & IStringSerializable,I extends T> EnumObject<E,I> registerEnum(Function<E,I> mapper, E[] values, String name) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    EnumObject.Builder<E,I> builder = new EnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      // assuming the type will not sub for a different class
      IRegistryDelegate<T> delegate = register(mapper.apply(value), value.getString() + "_" + name).delegate;
      builder.put(value, () -> (I) delegate.get());
    }
    return builder.build();
  }

  /**
   * Registers an entry with multiple variants, suffixing the name with the value name
   * @param mapper    Function to get a block for the given enum value
   * @param name      Name of the block
   * @param values    Enum values to use for this block
   * @return  EnumObject mapping between different block types
   */
  @SuppressWarnings("unchecked")
  public <E extends Enum<E> & IStringSerializable,I extends T> EnumObject<E,I> registerEnum(Function<E,I> mapper, String name, E[] values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    EnumObject.Builder<E,I> builder = new EnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      // assuming the type will not sub for a different class
      IRegistryDelegate<T> delegate = register(mapper.apply(value), name + "_" + value.getString()).delegate;
      builder.put(value, () -> (I) delegate.get());
    }
    return builder.build();
  }
}
