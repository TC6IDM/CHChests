struct_name = input("Structure Name: ")
blockpositions=[]
nextpos = ""
while nextpos != "done":
    nextpos = input("Block Position: ")
    blockpos = nextpos.split (" ")
    if nextpos != "done":
        blockpositions.append({'x': int(blockpos[0]), 'y': int(blockpos[1]), 'z': int(blockpos[2]), 'nextblock': blockpos[3]})

chestpositions=[]
nextpos = ""
while nextpos != "done":
    nextpos = input("Chest Position: ")
    blockpos = nextpos.split (" ")
    if nextpos != "done":
        chestpositions.append({'x': int(blockpos[0]), 'y': int(blockpos[1]), 'z': int(blockpos[2])})


fulltext = f"//{struct_name} {blockpositions[0]['x']} {blockpositions[0]['y']} {blockpositions[0]['z']}\n\
            if (world.getBlockState(pos).getBlock() == Blocks.{blockpositions[0]['nextblock']} &&\n"

debugtext = f"/setblock {blockpositions[0]['x']} {blockpositions[0]['y']} {blockpositions[0]['z']} minecraft:{blockpositions[0]['nextblock']}\n"

start = blockpositions[0]

blockpositions.pop(0)

for block in blockpositions:
    fulltext += f"world.getBlockState(new BlockPos(x + {block['x'] - start['x']}, y + {block['y'] - start['y']}, z + {block['z'] - start['z']})).getBlock() == Blocks.{block['nextblock']} &&\n"
    debugtext += f"/setblock {block['x']} {block['y']} {block['z']} minecraft:{block['nextblock']}\n"

fulltext = fulltext[:-4] + "){\n"

for chest in chestpositions:
    chestinstructure = f'"{struct_name} {chestpositions.index(chest) + 1}"'
    fulltext += f"BlockPos pos{chestpositions.index(chest) + 1} = new BlockPos(x + {chest['x'] - start['x']}, y + {chest['y'] - start['y']}, z + {chest['z'] - start['z']});\n\
        blockTextMap.put(pos{chestpositions.index(chest) + 1}, {chestinstructure});\n"
    debugtext += f"/setblock {chest['x']} {chest['y']} {chest['z']} minecraft:chest\n"

fulltext += "}"

print(fulltext)

print("\n\n\n\n")
print(debugtext)