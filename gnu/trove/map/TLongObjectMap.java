package gnu.trove.map;

public interface TLongObjectMap {
   long getNoEntryKey();

   int size();

   boolean containsKey(long var1);

   Object get(long var1);

   boolean forEachEntry(gnu.trove.procedure.TLongObjectProcedure var1);
}
