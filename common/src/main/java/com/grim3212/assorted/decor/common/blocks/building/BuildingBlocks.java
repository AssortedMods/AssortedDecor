package com.grim3212.assorted.decor.common.blocks.building;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * The More Building Blocks part: bricks, tiles, columns and timber from the 1.2.4 mod, carried over to
 * the stones and woods vanilla has added since.
 */
public final class BuildingBlocks {

    private static final List<IRegistryObject<? extends Block>> ALL = new ArrayList<>();
    private static final Map<IRegistryObject<Block>, CutShapes> CUTS = new LinkedHashMap<>();
    private static final List<IRegistryObject<Block>> CUBES = new ArrayList<>();
    private static final List<StoneFamily> STONES = new ArrayList<>();
    private static final List<WoodSet> WOODS = new ArrayList<>();

    public static final IRegistryObject<Block> GLOWSTONE_BRICKS = cut("glowstone_bricks", Cuts.WALL, p -> p.mapColor(MapColor.SAND).instrument(NoteBlockInstrument.PLING).requiresCorrectToolForDrops().strength(1.5F, 6.0F).sound(SoundType.GLASS).lightLevel(state -> 15));

    public static final IRegistryObject<Block> POLISHED_OBSIDIAN = cut("polished_obsidian", Cuts.STAIRS, p -> p.mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(50.0F, 1200.0F));
    public static final GemBrickSet LAPIS = gem("lapis", p -> p.mapColor(MapColor.LAPIS).requiresCorrectToolForDrops().strength(3.0F, 3.0F));
    public static final GemBrickSet REDSTONE = gem("redstone", p -> p.mapColor(MapColor.FIRE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));

    public static final IRegistryObject<Block> IRON_BRICKS = cut("iron_bricks", Cuts.STAIRS, p -> p.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON));
    public static final IRegistryObject<Block> GOLD_BRICKS = cut("gold_bricks", Cuts.STAIRS, p -> p.mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BELL).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.METAL));
    public static final IRegistryObject<Block> DIAMOND_BRICKS = cut("diamond_bricks", Cuts.STAIRS, p -> p.mapColor(MapColor.DIAMOND).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));

    public static final IRegistryObject<Block> BASKETWEAVE_BRICKS = cut("basketweave_bricks", Cuts.WALL, BuildingBlocks::bricks);
    public static final IRegistryObject<Block> HERRINGBONE_BRICKS = cut("herringbone_bricks", Cuts.WALL, BuildingBlocks::bricks);

    public static final IRegistryObject<Block> REINFORCED_COBBLESTONE = cube("reinforced_cobblestone", p -> stone(p, MapColor.STONE).strength(2.0F, 6.0F));
    public static final IRegistryObject<Block> FRAMED_COBBLESTONE = cube("framed_cobblestone", p -> stone(p, MapColor.STONE).strength(2.0F, 6.0F));
    public static final IRegistryObject<Block> CHECKERED_STONE = cut("checkered_stone", Cuts.STAIRS, p -> stone(p, MapColor.STONE));

    public static final StoneFamily STONE = stone(new StoneFamilyBuilder("stone", "stone", p -> stone(p, MapColor.STONE))
            .raw(() -> Blocks.STONE).polished(() -> Blocks.SMOOTH_STONE, () -> Blocks.SMOOTH_STONE_SLAB)
            .bricks(() -> Blocks.STONE_BRICKS, () -> Blocks.MOSSY_STONE_BRICKS, () -> Blocks.CRACKED_STONE_BRICKS));
    public static final StoneFamily GRANITE = stone(new StoneFamilyBuilder("granite", "granite", p -> stone(p, MapColor.DIRT))
            .raw(() -> Blocks.GRANITE).polished(() -> Blocks.POLISHED_GRANITE, () -> Blocks.POLISHED_GRANITE_SLAB));
    public static final StoneFamily DIORITE = stone(new StoneFamilyBuilder("diorite", "diorite", p -> stone(p, MapColor.QUARTZ))
            .raw(() -> Blocks.DIORITE).polished(() -> Blocks.POLISHED_DIORITE, () -> Blocks.POLISHED_DIORITE_SLAB));
    public static final StoneFamily ANDESITE = stone(new StoneFamilyBuilder("andesite", "andesite", p -> stone(p, MapColor.STONE))
            .raw(() -> Blocks.ANDESITE).polished(() -> Blocks.POLISHED_ANDESITE, () -> Blocks.POLISHED_ANDESITE_SLAB));
    public static final StoneFamily CALCITE = stone(new StoneFamilyBuilder("calcite", "calcite", p -> stone(p, MapColor.TERRACOTTA_WHITE).strength(0.75F).sound(SoundType.CALCITE))
            .raw(() -> Blocks.CALCITE));
    public static final StoneFamily TUFF = stone(new StoneFamilyBuilder("tuff", "tuff", p -> stone(p, MapColor.TERRACOTTA_GRAY).sound(SoundType.TUFF_BRICKS))
            .raw(() -> Blocks.TUFF).polished(() -> Blocks.POLISHED_TUFF, () -> Blocks.POLISHED_TUFF_SLAB)
            .bricks(() -> Blocks.TUFF_BRICKS, null, null));
    public static final StoneFamily DRIPSTONE = stone(new StoneFamilyBuilder("dripstone", "dripstone", p -> stone(p, MapColor.TERRACOTTA_BROWN).strength(1.5F, 1.0F).sound(SoundType.DRIPSTONE_BLOCK))
            .raw(() -> Blocks.DRIPSTONE_BLOCK));
    public static final StoneFamily DEEPSLATE = stone(new StoneFamilyBuilder("deepslate", "deepslate", p -> stone(p, MapColor.DEEPSLATE).strength(3.5F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS))
            .raw(() -> Blocks.COBBLED_DEEPSLATE).polished(() -> Blocks.POLISHED_DEEPSLATE, () -> Blocks.POLISHED_DEEPSLATE_SLAB)
            .bricks(() -> Blocks.DEEPSLATE_BRICKS, null, () -> Blocks.CRACKED_DEEPSLATE_BRICKS)
            .tiles(() -> Blocks.DEEPSLATE_TILES));
    public static final StoneFamily BLACKSTONE = stone(new StoneFamilyBuilder("blackstone", "polished_blackstone", p -> stone(p, MapColor.COLOR_BLACK))
            .raw(() -> Blocks.BLACKSTONE).polished(() -> Blocks.POLISHED_BLACKSTONE, () -> Blocks.POLISHED_BLACKSTONE_SLAB).noCarvedFromSlabs()
            .bricks(() -> Blocks.POLISHED_BLACKSTONE_BRICKS, null, () -> Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS));
    public static final StoneFamily SANDSTONE = stone(new StoneFamilyBuilder("sandstone", "sandstone", BuildingBlocks::sandstone)
            .raw(() -> Blocks.SANDSTONE).polished(() -> Blocks.SMOOTH_SANDSTONE, () -> Blocks.SMOOTH_SANDSTONE_SLAB).carved(() -> Blocks.CHISELED_SANDSTONE));
    public static final StoneFamily RED_SANDSTONE = stone(new StoneFamilyBuilder("red_sandstone", "red_sandstone", p -> sandstone(p).mapColor(MapColor.COLOR_ORANGE))
            .raw(() -> Blocks.RED_SANDSTONE).polished(() -> Blocks.SMOOTH_RED_SANDSTONE, () -> Blocks.SMOOTH_RED_SANDSTONE_SLAB).carved(() -> Blocks.CHISELED_RED_SANDSTONE));

    public static final WoodSet OAK = wood("oak", () -> Blocks.OAK_PLANKS, () -> Blocks.OAK_SLAB, MapColor.WOOD, true);
    public static final WoodSet SPRUCE = wood("spruce", () -> Blocks.SPRUCE_PLANKS, () -> Blocks.SPRUCE_SLAB, MapColor.PODZOL, true);
    public static final WoodSet BIRCH = wood("birch", () -> Blocks.BIRCH_PLANKS, () -> Blocks.BIRCH_SLAB, MapColor.SAND, true);
    public static final WoodSet JUNGLE = wood("jungle", () -> Blocks.JUNGLE_PLANKS, () -> Blocks.JUNGLE_SLAB, MapColor.DIRT, true);
    public static final WoodSet ACACIA = wood("acacia", () -> Blocks.ACACIA_PLANKS, () -> Blocks.ACACIA_SLAB, MapColor.COLOR_ORANGE, true);
    public static final WoodSet DARK_OAK = wood("dark_oak", () -> Blocks.DARK_OAK_PLANKS, () -> Blocks.DARK_OAK_SLAB, MapColor.COLOR_BROWN, true);
    public static final WoodSet MANGROVE = wood("mangrove", () -> Blocks.MANGROVE_PLANKS, () -> Blocks.MANGROVE_SLAB, MapColor.COLOR_RED, true);
    public static final WoodSet CHERRY = wood("cherry", () -> Blocks.CHERRY_PLANKS, () -> Blocks.CHERRY_SLAB, MapColor.TERRACOTTA_WHITE, true);
    public static final WoodSet PALE_OAK = wood("pale_oak", () -> Blocks.PALE_OAK_PLANKS, () -> Blocks.PALE_OAK_SLAB, MapColor.QUARTZ, true);
    public static final WoodSet BAMBOO = wood("bamboo", () -> Blocks.BAMBOO_PLANKS, () -> Blocks.BAMBOO_SLAB, MapColor.COLOR_YELLOW, true);
    public static final WoodSet CRIMSON = wood("crimson", () -> Blocks.CRIMSON_PLANKS, () -> Blocks.CRIMSON_SLAB, MapColor.CRIMSON_STEM, false);
    public static final WoodSet WARPED = wood("warped", () -> Blocks.WARPED_PLANKS, () -> Blocks.WARPED_SLAB, MapColor.WARPED_STEM, false);

    public static final IRegistryObject<Block> IRON_BEAM = shaped("iron_beam", p -> new BeamBlock(p.mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.IRON)));

    public static final IRegistryObject<Block> MEAT_BLOCK = shaped("meat_block", p -> new HorizontalAxisBlock(Shapes.or(Block.box(0, 0, 1, 16, 16, 15), Block.box(6, 6, 0, 10, 10, 16)),
            p.mapColor(MapColor.COLOR_PINK).strength(0.7F).sound(SoundType.MUD)));

    private BuildingBlocks() {
    }

    /** Every block of the part in creative tab order, each cut shape after the block it is cut from. */
    public static List<IRegistryObject<? extends Block>> all() {
        return Collections.unmodifiableList(ALL);
    }

    /** The plain full cubes, each drawn from {@code block/building/<id>} on all six sides. */
    public static List<IRegistryObject<Block>> cubes() {
        return Collections.unmodifiableList(CUBES);
    }

    /** Full block to the shapes cut from it. */
    public static Map<IRegistryObject<Block>, CutShapes> cuts() {
        return Collections.unmodifiableMap(CUTS);
    }

    public static List<StoneFamily> stones() {
        return Collections.unmodifiableList(STONES);
    }

    public static List<WoodSet> woods() {
        return Collections.unmodifiableList(WOODS);
    }

    /** The blocks, each followed by whatever is cut from it. Vanilla's own are left out. */
    @SafeVarargs
    public static List<Block> withCuts(Supplier<? extends Block>... blocks) {
        List<Block> all = new ArrayList<>();
        for (Supplier<? extends Block> block : blocks) {
            if (!(block instanceof IRegistryObject<?>)) {
                continue;
            }
            all.add(block.get());
            CutShapes cuts = CUTS.get(block);
            if (cuts != null) {
                all.add(cuts.slab().get());
                all.add(cuts.stairs().get());
                if (cuts.wall() != null) {
                    all.add(cuts.wall().get());
                }
            }
        }
        return all;
    }

    /** Every block of ours in a stone family. */
    public static List<Block> blocksOf(StoneFamily family) {
        WeatheredSet bricks = family.bricks();
        return withCuts(family.raw(), family.polished(), bricks.plain(), bricks.mossy(), bricks.cracked(), family.tiles(), family.carved(), family.fluted(), family.column());
    }

    /** Every block of ours made from one wood. */
    public static List<Block> blocksOf(WoodSet wood) {
        List<Block> all = new ArrayList<>(withCuts(wood.parquet(), wood.framed()));
        all.add(wood.panel().get());
        all.add(wood.beam().get());
        return all;
    }

    /** The overworld wood blocks, which fire spreads to and burns away with vanilla's odds for planks. */
    public static List<Supplier<? extends Block>> flammables() {
        List<Supplier<? extends Block>> all = new ArrayList<>();
        for (WoodSet wood : WOODS) {
            if (wood.flammable()) {
                all.addAll(suppliersOf(wood));
            }
        }
        return all;
    }

    /** Burn times in ticks, as vanilla's planks, slabs and stairs; a panel is half a plank, as two framed planks make four. */
    public static Map<Supplier<? extends Block>, Integer> fuels() {
        Map<Supplier<? extends Block>, Integer> fuels = new LinkedHashMap<>();
        for (WoodSet wood : WOODS) {
            if (!wood.flammable()) {
                continue;
            }
            // A plank's 300 ticks shared out over what the lumber mill cuts from one.
            for (Supplier<? extends Block> block : suppliersOf(wood)) {
                int ticks = block == wood.panel() ? 37 : block == wood.beam() ? 75 : block.get() instanceof SlabBlock ? 150 : 300;
                fuels.put(block, ticks);
            }
        }
        return fuels;
    }

    private static List<Supplier<? extends Block>> suppliersOf(WoodSet wood) {
        List<Supplier<? extends Block>> all = new ArrayList<>();
        for (IRegistryObject<Block> block : List.of(wood.parquet(), wood.framed())) {
            all.add(block);
            CutShapes cuts = CUTS.get(block);
            all.add(cuts.slab());
            all.add(cuts.stairs());
        }
        all.add(wood.panel());
        all.add(wood.beam());
        return all;
    }

    /** Vanilla's naming: {@code obsidian_bricks} gives {@code obsidian_brick_slab}. */
    public static String cutName(String block, String shape) {
        return block.replaceFirst("_bricks$", "_brick").replaceFirst("_tiles$", "_tile").replaceFirst("_planks$", "_plank") + "_" + shape;
    }

    private static GemBrickSet gem(String material, UnaryOperator<Properties> props) {
        return new GemBrickSet(cut(material + "_bricks", Cuts.WALL, props), cut(material + "_tiles", Cuts.WALL, props), cut("polished_" + material, Cuts.STAIRS, props));
    }

    private static StoneFamily stone(StoneFamilyBuilder builder) {
        StoneFamily family = builder.build();
        STONES.add(family);
        return family;
    }

    private static WoodSet wood(String name, Supplier<Block> planks, Supplier<Block> slab, MapColor color, boolean flammable) {
        UnaryOperator<Properties> props = p -> {
            p.mapColor(color).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(name.equals("bamboo") ? SoundType.BAMBOO_WOOD : !flammable ? SoundType.NETHER_WOOD : name.equals("cherry") ? SoundType.CHERRY_WOOD : SoundType.WOOD);
            return flammable ? p.ignitedByLava() : p;
        };
        WoodSet wood = new WoodSet(name, planks, slab, flammable,
                cut(name + "_parquet", Cuts.STAIRS, props),
                cut("framed_" + name + "_planks", Cuts.STAIRS, props),
                shaped(name + "_panel", p -> new PanelBlock(props.apply(p))),
                shaped(name + "_beam", p -> new BeamBlock(props.apply(p))));
        WOODS.add(wood);
        return wood;
    }

    static IRegistryObject<Block> cube(String name, UnaryOperator<Properties> props) {
        IRegistryObject<Block> block = DecorBlocks.register(name, p -> new Block(props.apply(p)));
        ALL.add(block);
        CUBES.add(block);
        return block;
    }

    static IRegistryObject<Block> cut(String name, Cuts cuts, UnaryOperator<Properties> props) {
        IRegistryObject<Block> block = cube(name, props);
        IRegistryObject<SlabBlock> slab = shaped(cutName(name, "slab"), p -> new SlabBlock(props.apply(p)));
        IRegistryObject<StairBlock> stairs = shaped(cutName(name, "stairs"), p -> new BuildingStairBlock(block.get().defaultBlockState(), props.apply(p)));
        IRegistryObject<WallBlock> wall = cuts == Cuts.WALL ? shaped(cutName(name, "wall"), p -> new WallBlock(props.apply(p).forceSolidOn())) : null;
        CUTS.put(block, new CutShapes(slab, stairs, wall));
        return block;
    }

    static <T extends Block> IRegistryObject<T> shaped(String name, Function<Properties, T> factory) {
        IRegistryObject<T> block = DecorBlocks.register(name, factory);
        ALL.add(block);
        return block;
    }

    private static Properties stone(Properties p, MapColor color) {
        return p.mapColor(color).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1.5F, 6.0F);
    }

    private static Properties bricks(Properties p) {
        return p.mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F);
    }

    private static Properties sandstone(Properties p) {
        return p.mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(0.8F);
    }

    public static void init() {
    }
}
