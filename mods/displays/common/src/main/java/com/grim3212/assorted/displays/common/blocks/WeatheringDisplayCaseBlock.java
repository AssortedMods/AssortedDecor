package com.grim3212.assorted.displays.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * A copper display case, which oxidises on its own the way any other copper block does. The four
 * waxed ones are plain {@link DisplayCaseBlock}s, a waxed case never changing.
 */
public class WeatheringDisplayCaseBlock extends DisplayCaseBlock implements WeatheringCopper {

    public static final MapCodec<WeatheringDisplayCaseBlock> CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), propertiesCodec())
                    .apply(i, WeatheringDisplayCaseBlock::new));

    private final WeatherState age;

    public WeatheringDisplayCaseBlock(WeatherState age, Properties props) {
        super(props);
        this.age = age;
    }

    @Override
    protected MapCodec<WeatheringDisplayCaseBlock> codec() {
        return CODEC;
    }

    @Override
    public WeatherState getAge() {
        return this.age;
    }

    /**
     * From this block's own stage, not {@link WeatheringCopper#NEXT_BY_BLOCK}: that map is filled
     * from data loaded after the blocks are built, and an empty read there never oxidises silently.
     */
    @Override
    public Optional<BlockState> getNext(BlockState state) {
        if (this.age == WeatherState.OXIDIZED) {
            return Optional.empty();
        }

        return Optional.of(DisplaysBlocks.COPPER_DISPLAY_CASES.weathering().pick(this.age.next()).get().withPropertiesOf(state));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }
}
