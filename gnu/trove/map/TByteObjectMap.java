package gnu.trove.map;

public interface TByteObjectMap {
   byte getNoEntryKey();

   int size();

   boolean containsKey(byte var1);

   Object get(byte var1);

   boolean forEachEntry(gnu.trove.procedure.TByteObjectProcedure var1);
}
