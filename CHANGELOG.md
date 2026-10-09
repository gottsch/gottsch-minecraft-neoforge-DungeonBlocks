# Changelog for DungeonBlocks (Neoforge 1.21.1)

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - Unreleased

### ⚠️ Breaking changes

- **Corner pieces on Ledge, Facade, Fluted Facade, Quarter Facade, Cornice, and Crown Molding blocks change meaning.** Their corner shape is now defined *relative to the direction the block faces*, the way vanilla stairs do it, instead of by absolute compass direction. This is what makes east- and west-facing corners work at all — previously only north–south runs rendered and collided correctly. There is no upgrade path: **corner pieces already placed in existing worlds will visibly change orientation** and need to be re-placed. Straight (non-corner) pieces are unaffected.
- **The Brazier's `lit` and `soul` blockstate properties are replaced by a single `fire` property**, with the values `none`, `embers`, `soul` and `lit`. Braziers already placed in existing worlds go back to `none` (unlit) on load, and any datapack or structure that set `lit`/`soul` on a Brazier needs updating.
- **Requires GottschCore 2.7.0 or later** (was 2.6.0). 2.7.0 fixes blocks built on its waterlogged facing-half block coming out dry when placed into water; the Angle and Corner Plate Brackets relied on a local workaround for it, which is now removed.

### Added

- **Decorative entity props** — a new category of content. These are entities rather than blocks, so they sit anywhere in a block, react to being walked into, and are not limited to the block grid.
- **Terracotta Pots**, in three shapes. They fall under gravity, tip over when something walks into them, and shatter into a spray of ceramic debris on a fast enough collision or a fall of more than two blocks. Placing one against a wall spawns it already lying on its side. All three shapes share the name "Terracotta Pot" — they are a visual variation of one prop, not three things to tell apart by name.
- **Big Potion** and **Flask**, in red, yellow, blue and green — breakable glass potion props in two bottle shapes. They behave exactly like a pot (fall, tip over, shatter on a hard hit or a fall), stand about half the height of one, and throw glass shards instead of ceramic ones. Breaking one releases a lingering effect cloud rather than loot, with vanilla lingering-potion timing and a colour taken from whatever it carries. All four colours share one model and one set of dimensions per shape — colour is a reskin, not a different prop.
- A potion's effects are a per-instance property, set the same way a pot's loot is: an individual potion carries a vanilla `potion_contents` component under its `Effects` tag — the same form a 1.21 potion item uses, for example `{Effects:{potion_contents:{potion:"minecraft:poison"}}}` or `{Effects:{potion_contents:{custom_effects:[{id:"minecraft:slowness",duration:200}]}}}` — so a structure can make one potion a trap and leave another inert. **A potion with nothing set releases nothing**, and the bottle's colour is unrelated to what it does; a red potion is not implicitly healing.
- **Stone Pots**, **Red Pots**, and **Blue Pots** — the same three shapes in a cool grey stone palette, a brick red one, and a slate blue one. Each shatters into debris of its own colour: a pot's material is fixed by its type and travels to the shards it throws.
- Shattering a pot can drop loot. Each pot type has its own loot table (empty by default, and overridable by a datapack), and an individual pot can additionally carry a `LootTable` NBT tag — so a structure can put specific loot in a specific pot without affecting any other.
- **Banners** — a two-block-tall wall hanging, narrow like a vanilla banner but tapering to a point instead of ending square. The cloth waves gently and continuously, rippling from the rod down to the hem, and neighbouring banners are offset from each other so a row of them does not move as one sheet. A banner hangs from its rod, so only its top block needs a wall behind it — one can drape over a doorway or into a stairwell — and breaking either half, or the single wall block it hangs on, takes the whole thing down for one item. You walk through them, as you would expect of cloth. They are **not** vanilla banners: no patterns, no dyes, no loom and nothing to stack — each one carries one fixed design.
- **Eighteen banners, in six factions**, each in three conditions. **Dungeon** is crimson under a bone cross, and runs plain, **Grimy** and **Tattered**. The other five run plain, **Tattered** and **Bloodstained**: the **Orc War Banner**, daubed on filthy sackcloth with a slit-pupil eye and claw marks; the **Undead Banner**, near-black grave linen carrying a bone skull over a spine; the **Dwarven Banner**, brass mountain and delving-strata on indigo; the **Cult Banner**, a sickly green sigil on deep violet; and the **Plague Banner**, a beak-masked plague doctor on pale sailcloth — the one banner in the set light enough to carry across a dark room. The wear is not cosmetic labelling: tattered banners have genuinely ragged hems and holes torn clean through the cloth that you can see the wall through — though never through the crest itself, so a banner always stays identifiable. All eighteen share one model, one block entity type and one renderer; the faction and the condition live entirely in the texture. The artwork is placeholder for now.
- **Pennants** — a one-block version of the banner, in all six factions: **Dungeon**, **Orc War**, **Undead**, **Dwarven**, **Cult** and **Plague**. Pristine only for now. Same cloth cut short (14px of drop rather than 30), so it hangs entirely inside its own block and needs only the one wall behind it — handy over a door frame, along a corridor, or anywhere a full banner would reach the floor. Each crest is laid out again for the shorter cloth rather than shrunk, and the secondary motifs the tall banners carry (the undead spine, the dwarven strata, the orc claw marks, the cult tally) are dropped so the primary emblem still reads. Pennants wave and can be stilled exactly like the tall banners. Both hang against their wall: the rod rests on the stone and the cloth falls just in front of it.
- **A banner's motion can be switched off, per banner** — right-click one with an empty hand and its cloth goes still; right-click again and it resumes. This is a real `animated` blockstate, so it survives saving and can be set by a structure or datapack. `animateBanners`, in the new **visuals** section of the client config, sits over the top of that as a master switch: turning it off stills every banner on your own client, but it will never animate a banner somebody deliberately stilled. Still banners keep a slight bow so they read as cloth rather than as a painted board, and both switches take effect immediately — no reload or chunk rebuild.
- **Swinging Chain** — a decorative chain that sways gently on its own and swings properly when something walks through it, settling over a couple of seconds. Longer chains swing more slowly. Stack them like vanilla chains; each segment is placed and broken individually, and breaking a link drops everything hanging below it.
- A **Lantern**, **Soul Lantern**, or **Dungeon Lantern** can be attached to the bottom of a swinging chain by right-clicking it with one. The lantern swings with the chain and lights the area; right-clicking with an empty hand takes it back off. A Dungeon Lantern attached this way is lit and extinguished with a torch or flint and steel, exactly like a placed one.
- **DungeonBlocks Entities** creative tab, holding the decorative entity props. They no longer appear in the main DungeonBlocks tab.
- **Tomes** — books to lay on a table, a shelf or the floor, in the DungeonBlocks Entities tab: **Old Binder**, **Crimson Magic Book**, **Golden Skull Tome**, **Occult Bible** and **Ominous Manuscript**, and the **Tall Leather Tome**. A tome lies exactly where you place it on a block's top, so several fit along one shelf. Right-click one to open it: open, it turns a page now and then, and enchanting glyphs rise from it and sink back in. Use a tome on a closed one to pile it on top; sneak while placing to stand it upright, spine out, as on a bookshelf. Hitting one picks it up. The Tall Leather Tome is taller than it is wide, so it is the one that looks right standing on a shelf.
- **Scrolls** — ten designs to leave on a table, a shelf or the floor, in the DungeonBlocks Entities tab: **Fire**, **Haunted**, **Health**, **Hearts**, **Orb**, **Plain**, **Rain**, **Skull**, **Star** and **Wind Scroll**. A scroll lies rolled up, tied with a ribbon and sealed with red wax, exactly where you place it. Right-click one and it unrolls toward you, the roll trundling across the table as the sheet opens behind it, to show its design; right-click again to roll it up. Open, a rune scroll now and then gives off an enchanting glyph (the Plain Scroll, only writing, does not). Use a scroll on a rolled one to pile it on top. Hitting one picks it up.
- Tall (3-block and 4-block) variants of the Spruce, Crimson, Dark Oak, and Mangrove Dungeon Doors. Placed and broken as a single unit like a vanilla door, generalized to any number of segments; the interior of the door reuses a single middle model/texture regardless of height, so a 3-tall and a 4-tall door of the same wood share their assets. Spruce and Dark Oak tile their own top texture down the middle; Mangrove and Crimson have a middle texture of their own, so their frame runs the full height of the door instead of repeating its arch or trim every block, and their handle sits once, on the bottom segment. The 4-tall Dark Oak door has its proper base and handle.
- **Angle Cobweb**, in two variants — a purely decorative, walk-through web spanning the corner between two perpendicular surfaces (wall-to-ceiling or wall-to-floor) rather than sitting flush on one, tapering away from the junction it gathers into. No collision and none of a vanilla cobweb's movement slowdown.
- **Mossy Chiseled Stone Bricks** — a full block of chiseled stone bricks overgrown with moss, which vanilla has no version of. Crafted from Chiseled Stone Bricks and a Vine.
- **Rubble** and **Mossy Rubble** — plain full blocks at cobblestone-grade properties, for filling in where a decorative pattern would be wasted.
- **Square Mud Brick** — the Square Stone Brick pattern in vanilla mud brick colours. One brick filling one block, like its stone counterpart.
- **Stairs, Facade and Quarter Facade** for both Square Stone Brick and Square Mud Brick.
- **Square Stone Brick Slab** and **Mossy Square Stone Brick Slab**.
- **Left and Right Large Mud Brick** — the two halves of a single large brick spanning two blocks, in the same mud brick colours.
- **Mossy Square Mud Brick** and **Mossy Left / Right Large Mud Brick** — mossy variants of all three, carrying the same moss the stone and clay bricks use.
- **Polished Andesite Bricks** and **Polished Andesite Brick Stairs** — a brick pattern in vanilla polished andesite's grey, at polished andesite's own properties. Andesite is the mod's usual trim stone, and until now there was no bricked form of it to run a course or a step in.
- **Mossy Polished Andesite Bricks** and **Mossy Polished Andesite Brick Stairs** — mossy variants of both. Vanilla has no mossy polished andesite to copy properties from, so like Mossy Bricks and Mossy Large Bricks they take theirs from Mossy Stone Bricks.
- **Square Deepslate Brick**, with **Stairs**, a **Slab**, a **Facade** and a **Quarter Facade** — the Square Stone Brick pattern in vanilla deepslate brick colours, one brick filling one block. The dark counterpart to the stone and mud sets, for building down where stone brick reads too bright.
- **Left and Right Large Deepslate Brick** — the two halves of a single large brick spanning two blocks, in the same deepslate colours.
- **Mossy Deepslate Bricks**, **Mossy Deepslate Tiles** and **Mossy Cobbled Deepslate** — three full material families, so each comes with the complete decorative set (facade, quarter facade, fluted, fluted facade, sill, double sill, cornice, crown molding, pillar, pillar base, arrow slit) on top of the full block. Vanilla ships no mossy deepslate in any form; the moss is vanilla's own clump pattern and vanilla's own green, stamped onto the deepslate palette rather than darkened to match it.
- **Mossy Deepslate Brick Stairs**.
- **Mossy Polished Basalt** — a rotated pillar like vanilla polished basalt, so it keeps a distinct top and side. Moss is deliberately kept off the dark vertical striations, which are what make basalt read as basalt rather than as generic grey stone.
- **Mossy Tuff** — a full block of tuff with vanilla's mossy cobblestone moss, kept off tuff's pale ash flecks so it still reads as tuff. Same properties as vanilla tuff.
- **Iron Bars Door** — a two-block cell door built from vanilla's own iron bars, so it sits in a wall of iron bars as a working cell front. Unlike the vanilla iron door it opens by hand, and redstone still opens it too. Villagers will not walk through it and zombies cannot break it down, so a cell holds whatever is kept in it. Crafted from an Iron Door and Iron Bars.
- **Dark Iron Bars** and **Dark Iron Bars Door** — iron bars and the cell door in the mod's dark iron, to match its dark iron grates and heavy trapdoors. The bars connect like vanilla iron bars, to each other and to ordinary iron bars. Eight iron bars around a coal make eight; the door is an Iron Door and Dark Iron Bars. **Tarnished Dark Iron Bars** and **Tarnished Dark Iron Bars Door** carry a light staining of rust; creative-only for now, like the tarnished grates.
- **Sharpened Logs**, in every vanilla wood (Oak, Spruce, Birch, Jungle, Acacia, Dark Oak, Mangrove and Cherry Logs, Crimson and Warped Stems, and Bamboo) — the sharpened point of a stake. Set one on the end of a log to finish a palisade wall, a spiked barricade or a stockade gate. It points away from whatever face it is placed against, so it caps a sideways or hanging log as well as an upright one. The cut faces show the wood's stripped grain. Crafted from the stripped log (or stem, or bamboo block) and a Flint.
- **Capstones** — a pyramid point in stone, to cap a pillar, a tower, a gatepost or an obelisk. One for every overworld bricks block, vanilla's and the mod's own (square, large, mossy, cracked, cobblestone and gravel bricks included, chiseled excepted), plus Polished Blackstone, Polished Andesite and all six sandstones: 34 in all. Like Sharpened Logs they point away from the face they are placed on, so they can hang from a ceiling too. Made in a stonecutter, one for one.
- **Iron Spikes** and **Dark Iron Spikes** — a trap bed of spikes on an iron plate, for floors, walls or ceilings. Anything walking through them is slowed and hurt, and a fall onto them hurts more than a fall onto stone. Their own death message: "was impaled on spikes".
- **Cheval-de-Frise**, in every vanilla wood — a log beam with sharpened stakes crossed through it; a row of them is the classic X-section barricade. It lies across the way you face when placing it, and pricks anything that pushes against it. Made from a Sharpened Log either side of a log of the same wood.
- **Walkway Bracket**, in every vanilla wood — a timber knee brace for the inside of a palisade or wall, to carry a walkway: place it against the wall and lay planks on the block above. Four from three stripped logs laid out in an L.
- **Portcullis** and **Portcullis Winch** — a lattice gate that really rises. Build the gate from Portcullis blocks (the bottom row grows spiked tips on its own), leave an empty slot above it for it to rise into, and place a Winch anywhere directly above the gate, up to 24 blocks up - on the floor of a room over the gatehouse, say. Press a button beside the winch, or right-click the winch itself, and the gate winds up one row at a time until it meets the top of the slot; do it again and the gate comes back down. The winch is a toggle: a lever reverses the gate each time it is switched on. It stops short of anything standing under it rather than crushing it, and never overwrites a block.
- **Stone Sarcophagus** and **Deepslate Sarcophagus** — a two-block tomb with a carved effigy lying on its lid. Right-click and the lid grinds smoothly aside to show the hollow tomb inside; right-click again to slide it closed. Placed like a bed, the effigy's head away from you.
- **A sealed sarcophagus can hold loot or a guardian.** The first time it is opened, a single roll decides whether loot spills out over the lid, a guardian mob rises from it and goes for whoever opened it, or nothing happens. It is never both, and it only happens once. Only sarcophagi a structure or command has set up do this; one you place yourself holds nothing, so it can't be farmed. It is set with block data on either half: `LootTable` (any loot table, rolled as chest loot, with an optional `LootTableSeed`), `Guardian` (an entity id such as `minecraft:zombie`), and the weights of the roll, `LootWeight` and `GuardianWeight` (default 1 each) and `EmptyWeight` (default 0). For example `/data merge block <x> <y> <z> {LootTable:"minecraft:chests/simple_dungeon",Guardian:"minecraft:skeleton",LootWeight:3,GuardianWeight:2,EmptyWeight:5}` gives 30% loot, 20% a skeleton and 50% nothing.
- **Iron Maiden** — two blocks tall, with a hooded head. Right-click to swing its doors open on the spikes inside, and again to shut them. A prop: it hurts nothing.
- **Gibbet** — a full-width iron cage with a full-size skeleton in it, hung on a chain: three blocks tall in all, for a gallows, a dungeon cell or a crossroads. Place it against the underside of a ceiling, or of a chain, and it hangs from there — the top of the cage where you clicked, the rest below, its chain meeting whatever holds it up — so it can hang high in a tall hall or at the end of a long chain. Placed against a floor or a wall it stands, and grows upward instead.
- **Pillory** and **Occupied Pillory** — a dark oak pillory two blocks tall: a post each side, a crossbeam, and a board with a neck hole and two wrist holes. Right-click it to lift the top of the board open, and again to drop it shut. The occupied one has a skeleton locked in it, arms out through the wrist holes and its skull hanging forward over the board. Placed facing you. Pillory: two Dark Oak Logs each side under a Dark Oak Slab; Occupied Pillory: a Pillory and a Bone Block.
- **Torture Rack** and **Occupied Torture Rack** — a dark oak rack three blocks long, placed like a bed (it runs away from you from where you place it), with a roller at each end and a crank on the head roller. Right-click it to turn the crank a notch: the ropes draw in, and on the occupied rack — a full-size skeleton lying face up, arms stretched over its head — its arms and legs are pulled toward the rollers until, at full tension, they come away from the body. A third click lets it go slack. Torture Rack: String between two Dark Oak Logs, over a row of Dark Oak Planks on two Dark Oak Fences; Occupied Torture Rack: a Torture Rack and a Bone Block.
- **Dark Iron Ladder** — a ladder of blackened iron: two rails standing off the wall on riveted brackets, with round-stock rungs between them. It is real 3D ironwork rather than a flat picture of one, but it takes exactly a vanilla ladder's space and climbs exactly like one, and its rungs keep a vanilla ladder's spacing so a run of them lines up block to block. Needs a pickaxe. Crafted like a ladder from six Iron Ingots around a Coal, making eight.
- **Firewood Rack** — a dark iron log rack in the Brazier's ironwork: two hoops on little feet, their posts flaring outward at the top like the brazier's. It is placed empty. Right-click it with any log or planks to stack another pair of firewood logs, four times over until it is full, with the cut ends facing the front. The wood cannot be taken back out, and breaking the rack drops only the rack. For a guardroom hearth, a kitchen or a camp. It does not catch fire. Four Iron Bars around three logs of any wood a campfire will burn.
- **Weapon Rack** — a dark iron rack that holds two weapons. Right-click it with a sword or an axe to rack it, and with an empty hand to take one down into your hand; it uses the slot nearest where you click. Swords hang point-down by their guards and axes stand head-up. What you rack is drawn as the item itself, so an enchanted sword keeps its glint, and anything that counts as a sword or an axe can go on it. Breaking the rack drops what it holds. Three Iron Bars over two Iron Ingots.
- **Coffins**, in Spruce, Dark Oak, Crimson and Mangrove — a two-block wooden coffin, the tapered kind, wide at the shoulders and narrow at head and foot, with an iron cross on the lid. Right-click and the lid swings up on its hinge to show the red lining inside; right-click again to close it. Placed like the sarcophagus, the head away from you. **A coffin can be sealed exactly as a sarcophagus can**, with the same block data (`LootTable`, `Guardian` and the three weights), so its first opening spills loot, raises a guardian, or does nothing - and one you place yourself holds nothing. Three of the wood's slabs over its planks, with a Bone in the middle.
- **Catacomb Niches**, in Stone Brick, Mossy Stone Brick, Cracked Stone Brick, Deepslate Brick, Cracked Deepslate Brick, Tuff and Sandstone — a block of wall with a burial recess cut into its face, and remains left inside: a skull and a couple of femurs, a lone skull, a stack of long bones laid knuckles-out as in the Paris catacombs, two skulls on a shelf of bones, or nothing at all. A niche you place picks one for its spot, so a row of them varies on its own; right-click one with an empty hand to move on to the next. Build them into a wall of the same stone, in rows, for a catacomb or an ossuary. It is still a wall everywhere but the opening, and light gets into the recess. Its stone with a Bone.
- **Skull Pike** — a skull impaled on a spruce pole two blocks tall, the iron point driven out through the crown: the warning outside an orc camp, a bandit road or a necromancer's door. Placed looking at you. A Bone Block on two Sticks.
- **Zombie Head Pike** and **Bloody Steve Head Pike** — the same pike with a zombie's head on it, or a freshly severed Steve's, blood soaked up from the neck and running from where the point came out. The zombie head is vanilla's own texture, so it follows your resource pack. Rotten Flesh on two Sticks; Leather and Red Dye on two Sticks.
- **Bone Pile** — a heap of bones and skulls on the floor. It is placed as a few scattered femurs; right-click it with a Bone to heap it higher, three times over, until it is a mound with two skulls in it. The bones cannot be taken back out, and breaking the pile drops only the pile. You wade through it rather than walking on top. Four Bones.
- **Chandelier** — a dark iron wheel of eight candles, hung on a chain from the ceiling, or from a chain above. Light it with a torch or flint and steel and every candle burns, giving full light; put it out with an empty hand. Water puts it out too. A Chain over three Candles over three Iron Ingots.
- **Gargoyles** — the gargoyle from gottsch's Monster Manual, its own model carved in stone, three ways. The **Perched Gargoyle** crouches on whatever it is set on, wings raised behind it: put it on a wall's top, a pillar or the edge of a roof. The **Gargoyle Statue** stands two blocks tall on a plinth with its wings spread wide - they overhang a block's width either side, so give it room. The **Gargoyle Bust** is its head and shoulders on a pedestal. Each faces you when placed. The perched gargoyle and the bust are carved from Stone in a stonecutter; the statue is two Chiseled Stone Bricks on a Smooth Stone. Monster Manual is not needed.
- **The secret passage** — three blocks that make the oldest trick in the dungeon:
  - **Hidden Doors**, in Stone Brick, Mossy Stone Brick, Cracked Stone Brick, Brick, Deepslate Brick and Mud Brick — a door in the texture of a wall. Shut and set in a wall of the same stone, it is two more blocks of wall: its face lies flush with the wall's. It opens to redstone only, like an iron door, so clicking along a wall gives nothing away, and villagers and zombies treat it as an iron door. Five of the stone around a Redstone Dust, in a door's shape.
  - **Lever Sconce** — a torch sconce that is secretly a lever, identical to the ordinary one until it is pulled, when the torch tips out from the wall. It powers the wall it hangs on, as a lever does, so hung on the wall right beside a Hidden Door it opens the door. A Torch Sconce and a Lever.
  - **Pedestal** — a stone pedestal that shows one thing on its top, hovering and slowly turning: the prize at the end of the passage, a relic, a trophy. Right-click with an item to set it there, with an empty hand to take it. A comparator reads 15 from it while something stands on it. A structure can stock one from a loot table (`LootTable`, rolled once when it is first loaded - one stack, so give it a table that yields one) or name its item outright (`Item`); and `Key`, an item id, makes the comparator answer only to that item, so placing the right relic can open a door. Seven Polished Andesite.
- **Dragon Tapestry** and **Worn Dragon Tapestry** — a great woven wall hanging, four blocks wide and three tall, on a dark oak rod: a red dragon breathing fire on a castle under a crescent moon, a knight with his lance raised on the hill below, all inside a woven ochre border with a tasselled fringe. The rod rests against the wall and the cloth hangs just in front of it. The worn one has hung too long in a dungeon — faded, grimed toward the bottom, holes worn through the sky and border and half its tassels gone, though the scene itself is whole. Place it against a wall: the block you click is the top row, second from the left, and the rest hangs down and out from there — every part needs its space and a wall behind it. Take down any part and the whole tapestry comes down, as one item. You walk through it. Wool on a rod of Sticks with Red Dye; the worn one is a tapestry with Coarse Dirt. The art is placeholder for now.
- **Hunt Tapestry** and **Worn Hunt Tapestry** — the same great hanging with the medieval subject: a rider sounding his horn, two hounds and a leaping stag between two trees, on a dark green field scattered with small flowers. Wool on a rod of Sticks with Green Dye.
- **Necromancer Tapestry** and **Summoning Tapestry**, each with a **Worn** version — two darker hangings for the villain's hall. The Necromancer: a tall robed figure in a peaked hood, its face skull-pale and its eyes burning, stretches a clawed hand over a graveyard, a horned skull aflame on its staff; the dead rise in green mist before it — one standing, one half out of the earth, a hand breaking the soil — under a pale moon in torn cloud, a ruined chapel on the horizon. The Summoning: a horned red demon with bat wings rises out of the flames at the heart of a glowing circle, hooded cultists kneeling either side with candles, on a field of black and embers. Wool on a rod of Sticks with Purple Dye, and with Black Dye.
- **Crumbling floors**, in Stone Brick, Mossy Stone Brick, Cracked Stone Brick, Cobblestone, Mossy Cobblestone, Polished Andesite, Deepslate Brick, Deepslate Tile and Mud Brick — a floor that gives way. It is its stone with a few faint hairline cracks, there for a sharp-eyed player to spot. Step on it, player or mob, and it shudders, the cracks open, dust sifts down from underneath, and a second later it crumbles away with whoever was standing on it. The collapse spreads through every crumbling floor block touching it, so a whole patch drops out in a wave from where it was trodden on. A redstone signal sets it off too. Mined by hand it drops itself; crumbling, it drops nothing. Its stone with Gravel.
- **Rubble Scatter** — loose chips of stone strewn across a floor, turned a different way on each block so a floor of it doesn't repeat. For a collapsed passage or a ruin's floor. Lay it on as you would pink petals: one placed is a few chips, and each more you use on it adds more, up to four; breaking it gives back one for each. Four from a block of Rubble.
- **Manacles**, **Meat Hook** and **Censer** — iron things to hang up. Like a lantern, each hangs beneath a vanilla Chain or straight from a ceiling. On a Swinging Chain, right-click the chain's bottom link with one to hang it there instead, and it swings with the chain; right-click with an empty hand to take it off. The Censer is an iron incense burner: light it with a torch or flint and steel and it glows and trails smoke, and on a swinging chain the smoke follows it as it swings. An empty hand or water puts it out. Manacles are two iron cuffs on a ring, light enough that they swing like the bare chain. Manacles: a Chain over two Iron Ingots. Meat Hook: an Iron Ingot and two Iron Nuggets. Censer: a Chain over Coal in a bowl of Iron Ingots.
- **Bubbling Cauldron** — a cauldron brimming with a brew at a rolling boil, for a witch's hut, an alchemist's cellar or a necromancer's lair. The surface churns and bubbles, bubbles in the brew's colour rise off it and burst, and steam drifts up from the rim. It comes in six brews — **green** (the default), **purple**, **red**, **blue**, **yellow** and **black**: right-click with the matching dye to change it. A structure can set the brew with the `color` blockstate. It is a prop, not a working cauldron: it takes no water, brews nothing and never empties. Mine it with a pickaxe, as a cauldron. Crafted from a Cauldron, a Water Bucket and Nether Wart.
- **Tarnished**, **Rusted** and **Corroded Dark Iron Grates** and **Heavy Trapdoors** — three rust stages for each, in the spirit of the copper grates' four levels of oxidization. They are separate blocks rather than a weathering chain, since iron does not age in vanilla: place whichever stage the build calls for. Same properties as the plain dark iron versions; creative-only for now.
- **Chiseled Deepslate Bricks** and **Mossy Chiseled Deepslate Bricks** — vanilla's chiseled *stone brick* carving in the deepslate brick palette. Not a reskin of vanilla's Chiseled Deepslate, which carries a different carving on a notably darker stone: this one belongs to the deepslate *brick* family and sits at that family's brightness, so it can be set into a deepslate brick wall as a feature block without reading as a different material.
- **Mossy Square Deepslate Brick** (with **Stairs**, a **Slab**, a **Facade** and a **Quarter Facade**) and **Mossy Left / Right Large Deepslate Brick** — mossy variants of all three. This is the only square brick set whose facades come in mossy as well as plain. Vanilla has no mossy deepslate brick to copy properties from, so they take theirs from Deepslate Bricks. The moss is the same green the stone and mud bricks carry, not a darkened version of it — against deepslate it reads as living growth on near-black rock rather than as grime.
- **Mossy Square Stone Brick Stairs** — completes the Square Stone Brick family. The mossy full block and the plain stairs both already existed; the mossy stairs did not, so a run of square stone brick steps had no aged form to weather into.
- **The Brazier has an `embers` state**: glowing coals with no flame, emitting light level 3. Mobs need block light 0 to spawn and block light drops by 1 per block, so a brazier at the usual light 15 sterilises an entire small dungeon room. At light 3 the brazier still reads as hot while most of a room's floor stays spawnable. The coals are drawn full-bright so they are visible in an otherwise dark room. The full range is now `fire=none` (0), `fire=embers` (3), `fire=soul` (10) and `fire=lit` (15), and the default is `none`.

### ⚙️ Changed

- **Sills and Double Sills are remodelled with true slopes**, in every material. The sloped top used to be a block tilted at an angle, which stretched its texture and left a notch where the slope met the flat top; it is now one clean sloped face whose brick courses stay level and run on unbroken into the flat top. Both sit on the whole block's footprint with 12px walls and a gentle slope, rising 4px over 8: the Sill from its front wall up to its full-height back half, the Double Sill from both walls up to a low ridge along the top centre. Their hitbox and collision are now a full block, so they are as easy to stand on, target and build against as any cube.
- **Arrow Slit openings are twice as wide**, 4px instead of 2px, in every material. At 2px the slit read as a crack in the wall rather than an opening.
- The DungeonBlocks creative tab's icon is now the **Undead Pennant** instead of vanilla Mossy Stone Bricks, so the tab stands out among other mods' tabs.
- Ceramic Pot items render as 3D models in the inventory, in hand, on the ground and in item frames, the way block items do, instead of as flat sprites.

### 🛠️ Fixed

- **The stonecutter recipes for the decorative stone pieces are back.** Every Sill, Double Sill, Facade, Quarter Facade, Fluted block, Fluted Facade, Cornice, Crown Molding, Pillar and Pillar Base could be cut from its stone on Forge, but the NeoForge 2.3 releases shipped without those 370 recipes, and without the Mossy Chiseled Stone Bricks recipe. They are all included again.
- Chiseled Polished Blackstone can be cut into a Chiseled Polished Blackstone Facade. Its stonecutter recipe named a block that does not exist, so it never loaded.
- **Hay bales can be crafted from wheat again.** The recipe that turns Hay Patches back into a Hay Bale was saved under vanilla's own recipe name, which replaced vanilla's nine-wheat recipe for as long as the mod was installed.
- **Dirty Hay Patch has a new texture** — loose, trodden straw with the floor showing through, instead of a darkened hay bale top.
- **Doors no longer drop twice.** Breaking any of the mod's two-block doors — the Copper Doors in every stage, waxed or not, and the Spruce, Dark Oak, Crimson and Mangrove Dungeon Doors — dropped two doors, one for each half. Only the lower half drops now, as with vanilla doors.
- **Copper grates, heavy grates, valve wheels, trapdoors, heavy trapdoors and plate brackets drop themselves again.** All 64, in every oxidization stage and waxed, required the correct tool for drops but belonged to no tool tag, so nothing could mine them for a drop. They now need a stone pickaxe or better, like vanilla copper. Copper Doors and the Copper Sewer Block were never affected, but now also mine faster with a pickaxe.
- Dark Iron Grates and Dark Iron Heavy Trapdoors now mine faster with a pickaxe. They belonged to no tool tag, so a pickaxe broke them no quicker than a bare hand. They still drop to any tool, as before.
- **Blocks placed into water now keep the water.** Sills, Double Sills, Corbels, Facades, Quarter Facades, Fluted Facades, Cornices, Crown Moldings, Ledges, Barred Windows, Pillars, Pillar Bases, Heavy Grates, Plate Brackets (straight, corner and angle), Valve Wheels and Wall Rings can all hold water. But placing one into water pushed the water out and left a dry pocket, and it held water only if you poured a bucket on it afterwards. They now stay waterlogged when placed in water, as vanilla stairs and slabs do.
- **The Dungeon Lantern drops itself again.** Like a vanilla lantern it needs a pickaxe to drop, but it belonged to no tool tag, so nothing counted as the right tool and breaking one gave you nothing. Any pickaxe now works, as with a vanilla lantern.
- The Brazier, Candle Sconce, Torch Sconce and Wall Ring now mine faster with a pickaxe. They belonged to no tool tag, so a pickaxe broke them no quicker than a bare hand. They still drop to any tool, as before.
- **Arrow Slit blocks drop themselves again.** 30 of the 39 Arrow Slits belonged to no tool tag while still requiring the correct tool for drops, so they could never be mined for a drop by anything. Only the ones whose material name happened to contain a block-type keyword ("brick", "square") were tagged — meaning the Stone, Cobblestone, Granite, Diorite, Andesite, Blackstone, Sandstone, Tuff, Obsidian and most Deepslate variants were all affected.
- East- and west-facing corner pieces on Ledge, Facade, Fluted Facade, Quarter Facade, Cornice, and Crown Molding blocks now render at the correct rotation and have the correct collision box. Previously the two corner variants of an east- or west-facing run were drawn identically, and both fell back to a south-facing corner's collision box.
- Corner pieces inside a rotated structure keep their shape. A dungeon room placed at a 90 or 270 degree rotation used to come out with its corner pieces turned the wrong way.
- Corner pieces are now handled correctly in mirrored structure placements, which previously left the piece untouched.
- The collision box of a Crown Molding outer corner now matches its model; its lower moulding was two pixels shallower than it looked.
- Facade, Quarter Facade, Fluted Facade, Sill and Double Sill blocks (straight, inner corner and outer corner, in every material) no longer render almost black when placed against full blocks. These models reach the edges of their own block, so ambient occlusion treated the neighbouring full block as shadowing them. The models had asked for ambient occlusion to be turned off for some time, but the request was being silently discarded: Minecraft reads that setting from the *root* of a model's parent chain, and these models inherited from a vanilla model that leaves it switched on. They now inherit from a model that turns it off.
- Faces of Facade, Quarter Facade and Fluted Facade blocks that sit flush against a neighbouring full block are no longer drawn at all. Previously they were still rendered, taking their light level from inside the solid block next door and fighting with that block's own face for the same pixels, which showed as a black flickering seam.
- The tops of the candles on a Candle Sconce are no longer black. Every sconce except a north-facing one had its candle tops mapped onto an empty region of the candle texture, so they drew as solid black. North-facing sconces were the only ones unaffected.
- Two-candle and three-candle Candle Sconce models were also missing their transparency setting, so see-through parts of the candle texture were drawn as solid black. The one-candle model was already correct.
- The top face of each candle on a Candle Sconce now shows the wax top of the candle rather than a slice of the candle's side.
- The third arm of the Candle Sconce sat almost entirely outside its own block, roughly three pixels into the neighbouring block. It is now positioned as a true mirror of the first arm, so the three arms are evenly spaced.
- 265 block models across 20 base models no longer render one or more faces as a magenta/black missing-texture checkerboard. Affected: every Crown Molding piece, Corbel, Barred Window Facade, Ledge inner corners, five Dungeon Door pieces, three Heavy Trapdoor pieces, two Sewer pieces, Wall Ring, Torch Sconce, and all three Braziers. The models referenced a texture variable nothing in their parent chain ever declared.
- Pillar Base blocks now render facing the direction they were placed against (previously always rendered in the default orientation, though the collision box rotated correctly).
- The side of a Sill block no longer shows a seam down its middle. The sill is built from two halves and both were drawing the same half of the texture, so the pattern restarted midway along the block. The underside had the same fault. Cornice, Facade and Quarter Facade inner corners were affected in the same way.
- Angle Cobwebs break quickly again. A sword now cuts one in 8 ticks, the same as a vanilla cobweb, and anything else takes 12. They previously took a full 20 seconds to break with any tool and then dropped nothing at all: they took their hardness and their "requires the correct tool" flag from the vanilla cobweb, but nothing counts as the correct tool for them, because vanilla only grants a sword its speed and its harvesting ability on the vanilla cobweb block specifically.
- A Crown Molding outer corner now blends with the straight pieces either side of it. Its top surface was taking its colours from the corner of the texture, where many materials keep a lighter edge, while its neighbours took theirs from the middle — so the corner read as a brighter patch rather than as a continuation of the run.
- Dungeon Lanterns and Sconces placed by a structure, a datapack or a command are no longer waterlogged. Their default state had every on/off property switched on, so anything that started from the default and did not explicitly say otherwise got a lantern that was hanging and full of water, and the water poured out across the floor around it. Lanterns and sconces placed by hand were never affected.

### Known limitations

- Swinging chains must hang from a ceiling or from another chain, and cannot be placed in mid-air.
- Ceramic Pots drop nothing by default; the loot plumbing is in place but the shipped loot tables are intentionally empty.
- Tall doors have no crafting recipe yet (obtainable via creative/give only).
- Square Stone Brick and Square Mud Brick stairs and facades have no crafting recipe yet (obtainable via creative/give only).
- Polished Andesite Brick blocks and stairs, in both plain and mossy, have no crafting recipe yet (obtainable via creative/give only), and neither do Mossy Square Stone Brick Stairs.

## [2.3.1] - 2026-07-12

### 🛠️ Fixed

- Pillar Base blocks now render facing the direction they were placed against (previously always rendered in the default orientation, though the collision box rotated correctly).

## [2.3.0] - 2025-07-07

### ⚙️ Changed

- now use DataGen for Cornice, Crown Molding, Facade, Pillar, Pillar Base, and Quarter Facade blocks, items, and loot tables.
- fixed weathering of all Copper variant blocks.
- remodeled Plate Bracket block slightly.
- uses GottschCore 2.6.0 (required)
- stone block families are now registered from a single data-driven material table, making new material variants a one-line addition (internal; no gameplay change).
- normalized stone-family block properties (hardness, blast resistance, sound, and tool requirement) to match their base vanilla material.
- copper Door and Trapdoor blockstates and models are now generated via DataGen (internal; no gameplay change).

### Added

- Angle Plate Bracket block
- Corner Plate Bracket block
- Copper variants for Plate, Angle Plate, and Corner Plate Bracket blocks.
- Tuff variants for all stone block types (Facade, Quarter Facade, Fluted, Fluted Facade, Sill, Double Sill, Cornice, Crown Molding, Pillar, and Pillar Base).
- Iron Angle Plate Bracket block
- Iron Corner Plate Bracket block
- Roots block (weeping-vines style hanging plant)
- Arrow Slit block for all stone material types

### 🛠️ Fixed

- Valve Wheels, Greek Blocks, and the Square/Large/Cobblestone/Gravel Brick blocks now drop when mined (they previously had no loot table and dropped nothing).
- Waxed Oxidized Copper Grate now uses the correct oxidized map color.
- corrected Stripped Cherry, Stripped Dark Oak, and Stripped Jungle Corbel textures and stonecutting recipes.
- Dark Iron Angle Plate Bracket now shows the correct (angle) model as its inventory icon instead of the corner model.
- Corner Plate Bracket and Valve Wheel blocks no longer render a missing-texture (magenta/black) speck on a small face (model referenced an undefined texture slot).
- Creative tab icon no longer appears jagged (now uses the Mossy Stone Bricks block instead of the non-square logo texture).

## [2.2.0] - 2025-05-05

### ⚙️ Changed

- now use DataGen for Double Sill, Sill, Fluted, and Fluted Facade blocks, items, and loot tables.
- rename Grate block to Heavy Grate.
- rename Grate Trapdoor block to Heavy Trapdoor.

### Added

- Square Brick block
- Mossy Square Brick block
- Square Stone Brick block
- Mossy Square Stone Brick block
- Left & Right Large Brick block
- Left & Right Large Stone Brick block
- Mossy Bricks block
- Large Bricks block
- Mossy Large Bricks block
- Copper Door variants (from 1.21)
- Copper Grate variants (from 1.21)
- Copper Trapdoor variants (from 1.21)
- Copper Heavy Grate variants
- Copper Heavy Trapdoor variants

## [2.1.0] - 2023-12-24

### ⚙️ Changed

- Fixed tool requirements for blocks

## [2.0.0] - 2023-12-15

### ⚙️ Changed

- Removed all variants of wall sconce
- Replaced wall sconce's torches with candles
- Updated grate to be a full block
- Fixed Cornice texture positioning
- All new blocks assets and data files are generated. Some older files were ported to generation.

### Added

- Grate Trapdoor (with variant)
- Barred window (with variants)
- Barred window facade (with variants)
- Torch sconce
- Brazier
- Sewer block (with variant)
- Dungeon Lantern (lit/unlit)
- Dungeon Door (with variants)
- Hay Patch (with variant)
- Wall Ring
- Pattern block (greek-esque)
- Corbel (with variants)
- Ledge (with variants)
- Plate Bracket

## [1.2.0] - 2023-10-24

### ⚙️ Changed

- Port from 1.19.3-1.2.0