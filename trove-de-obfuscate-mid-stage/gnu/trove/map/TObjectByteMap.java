package gnu.trove.map;

public interface TObjectByteMap {
   byte getNoEntryValue();

   int size();

   boolean containsKey(Object var1);

   byte get(Object var1);

   boolean forEachEntry(gnu.trove.procedure.TObjectByteProcedure var1);
}
