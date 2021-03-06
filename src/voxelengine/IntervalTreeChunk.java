package voxelengine;

public class IntervalTreeChunk extends Chunk<IntervalTreeChunk.IntervalTreeNode> {

    static final short CHUNK_VOLUME = Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE;
    private IntervalTreeNode intervalTree;

    IntervalTreeChunk(World world, TerrainGenerator generator, Chunk[] neighbors) {
        super(world, generator, neighbors);
    }

    void initializeChunk(TerrainGenerator generator) {
        intervalTree = new IntervalTreeNode(new Block(Block.BlockType.EMPTY), (short)0, (short)(CHUNK_VOLUME - 1));
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    set(x, y, z, generator.getBlock(x, y, z));
                }
            }
        }
    }

    @Override
    void optimize() {
        // TODO implement
    }

    void set(short x, short y, short z, Block block) {
        intervalTree.setBlock((byte)x, (byte)y, (byte)z, block);
    }

    Block get(short x, short y, short z) {  //TODO Change type to BYTE (instead of INT)
        return intervalTree.getBlock((byte)x, (byte)y, (byte)z);
    }

    //TODO In welche Richtung die Intervalle durchlaufen?
    //Aktuelle Annahme yzx
    Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2) {
        Block[][][] interval = new Block[y2-y1][z2-z1][x2-x1];
       // Block[][][] interval = new Block[x2-x1][y2-y1][z2-z1];
        Block emptyBlock = new Block(Block.BlockType.GLASS);
        short tmp;
        for (short y = 0; y < y2-y1; y++) {
            for (short z = 0; z < z2-z1; z++) {
                tmp = (short) (CHUNK_SIZE*(z* Chunk.CHUNK_SIZE + y));
                Block[] result = new Block[x2-x1];
                for (int i = 0; i < result.length; i++) {
                    result[i] = emptyBlock;
                }
                result = intervalTree.getInterval((short)(tmp + x1), (short)(tmp + x2 - 1), (short) 0, (short)(x2-x1-1), result);
                interval[y][z] = result;
/*
                for (short x = 0; x < x2-x1; x++) {
                    interval[x][y][z] = intervalTree.getBlock(x, y, z);
                } */
            }
        }
        return interval;
    }

    IntervalTreeNode getParentOfCoord (short coord) {
        IntervalTreeNode parent = intervalTree;
        boolean reachedCoord = false;

        while (!reachedCoord) {
            if ((parent.getLeftNode().getStart() <= coord && parent.getLeftNode().getEnd() >= coord) || (parent.getRightNode().getStart() <= coord && parent.getRightNode().getEnd() >= coord))
                reachedCoord = true;
            else if (coord < parent.getStart())
                parent = parent.getLeftNode();
            else
                parent = parent.getRightNode();
        }

        return parent;
    }

    public String toString() {
        return "";
    }



    class IntervalTreeNode {

        private IntervalTreeNode leftNode;
        private IntervalTreeNode rightNode;
        private short startPoint;
        private short endPoint;
        private Block block;

        IntervalTreeNode(Block overlapping, short start, short length) {
            startPoint = start;
            endPoint = (short)(start + length);
            block = overlapping;
        }

        short getStart() {
            return startPoint;
        }

        short getEnd() {
            return endPoint;
        }

        boolean isLeaf() {
            return (leftNode == null) && (rightNode == null);
        }

        void setLeftNode(IntervalTreeNode node) {
            this.leftNode = node;
        }

        void setRightNode(IntervalTreeNode node) {
            this.rightNode = node;
        }

        IntervalTreeNode getLeftNode() {
            return leftNode;
        }

        IntervalTreeNode getRightNode() {
            return rightNode;
        }

        Block getBlock(short x, short y, short z) {
            short onedimPoint = (short) ((z* Chunk.CHUNK_SIZE + y) * Chunk.CHUNK_SIZE + x);

            if (onedimPoint >= startPoint && onedimPoint <= endPoint)
                return block;
            else if (onedimPoint > endPoint)
                return rightNode.getBlock(x, y, z);
            else // if (onedimPoint < startPoint)
                return leftNode.getBlock(x, y, z);
        }

        Block getBlock(short onedimPoint) {
            if (onedimPoint >= startPoint && onedimPoint <= endPoint)
                return block;
            else if (onedimPoint > endPoint && rightNode != null)
                return rightNode.getBlock(onedimPoint);
            else if (onedimPoint < startPoint && leftNode != null)
                return leftNode.getBlock(onedimPoint);

            return null;
        }

        Block getBlock() {
            return this.block;
        }

        void reduceIntervalLeft() {

        }

        void reduceIntervalRight() {

        }

        void extendIntervalLeft(short intervalCoord) {

        }

        void extendIntervalRight(short intervalCoord) {

        }

        IntervalTreeNode getNodeAtCoord(short coord) {
            return null;
        }

        boolean isParentOf(IntervalTreeNode node) {
            return this.leftNode == node || this.rightNode == node;
        }

        int isChildOf(IntervalTreeNode node) {
            int childOf = -1;
            if (node.getLeftNode() == this)
                childOf = 1;
            if (node.getRightNode() == this)
                childOf = 2;

            return childOf;
        }

        private Block[] getInterval (short start, short end, short startArray, short endArray, Block[] interval) {
//TODO experiment with startArray and endArray
            if (start >= startPoint && end <= endPoint) {
                for (int i = startArray; i < endArray; i++) {
                    interval[i] = this.block;
                }
                return interval;
            }

            if (start < startPoint && end > endPoint) {
                for (int i = startArray + startPoint - start; i <= ((endPoint - start + startArray < endArray)? endPoint - start + startArray : endArray); i++) {
                    interval[i] = this.block;
                }
                interval = leftNode.getInterval(start, (short)(startPoint-1), startArray, (short)(endPoint - start + 1 + startArray), interval);
                interval = rightNode.getInterval((short)(endPoint+1), end, (short)(endPoint - start + startArray + 1), endArray, interval);
                return interval;
            }

            if (start < startPoint && end >= startPoint && end <= endPoint) {
                for (int i = startArray + startPoint - start; i <= endArray; i++) {
                    interval[i] = this.block;
                }
                interval = leftNode.getInterval(start, (short)(startPoint-1), startArray, (short)(startPoint - start + startArray), interval);
                return interval;
            }

            if (start >= startPoint && start <= endPoint && end > endPoint) {
                for (int i = startArray; i <= ((endPoint - start + startArray < endArray)? endPoint - start + startArray : endArray); i++) {
                    interval[i] = this.block;
                }
                interval = rightNode.getInterval((short)(endPoint+1), end, (short)(endPoint - start + startArray + 1), endArray, interval);
                return interval;
            }

            if (start > endPoint)
                return rightNode.getInterval(start, end, startArray, endArray, interval);

            if (end < startPoint)
                return leftNode.getInterval(start, end, startArray, endArray, interval);

            return interval;
        }

        void setBlock(short x, short y, short z, Block block) {
//            this.block = new Block(0);

            Block.BlockType type = block.getType();
            short onedimcoord = (short) (Chunk.CHUNK_SIZE * (z* Chunk.CHUNK_SIZE + y) + x);

            if (onedimcoord < endPoint && onedimcoord > startPoint) {
                if (type == this.block.getType()) {
                    return;
                } else {
                    IntervalTreeNode newLeftNode = new IntervalTreeNode(this.block, startPoint, (short) (onedimcoord - startPoint - 1));
                    IntervalTreeNode newRightNode = new IntervalTreeNode(this.block, (short) (onedimcoord + 1), (short) (endPoint - onedimcoord + 1));
                    newLeftNode.setLeftNode(this.leftNode);
                    newRightNode.setRightNode(this.rightNode);
                    this.startPoint = onedimcoord;
                    this.endPoint = onedimcoord;
                    this.block = block;
                    this.leftNode = newLeftNode;
                    this.rightNode = newRightNode;
                }
            } else if (onedimcoord == startPoint && onedimcoord == endPoint){
                IntervalTreeNode leftNode = getNodeAtCoord((short)(onedimcoord - 1));
                IntervalTreeNode rightNode = getNodeAtCoord((short)(onedimcoord - 1));

                if (type == this.block.getType()) {
                    return;
                } else if (leftNode != null && rightNode != null &&
                        (leftNode.isParentOf(this) || leftNode.isChildOf(this) > 0) &&
                        (rightNode.isParentOf(this) || rightNode.isChildOf(this) > 0) &&
                        leftNode.getBlock().getType() == type && rightNode.getBlock().getType() == type) {
                    IntervalTreeNode newNode = new IntervalTreeNode (block, leftNode.getStart(), (short)(rightNode.getEnd() - leftNode.getStart()));
                    newNode.setLeftNode(leftNode.getLeftNode());
                    newNode.setRightNode(rightNode.getRightNode());
                    if (leftNode.isParentOf(this))
                        if (getParentOfCoord(leftNode.getStart()).getRightNode() == leftNode)
                            getParentOfCoord(leftNode.getStart()).setRightNode(newNode);
                        if (getParentOfCoord(leftNode.getStart()).getLeftNode() == leftNode)
                            getParentOfCoord(leftNode.getStart()).setLeftNode(newNode);
                    else if (rightNode.isParentOf(this)) {
                            if (getParentOfCoord(rightNode.getStart()).getRightNode() == leftNode)
                                getParentOfCoord(rightNode.getStart()).setRightNode(newNode);
                            if (getParentOfCoord(rightNode.getStart()).getLeftNode() == leftNode)
                                getParentOfCoord(rightNode.getStart()).setLeftNode(newNode);
                        }
                    else if (getParentOfCoord(this.startPoint).getLeftNode() == this)
                        getParentOfCoord(this.startPoint).setLeftNode(newNode);
                        else
                            getParentOfCoord(this.startPoint).setRightNode(newNode);
                } else if (leftNode != null &&
                        (leftNode.isParentOf(this) || leftNode.isChildOf(this) > 0) &&
                        leftNode.getBlock().getType() == type) {
                    IntervalTreeNode newNode = new IntervalTreeNode(block, leftNode.getStart(), (short)(onedimcoord - leftNode.getStart()));
                    if (leftNode.isParentOf(this)) {
                        newNode.setLeftNode(leftNode.getLeftNode());
                        newNode.setRightNode(this.rightNode);
                        if (getParentOfCoord(leftNode.getStart()).getRightNode() == leftNode)
                            getParentOfCoord(leftNode.getStart()).setRightNode(newNode);
                        if (getParentOfCoord(leftNode.getStart()).getLeftNode() == leftNode)
                            getParentOfCoord(leftNode.getStart()).setLeftNode(newNode);
                    } else {
                        newNode.setLeftNode(leftNode.getLeftNode());
                        newNode.setRightNode(this.rightNode);
                        if (getParentOfCoord(this.startPoint).getRightNode() == leftNode)
                            getParentOfCoord(this.startPoint).setRightNode(newNode);
                        if (getParentOfCoord(this.startPoint).getLeftNode() == leftNode)
                            getParentOfCoord(this.startPoint).setLeftNode(newNode);
                    }
                } else if (rightNode != null &&
                        (rightNode.isParentOf(this) || rightNode.isChildOf(this) > 0) &&
                        rightNode.getBlock().getType() == type) {
                    IntervalTreeNode newNode = new IntervalTreeNode(block, onedimcoord, (short)(rightNode.getEnd() - onedimcoord));
                    if (rightNode.isParentOf(this)) {
                        newNode.setRightNode(rightNode.getRightNode());
                        newNode.setLeftNode(this.leftNode);
                        if (getParentOfCoord(rightNode.getStart()).getRightNode() == rightNode)
                            getParentOfCoord(rightNode.getStart()).setRightNode(newNode);
                        if (getParentOfCoord(rightNode.getStart()).getLeftNode() == rightNode)
                            getParentOfCoord(rightNode.getStart()).setLeftNode(newNode);
                    } else {
                        newNode.setLeftNode(this.leftNode);
                        newNode.setRightNode(rightNode.getRightNode());
                        if (getParentOfCoord(this.startPoint).getRightNode() == leftNode)
                            getParentOfCoord(this.startPoint).setRightNode(newNode);
                        if (getParentOfCoord(this.startPoint).getLeftNode() == leftNode)
                            getParentOfCoord(this.startPoint).setLeftNode(newNode);
                    }
                } else {
                    this.block = block;
                }
            } else if (onedimcoord == startPoint) {
                if (type == this.block.getType())
                    return;
                else if (onedimcoord > 0 && getBlock((short)(onedimcoord-1)) != null && type == getBlock((short)(onedimcoord-1)).getType()){
                    this.reduceIntervalLeft();
                    extendIntervalRight((short)(onedimcoord-1));
                } else {
                    IntervalTreeNode newNode = new IntervalTreeNode(this.block, (short)(startPoint + 1), (short)(endPoint - startPoint - 1));
                    newNode.setRightNode(this.rightNode);
                    this.endPoint = this.startPoint;
                    this.block = block;
                    this.rightNode = newNode;
                }
            } else if (onedimcoord == endPoint) {
                if (type == this.block.getType())
                    return;
                else if (getBlock((short)(onedimcoord + 1)) != null &&
                        type == getBlock((short)(onedimcoord + 1)).getType()) {
                    this.reduceIntervalRight();
                    extendIntervalLeft((short)(onedimcoord + 1));
                } else {
                    IntervalTreeNode newNode = new IntervalTreeNode(this.block, startPoint, (short)(endPoint - startPoint - 1));
                    newNode.setLeftNode(this.leftNode);
                    this.startPoint = this.endPoint;
                    this.block = block;
                    this.leftNode = newNode;
                }
            } else if (onedimcoord < startPoint) {
                this.leftNode.setBlock(x, y, z, block);
            } else {
                this.rightNode.setBlock(x, y, z, block);
            }
        }
    }
}