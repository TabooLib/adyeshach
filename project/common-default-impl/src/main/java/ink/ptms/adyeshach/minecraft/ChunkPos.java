package ink.ptms.adyeshach.minecraft;

/**
 * @author 坏黑
 * @since 2022/6/28 14:33
 */
public class ChunkPos {

    public static long asLong(int chunkX, int chunkZ) {
        return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
    }

    public static int getX(long pos) {
        return (int) (pos & 4294967295L);
    }

    public static int getZ(long pos) {
        return (int) (pos >>> 32 & 4294967295L);
    }
}
