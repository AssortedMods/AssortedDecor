package com.grim3212.assorted.decor.common.blocks.building;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Registers a {@link StoneFamily}, making only the blocks vanilla lacks. {@code name} prefixes the
 * stone's own blocks, {@code brick} its bricks and tiles, as {@code polished_blackstone_bricks} does.
 */
final class StoneFamilyBuilder {

    private final String name;
    private final String brick;
    private final UnaryOperator<Properties> props;
    private @Nullable Supplier<Block> raw;
    private @Nullable Supplier<Block> polished;
    private @Nullable Supplier<Block> polishedSlab;
    private boolean carvedFromSlabs = true;
    private final Supplier<Block>[] bricks = weathered();
    private @Nullable Supplier<Block> tiles;
    private @Nullable Supplier<Block> carved;

    StoneFamilyBuilder(String name, String brick, UnaryOperator<Properties> props) {
        this.name = name;
        this.brick = brick;
        this.props = props;
    }

    StoneFamilyBuilder raw(Supplier<Block> raw) {
        this.raw = raw;
        return this;
    }

    StoneFamilyBuilder polished(Supplier<Block> polished, Supplier<Block> slab) {
        this.polished = polished;
        this.polishedSlab = slab;
        return this;
    }

    /** Vanilla already turns two of its polished slabs into something else. */
    StoneFamilyBuilder noCarvedFromSlabs() {
        this.carvedFromSlabs = false;
        return this;
    }

    StoneFamilyBuilder bricks(@Nullable Supplier<Block> plain, @Nullable Supplier<Block> mossy, @Nullable Supplier<Block> cracked) {
        this.bricks[0] = plain;
        this.bricks[1] = mossy;
        this.bricks[2] = cracked;
        return this;
    }

    StoneFamilyBuilder tiles(Supplier<Block> tiles) {
        this.tiles = tiles;
        return this;
    }

    StoneFamilyBuilder carved(Supplier<Block> carved) {
        this.carved = carved;
        return this;
    }

    StoneFamily build() {
        Supplier<Block> raw = this.raw != null ? this.raw : BuildingBlocks.cut(this.name, Cuts.STAIRS, this.props);
        Supplier<Block> polished = this.polished;
        Supplier<Block> polishedSlab = this.polishedSlab;
        if (polished == null) {
            IRegistryObject<Block> own = BuildingBlocks.cut("polished_" + this.name, Cuts.STAIRS, this.props);
            polished = own;
            polishedSlab = () -> BuildingBlocks.cuts().get(own).slab().get();
        }
        WeatheredSet bricks = this.weatheredSet(this.brick + "_bricks", this.bricks);
        Supplier<Block> tiles = this.tiles != null ? this.tiles : BuildingBlocks.cut(this.brick + "_tiles", Cuts.WALL, this.props);
        Supplier<Block> carved = this.carved != null ? this.carved : BuildingBlocks.cube("carved_" + this.name, this.props);
        IRegistryObject<Block> fluted = BuildingBlocks.shaped("fluted_" + this.name, p -> new RotatedPillarBlock(this.props.apply(p)));
        IRegistryObject<Block> column = BuildingBlocks.shaped(this.name + "_column", p -> new ColumnBlock(this.props.apply(p)));
        return new StoneFamily(this.name, raw, polished, this.carvedFromSlabs ? polishedSlab : null, bricks, tiles, carved, fluted, column);
    }

    private WeatheredSet weatheredSet(String name, Supplier<Block>[] vanilla) {
        Supplier<Block> plain = vanilla[0] != null ? vanilla[0] : BuildingBlocks.cut(name, Cuts.WALL, this.props);
        Supplier<Block> mossy = vanilla[1] != null ? vanilla[1] : BuildingBlocks.cut("mossy_" + name, Cuts.WALL, this.props);
        Supplier<Block> cracked = vanilla[2] != null ? vanilla[2] : BuildingBlocks.cube("cracked_" + name, this.props);
        return new WeatheredSet(plain, mossy, cracked);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<Block>[] weathered() {
        return new Supplier[3];
    }
}
